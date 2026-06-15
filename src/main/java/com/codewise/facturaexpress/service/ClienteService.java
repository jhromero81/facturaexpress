package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.dao.ClienteDAO;
import com.codewise.facturaexpress.dao.ClienteDAOImpl;
import com.codewise.facturaexpress.model.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Servicio CRUD para clientes con validaciones de negocio:
 * nombre obligatorio y formato de email validado con regex.
 */
public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    /**
     * Guarda un nuevo cliente tras validar nombre obligatorio y formato de email.
     */
    public Cliente guardarCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (cliente.getEmail() != null && !cliente.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Formato de email invalido");
        }
        return clienteDAO.guardar(cliente);
    }

    /**
     * Busca un cliente por su ID (debe ser mayor a 0).
     */
    public Optional<Cliente> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de cliente invalido");
        }
        return clienteDAO.buscarPorId(id);
    }

    /**
     * Retorna la lista completa de clientes registrados.
     */
    public List<Cliente> listarClientes() {
        return clienteDAO.listarTodos();
    }

    /**
     * Actualiza un cliente existente. El ID es obligatorio.
     */
    public Cliente actualizarCliente(Cliente cliente) {
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio para actualizar");
        }
        return clienteDAO.actualizar(cliente);
    }

    /**
     * Elimina un cliente por su ID.
     */
    public void eliminarCliente(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de cliente invalido");
        }
        clienteDAO.eliminar(id);
    }
}
