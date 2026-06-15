package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.model.Factura;

import java.util.List;
import java.util.Optional;

public interface FacturaDAO {

    /** Guarda una nueva factura con sus detalles y asigna el ID generado. */
    Factura guardar(Factura factura);

    /** Busca una factura por su ID, incluyendo detalles y nombre del cliente. */
    Optional<Factura> buscarPorId(Long id);

    /** Retorna la lista completa de facturas ordenadas por ID. */
    List<Factura> listarTodos();

    /** Actualiza los datos de una factura existente. */
    Factura actualizar(Factura factura);

    /** Elimina una factura y sus detalles asociados. */
    void eliminar(Long id);
}
