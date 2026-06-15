package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteDAO {

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(Long id);

    List<Cliente> listarTodos();

    Cliente actualizar(Cliente cliente);

    void eliminar(Long id);
}
