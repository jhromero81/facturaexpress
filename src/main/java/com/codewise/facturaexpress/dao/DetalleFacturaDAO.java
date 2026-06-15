package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.model.DetalleFactura;

import java.util.List;
import java.util.Optional;

public interface DetalleFacturaDAO {

    /** Guarda un nuevo detalle de factura y asigna el ID generado. */
    DetalleFactura guardar(DetalleFactura detalle);

    /** Busca un detalle por su ID, incluyendo el nombre del producto. */
    Optional<DetalleFactura> buscarPorId(Long id);

    /** Retorna todos los detalles asociados a una factura específica. */
    List<DetalleFactura> listarPorFacturaId(Long facturaId);

    /** Retorna la lista completa de detalles. */
    List<DetalleFactura> listarTodos();

    /** Actualiza cantidad, precio unitario y subtotal de un detalle. */
    DetalleFactura actualizar(DetalleFactura detalle);

    /** Elimina un detalle por su ID. */
    void eliminar(Long id);

    /** Elimina todos los detalles asociados a una factura. */
    void eliminarPorFacturaId(Long facturaId);
}
