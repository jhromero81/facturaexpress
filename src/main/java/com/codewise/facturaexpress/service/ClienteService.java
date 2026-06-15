package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.dao.ClienteDAO;
import com.codewise.facturaexpress.dao.ClienteDAOImpl;
import com.codewise.facturaexpress.model.Cliente;

import java.util.List;
import java.util.Optional;

public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    public Cliente guardarCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (cliente.getEmail() != null && !cliente.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Formato de email invalido");
        }
        return clienteDAO.guardar(cliente);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de cliente invalido");
        }
        return clienteDAO.buscarPorId(id);
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.listarTodos();
    }

    public Cliente actualizarCliente(Cliente cliente) {
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio para actualizar");
        }
        return clienteDAO.actualizar(cliente);
    }

    public void eliminarCliente(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de cliente invalido");
        }
        clienteDAO.eliminar(id);
    }
}
