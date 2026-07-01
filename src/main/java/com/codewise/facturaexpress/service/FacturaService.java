package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.*;
import com.codewise.facturaexpress.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// Servicio principal para la gestion de facturas: creacion, calculos, documentos y envio
@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final XmlService xmlService;
    private final PdfService pdfService;
    private final EmailService emailService;

    public FacturaService(FacturaRepository facturaRepository,
                          DetalleFacturaRepository detalleFacturaRepository,
                          ProductoRepository productoRepository,
                          ClienteRepository clienteRepository,
                          XmlService xmlService,
                          PdfService pdfService,
                          EmailService emailService) {
        this.facturaRepository = facturaRepository;
        this.detalleFacturaRepository = detalleFacturaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.xmlService = xmlService;
        this.pdfService = pdfService;
        this.emailService = emailService;
    }

    // Crea una factura sin descuento
    @Transactional
    public Factura crearFactura(Factura factura) {
        return crearFactura(factura, 0);
    }

    // Crea una factura validando cliente, detalles, stock, calculando subtotal, descuento, IVA y total
    @Transactional
    public Factura crearFactura(Factura factura, int descuentoPorcentaje) {
        if (factura.getClienteId() == null) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio");
        }
        if (factura.getDetalles() == null || factura.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La factura debe tener al menos un detalle");
        }

        if (descuentoPorcentaje < 0 || descuentoPorcentaje > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 0% y 100%");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (DetalleFactura detalle : factura.getDetalles()) {
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
            }
            BigDecimal linea = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(linea);
            subtotal = subtotal.add(linea);
            if (detalle.getDescuento() == null) {
                detalle.setDescuento(BigDecimal.ZERO);
            }

            Optional<Producto> productoOpt = productoRepository.findById(detalle.getProductoId());
            if (productoOpt.isEmpty()) {
                throw new IllegalArgumentException("Producto no encontrado con id: " + detalle.getProductoId());
            }
            Producto producto = productoOpt.get();
            if (producto.getStock() < detalle.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre()
                    + " (disponible: " + producto.getStock() + ", solicitado: " + detalle.getCantidad() + ")");
            }
        }

        BigDecimal descuento = BigDecimal.valueOf(descuentoPorcentaje)
                .multiply(subtotal).divide(BigDecimal.valueOf(100));
        BigDecimal base = subtotal.subtract(descuento);
        BigDecimal iva = base.multiply(BigDecimal.valueOf(0.19));
        BigDecimal total = base.add(iva);
        factura.setTotal(total);
        factura.setEstado("PENDIENTE");

        Factura creada = facturaRepository.save(factura);

        for (DetalleFactura detalle : factura.getDetalles()) {
            detalle.setFacturaId(creada.getId());
            detalleFacturaRepository.save(detalle);
            productoRepository.descontarStock(detalle.getProductoId(), detalle.getCantidad());
        }

        generarDocumentos(creada);

        return buscarPorId(creada.getId()).orElse(creada);
    }

    // Genera los documentos XML y PDF de la factura y envia por correo al cliente
    public void generarDocumentos(Factura factura) {
        try {
            String xml = xmlService.generarXmlFactura(factura);
            factura.setXml(xml);
        } catch (Exception e) {
            System.err.println("Error al generar XML para factura " + factura.getId() + ": " + e.getMessage());
        }

        try {
            byte[] pdf = pdfService.generarPdfFactura(factura);
            factura.setPdf(pdf);
        } catch (Exception e) {
            System.err.println("Error al generar PDF para factura " + factura.getId() + ": " + e.getMessage());
        }

        facturaRepository.save(factura);

        try {
            Optional<Cliente> clienteOpt = clienteRepository.findById(factura.getClienteId());
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                if (cliente.getEmail() != null && !cliente.getEmail().isBlank()) {
                    boolean enviado = emailService.enviarFactura(
                        cliente.getEmail(),
                        "Factura Electronica FAC-" + String.format("%05d", factura.getId()),
                        "<h2>Factura Electronica</h2><p>Adjunto encontrara su factura No. FAC-" + String.format("%05d", factura.getId()) + ".</p>",
                        factura.getPdf(),
                        "factura_" + factura.getId() + ".pdf"
                    );
                    if (enviado) {
                        factura.setCorreoEnviado(true);
                        facturaRepository.save(factura);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al enviar correo para factura " + factura.getId() + ": " + e.getMessage());
        }
    }

    // Busca una factura por ID incluyendo nombre del cliente y detalles
    public Optional<Factura> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        Optional<Factura> facturaOpt = facturaRepository.findByIdWithClienteNombre(id);
        facturaOpt.ifPresent(f -> {
            clienteRepository.findById(f.getClienteId())
                .ifPresent(c -> f.setClienteNombre(c.getNombre()));
            List<DetalleFactura> detalles = detalleFacturaRepository.findByFacturaIdWithProductoNombre(f.getId());
            f.setDetalles(detalles);
        });
        return facturaOpt;
    }

    // Obtiene todas las facturas con nombre de cliente y detalles
    public List<Factura> listarFacturas() {
        List<Factura> facturas = facturaRepository.findAllWithClienteNombre();
        for (Factura f : facturas) {
            clienteRepository.findById(f.getClienteId())
                .ifPresent(c -> f.setClienteNombre(c.getNombre()));
            List<DetalleFactura> detalles = detalleFacturaRepository.findByFacturaIdWithProductoNombre(f.getId());
            f.setDetalles(detalles);
        }
        return facturas;
    }

    // Actualiza el estado de una factura
    @Transactional
    public void actualizarEstado(Long id, String nuevoEstado) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con id: " + id));
        factura.setEstado(nuevoEstado.toUpperCase());
        facturaRepository.save(factura);
    }

    // Elimina una factura y sus detalles asociados
    @Transactional
    public void eliminarFactura(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        detalleFacturaRepository.deleteByFacturaId(id);
        facturaRepository.deleteById(id);
    }
}
