package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.*;
import com.codewise.facturaexpress.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// Servicio que gestiona el ciclo de vida de las facturas.
// Se encarga de validaciones, cálculo de totales, descuento de stock,
// generacion de documentos (XML/PDF), envío de correos y eliminacion logica.
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

    // Crea una factura sin descuento adicional
    @Transactional
    public Factura crearFactura(Factura factura) {
        return crearFactura(factura, 0);
    }

    // Crea una factura validando datos, calculando totales con descuento, descontando stock y generando documentos
    @Transactional
    public Factura crearFactura(Factura factura, int descuentoPorcentaje) {
        if (factura.getCliente() == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
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

            if (detalle.getProducto() == null) {
                throw new IllegalArgumentException("El producto es obligatorio en cada detalle");
            }
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con id: " + detalle.getProducto().getId()));
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

        for (DetalleFactura detalle : factura.getDetalles()) {
            factura.addDetalle(detalle);
        }

        Factura creada = facturaRepository.save(factura);

        for (DetalleFactura detalle : creada.getDetalles()) {
            productoRepository.descontarStock(detalle.getProducto().getId(), detalle.getCantidad());
        }

        generarDocumentos(creada);

        return buscarPorId(creada.getId()).orElse(creada);
    }

    // Genera XML, PDF y envia correo electronico al cliente
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
            Cliente cliente = factura.getCliente();
            if (cliente != null && cliente.getEmail() != null && !cliente.getEmail().isBlank()) {
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
        } catch (Exception e) {
            System.err.println("Error al enviar correo para factura " + factura.getId() + ": " + e.getMessage());
        }
    }

    // Busca una factura por ID incluyendo cliente y detalles con productos
    public Optional<Factura> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        Optional<Factura> facturaOpt = facturaRepository.findByIdWithCliente(id);
        facturaOpt.ifPresent(f -> {
            List<DetalleFactura> detalles = detalleFacturaRepository.findByFacturaIdWithProducto(f.getId());
            f.setDetalles(detalles);
        });
        return facturaOpt;
    }

    // Lista todas las facturas incluyendo cliente y detalles
    public List<Factura> listarFacturas() {
        List<Factura> facturas = facturaRepository.findAllWithCliente();
        for (Factura f : facturas) {
            List<DetalleFactura> detalles = detalleFacturaRepository.findByFacturaIdWithProducto(f.getId());
            f.setDetalles(detalles);
        }
        return facturas;
    }

    // Actualiza el estado de una factura existente
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

    // Elimina una factura limpiando sus detalles primero (orphanRemoval)
    @Transactional
    public void eliminarFactura(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con id: " + id));
        factura.getDetalles().clear();
        facturaRepository.deleteById(id);
    }
}
