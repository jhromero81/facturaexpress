package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteDAO {

    /** Guarda un nuevo cliente y asigna el ID generado. */
    Cliente guardar(Cliente cliente);

    /** Busca un cliente por su ID. Retorna Optional vacío si no existe. */
    Optional<Cliente> buscarPorId(Long id);

    /** Retorna la lista completa de clientes ordenados por ID. */
    List<Cliente> listarTodos();

    /** Actualiza los datos de un cliente existente. */
    Cliente actualizar(Cliente cliente);

    /** Elimina un cliente por su ID. */
    void eliminar(Long id);
}
