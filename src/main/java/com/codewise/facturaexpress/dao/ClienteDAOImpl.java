package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.config.DatabaseConfig;
import com.codewise.facturaexpress.model.Cliente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementaci&oacute;n de ClienteDAO con JDBC.
 * Ejecuta consultas SQL directamente contra la tabla "clientes".
 */
public class ClienteDAOImpl implements ClienteDAO {

    private final DatabaseConfig databaseConfig;

    public ClienteDAOImpl() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        // Inserta un nuevo cliente y recupera el ID autogenerado
        String sql = "INSERT INTO clientes (nombre, email, telefono, direccion, fecha_creacion) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getDireccion());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getLong(1));
                }
            }
            cliente.setFechaCreacion(LocalDateTime.now());
            return cliente;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar cliente", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        // Busca un cliente por su ID primario
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCliente(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por id", e);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        // Obtiene todos los clientes ordenados por ID
        String sql = "SELECT * FROM clientes ORDER BY id";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
            return clientes;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar clientes", e);
        }
    }

    @Override
    public Cliente actualizar(Cliente cliente) {
        // Actualiza los datos de un cliente existente por ID
        String sql = "UPDATE clientes SET nombre = ?, email = ?, telefono = ?, direccion = ? WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getDireccion());
            stmt.setLong(5, cliente.getId());
            stmt.executeUpdate();
            return cliente;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar cliente", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        // Elimina un cliente por su ID
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar cliente", e);
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("id"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setEmail(rs.getString("email"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setDireccion(rs.getString("direccion"));
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            cliente.setFechaCreacion(ts.toLocalDateTime());
        }
        return cliente;
    }
}
