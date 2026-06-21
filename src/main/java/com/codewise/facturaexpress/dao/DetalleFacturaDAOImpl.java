package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.config.DatabaseConfig;
import com.codewise.facturaexpress.model.DetalleFactura;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementaci&oacute;n de DetalleFacturaDAO con JDBC.
 * Ejecuta consultas SQL directamente contra la tabla "detalles_factura".
 */
public class DetalleFacturaDAOImpl implements DetalleFacturaDAO {

    private final DatabaseConfig databaseConfig;

    public DetalleFacturaDAOImpl() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    @Override
    public DetalleFactura guardar(DetalleFactura detalle) {
        // Inserta un nuevo detalle de factura y recupera el ID autogenerado
        String sql = "INSERT INTO detalles_factura (factura_id, producto_id, cantidad, precio_unitario, subtotal, descuento) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, detalle.getFacturaId());
            stmt.setLong(2, detalle.getProductoId());
            stmt.setInt(3, detalle.getCantidad());
            stmt.setBigDecimal(4, detalle.getPrecioUnitario());
            stmt.setBigDecimal(5, detalle.getSubtotal());
            stmt.setBigDecimal(6, detalle.getDescuento() != null ? detalle.getDescuento() : BigDecimal.ZERO);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    detalle.setId(rs.getLong(1));
                }
            }
            return detalle;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar detalle de factura", e);
        }
    }

    @Override
    public Optional<DetalleFactura> buscarPorId(Long id) {
        // Busca un detalle por ID incluyendo el nombre del producto (LEFT JOIN)
        String sql = "SELECT df.*, p.nombre AS producto_nombre FROM detalles_factura df " +
                     "LEFT JOIN productos p ON df.producto_id = p.id WHERE df.id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDetalle(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar detalle por id", e);
        }
    }

    @Override
    public List<DetalleFactura> listarPorFacturaId(Long facturaId) {
        // Lista todos los detalles de una factura, ordenados por ID, con nombre del producto
        String sql = "SELECT df.*, p.nombre AS producto_nombre FROM detalles_factura df " +
                     "LEFT JOIN productos p ON df.producto_id = p.id WHERE df.factura_id = ? ORDER BY df.id";
        List<DetalleFactura> detalles = new ArrayList<>();
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, facturaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapearDetalle(rs));
                }
            }
            return detalles;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar detalles por factura", e);
        }
    }

    @Override
    public List<DetalleFactura> listarTodos() {
        // Lista todos los detalles con nombre del producto, ordenados por ID
        String sql = "SELECT df.*, p.nombre AS producto_nombre FROM detalles_factura df " +
                     "LEFT JOIN productos p ON df.producto_id = p.id ORDER BY df.id";
        List<DetalleFactura> detalles = new ArrayList<>();
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                detalles.add(mapearDetalle(rs));
            }
            return detalles;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar detalles", e);
        }
    }

    @Override
    public DetalleFactura actualizar(DetalleFactura detalle) {
        // Actualiza cantidad, precio unitario y subtotal de un detalle
        String sql = "UPDATE detalles_factura SET cantidad=?, precio_unitario=?, subtotal=?, descuento=? WHERE id=?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detalle.getCantidad());
            stmt.setBigDecimal(2, detalle.getPrecioUnitario());
            stmt.setBigDecimal(3, detalle.getSubtotal());
            stmt.setBigDecimal(4, detalle.getDescuento() != null ? detalle.getDescuento() : BigDecimal.ZERO);
            stmt.setLong(5, detalle.getId());
            stmt.executeUpdate();
            return detalle;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar detalle de factura", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        // Elimina un detalle por su ID
        String sql = "DELETE FROM detalles_factura WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar detalle de factura", e);
        }
    }

    @Override
    public void eliminarPorFacturaId(Long facturaId) {
        // Elimina todos los detalles asociados a una factura
        String sql = "DELETE FROM detalles_factura WHERE factura_id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, facturaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar detalles por factura", e);
        }
    }

    private DetalleFactura mapearDetalle(ResultSet rs) throws SQLException {
        DetalleFactura detalle = new DetalleFactura();
        detalle.setId(rs.getLong("id"));
        detalle.setFacturaId(rs.getLong("factura_id"));
        detalle.setProductoId(rs.getLong("producto_id"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        detalle.setSubtotal(rs.getBigDecimal("subtotal"));
        detalle.setProductoNombre(rs.getString("producto_nombre"));
        BigDecimal desc = rs.getBigDecimal("descuento");
        detalle.setDescuento(desc != null ? desc : BigDecimal.ZERO);
        return detalle;
    }
}
