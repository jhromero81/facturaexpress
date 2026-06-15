package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoDAO {

    Producto guardar(Producto producto);

    Optional<Producto> buscarPorId(Long id);

    List<Producto> listarTodos();

    Producto actualizar(Producto producto);

    void eliminar(Long id);
}
