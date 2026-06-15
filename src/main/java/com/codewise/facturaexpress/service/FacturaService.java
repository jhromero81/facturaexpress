package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.dao.*;
import com.codewise.facturaexpress.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class FacturaService {

    private final FacturaDAO facturaDAO;
    private final ProductoDAO productoDAO;

    public FacturaService() {
        this.facturaDAO = new FacturaDAOImpl();
        this.productoDAO = new ProductoDAOImpl();
    }

    public Factura crearFactura(Factura factura) {
        if (factura.getClienteId() == null) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio");
        }
        if (factura.getDetalles() == null || factura.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La factura debe tener al menos un detalle");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleFactura detalle : factura.getDetalles()) {
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }
            if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
            }
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
            total = total.add(detalle.getSubtotal());
        }

        factura.setTotal(total);
        factura.setEstado("PENDIENTE");
        Factura creada = facturaDAO.guardar(factura);

        for (DetalleFactura detalle : creada.getDetalles()) {
            productoDAO.descontarStock(detalle.getProductoId(), detalle.getCantidad());
        }

        return creada;
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

    public Factura actualizarEstado(Long id, String nuevoEstado) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        Factura factura = facturaDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con id: " + id));
        factura.setEstado(nuevoEstado.toUpperCase());
        return facturaDAO.actualizar(factura);
    }

    public void eliminarFactura(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        facturaDAO.eliminar(id);
    }
}
