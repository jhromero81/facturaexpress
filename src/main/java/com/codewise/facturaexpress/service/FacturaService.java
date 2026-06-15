package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.dao.*;
import com.codewise.facturaexpress.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de facturas. Orquesta la creación, consulta,
 * actualización de estado y eliminación de facturas con sus detalles.
 */
public class FacturaService {

    private final FacturaDAO facturaDAO;
    private final DetalleFacturaDAO detalleFacturaDAO;

    public FacturaService() {
        this.facturaDAO = new FacturaDAOImpl();
        this.detalleFacturaDAO = new DetalleFacturaDAOImpl();
    }

    /**
     * Crea una factura completa: valida cliente y detalles, calcula
     * subtotales por línea y total general, asigna estado PENDIENTE
     * y persiste cabecera y detalles en una sola operación.
     */
    public Factura crearFactura(Factura factura) {
        if (factura.getClienteId() == null) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio");
        }
        if (factura.getDetalles() == null || factura.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La factura debe tener al menos un detalle");
        }

        BigDecimal total = BigDecimal.ZERO;
        // Calcula el subtotal de cada detalle y acumula el total general
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
        return facturaDAO.guardar(factura);
    }

    /**
     * Busca una factura por su ID.
     */
    public Optional<Factura> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        return facturaDAO.buscarPorId(id);
    }

    /**
     * Retorna la lista completa de facturas.
     */
    public List<Factura> listarFacturas() {
        return facturaDAO.listarTodos();
    }

    /**
     * Actualiza el estado de una factura. El nuevo estado se convierte
     * a mayúsculas automáticamente. Lanza excepción si no existe la factura.
     */
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

    /**
     * Elimina una factura por su ID.
     */
    public void eliminarFactura(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de factura invalido");
        }
        facturaDAO.eliminar(id);
    }
}
