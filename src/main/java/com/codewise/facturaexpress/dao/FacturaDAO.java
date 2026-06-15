package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.model.Factura;

import java.util.List;
import java.util.Optional;

public interface FacturaDAO {

    Factura guardar(Factura factura);

    Optional<Factura> buscarPorId(Long id);

    List<Factura> listarTodos();

    Factura actualizar(Factura factura);

    void eliminar(Long id);
}
