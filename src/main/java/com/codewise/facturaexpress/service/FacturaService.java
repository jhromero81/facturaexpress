package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.dao.*;
import com.codewise.facturaexpress.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class FacturaService {

    private final FacturaDAO facturaDAO;
    private final ProductoDAO productoDAO;
    private final ClienteDAO clienteDAO;
    private final XmlService xmlService;
    private final PdfService pdfService;
    private final EmailService emailService;

    public FacturaService() {
        this.facturaDAO = new FacturaDAOImpl();
        this.productoDAO = new ProductoDAOImpl();
        this.clienteDAO = new ClienteDAOImpl();
        this.xmlService = new XmlService();
        this.pdfService = new PdfService();
        this.emailService = new EmailService();
    }

    public Factura crearFactura(Factura factura) {
        return crearFactura(factura, 0);
    }

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

            Optional<Producto> productoOpt = productoDAO.buscarPorId(detalle.getProductoId());
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
        Factura creada = facturaDAO.guardar(factura);

        try {
            for (DetalleFactura detalle : factura.getDetalles()) {
                productoDAO.descontarStock(detalle.getProductoId(), detalle.getCantidad());
            }
        } catch (Exception e) {
            facturaDAO.eliminar(creada.getId());
            throw new RuntimeException("Error al descontar stock, factura anulada: " + e.getMessage(), e);
        }

        generarDocumentos(creada);

        return facturaDAO.buscarPorId(creada.getId()).orElse(creada);
    }

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

        facturaDAO.actualizar(factura);

        try {
            Optional<Cliente> clienteOpt = clienteDAO.buscarPorId(factura.getClienteId());
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
                        factura.setCorreoEnviado(1);
                        facturaDAO.actualizar(factura);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al enviar correo para factura " + factura.getId() + ": " + e.getMessage());
        }
    }

    public Optional<Factura> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        return facturaDAO.buscarPorId(id);
    }

    public List<Factura> listarFacturas() {
        return facturaDAO.listarTodos();
    }

    public void actualizarEstado(Long id, String nuevoEstado) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        Factura factura = facturaDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con id: " + id));
        factura.setEstado(nuevoEstado.toUpperCase());
        facturaDAO.actualizar(factura);
    }

    public void eliminarFactura(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        facturaDAO.eliminar(id);
    }
}
