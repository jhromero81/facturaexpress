package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.config.DatabaseConfig;
import com.codewise.facturaexpress.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDAOImpl implements ProductoDAO {

    private final DatabaseConfig databaseConfig;

    public ProductoDAOImpl() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    @Override
    public Producto guardar(Producto producto) {
        String sql = "INSERT INTO productos (nombre, descripcion, precio, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setBigDecimal(3, producto.getPrecio());
            stmt.setInt(4, producto.getStock());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    producto.setId(rs.getLong(1));
                }
            }
            return producto;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar producto", e);
        }
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        String sql = "SELECT * FROM productos WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearProducto(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar producto por id", e);
        }
    }

    @Override
    public List<Producto> listarTodos() {
        String sql = "SELECT * FROM productos ORDER BY id";
        List<Producto> productos = new ArrayList<>();
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            return productos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productos", e);
        }
    }

    @Override
    public Producto actualizar(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, stock = ? WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setBigDecimal(3, producto.getPrecio());
            stmt.setInt(4, producto.getStock());
            stmt.setLong(5, producto.getId());
            stmt.executeUpdate();
            return producto;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar producto", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar producto", e);
        }
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getLong("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecio(rs.getBigDecimal("precio"));
        producto.setStock(rs.getInt("stock"));
        return producto;
    }
}
