package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoDAO {

    /** Guarda un nuevo producto y asigna el ID generado. */
    Producto guardar(Producto producto);

    /** Busca un producto por su ID. Retorna Optional vacío si no existe. */
    Optional<Producto> buscarPorId(Long id);

    /** Retorna la lista completa de productos ordenados por ID. */
    List<Producto> listarTodos();

    /** Actualiza los datos de un producto existente. */
    Producto actualizar(Producto producto);

    /** Elimina un producto por su ID. */
    void eliminar(Long id);
}
