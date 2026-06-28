package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.Cliente;
import com.codewise.facturaexpress.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente guardarCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (cliente.getEmail() != null && !cliente.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Formato de email invalido");
        }
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de cliente invalido");
        }
        return clienteRepository.findById(id);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Cliente actualizarCliente(Cliente cliente) {
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("El ID del cliente es obligatorio para actualizar");
        }
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (cliente.getEmail() != null && !cliente.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Formato de email invalido");
        }
        return clienteRepository.save(cliente);
    }

    public void eliminarCliente(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de cliente invalido");
        }
        clienteRepository.deleteById(id);
    }
}
