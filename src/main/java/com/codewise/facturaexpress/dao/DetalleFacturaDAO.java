package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.model.DetalleFactura;

import java.util.List;
import java.util.Optional;

public interface DetalleFacturaDAO {

    DetalleFactura guardar(DetalleFactura detalle);

    Optional<DetalleFactura> buscarPorId(Long id);

    List<DetalleFactura> listarPorFacturaId(Long facturaId);

    List<DetalleFactura> listarTodos();

    DetalleFactura actualizar(DetalleFactura detalle);

    void eliminar(Long id);

    void eliminarPorFacturaId(Long facturaId);
}
