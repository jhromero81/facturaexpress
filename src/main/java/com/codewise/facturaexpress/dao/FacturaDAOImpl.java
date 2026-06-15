package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.config.DatabaseConfig;
import com.codewise.facturaexpress.model.DetalleFactura;
import com.codewise.facturaexpress.model.Factura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FacturaDAOImpl implements FacturaDAO {

    private final DatabaseConfig databaseConfig;
    private final DetalleFacturaDAO detalleFacturaDAO;

    public FacturaDAOImpl() {
        this.databaseConfig = DatabaseConfig.getInstance();
        this.detalleFacturaDAO = new DetalleFacturaDAOImpl();
    }

    @Override
    public Factura guardar(Factura factura) {
        String sql = "INSERT INTO facturas (cliente_id, fecha, total, estado) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = databaseConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, factura.getClienteId());
                stmt.setTimestamp(2, Timestamp.valueOf(factura.getFecha()));
                stmt.setBigDecimal(3, factura.getTotal());
                stmt.setString(4, factura.getEstado());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        factura.setId(rs.getLong(1));
                    }
                }
            }

            if (factura.getDetalles() != null) {
                for (DetalleFactura detalle : factura.getDetalles()) {
                    detalle.setFacturaId(factura.getId());
                    detalleFacturaDAO.guardar(detalle);
                }
            }

            conn.commit();
            return factura;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error al hacer rollback", ex);
                }
            }
            throw new RuntimeException("Error al guardar factura", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException("Error al cerrar conexion", e);
                }
            }
        }
    }

    @Override
    public Optional<Factura> buscarPorId(Long id) {
        String sql = "SELECT f.*, c.nombre AS cliente_nombre FROM facturas f " +
                     "LEFT JOIN clientes c ON f.cliente_id = c.id WHERE f.id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Factura factura = mapearFactura(rs);
                    factura.setDetalles(detalleFacturaDAO.listarPorFacturaId(id));
                    return Optional.of(factura);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar factura por id", e);
        }
    }

    @Override
    public List<Factura> listarTodos() {
        String sql = "SELECT f.*, c.nombre AS cliente_nombre FROM facturas f " +
                     "LEFT JOIN clientes c ON f.cliente_id = c.id ORDER BY f.id";
        List<Factura> facturas = new ArrayList<>();
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                facturas.add(mapearFactura(rs));
            }
            return facturas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar facturas", e);
        }
    }

    @Override
    public Factura actualizar(Factura factura) {
        String sql = "UPDATE facturas SET cliente_id = ?, fecha = ?, total = ?, estado = ? WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, factura.getClienteId());
            stmt.setTimestamp(2, Timestamp.valueOf(factura.getFecha()));
            stmt.setBigDecimal(3, factura.getTotal());
            stmt.setString(4, factura.getEstado());
            stmt.setLong(5, factura.getId());
            stmt.executeUpdate();
            return factura;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar factura", e);
        }
    }

    @Override
    public void eliminar(Long id) {
        Connection conn = null;
        try {
            conn = databaseConfig.getConnection();
            conn.setAutoCommit(false);

            detalleFacturaDAO.eliminarPorFacturaId(id);

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM facturas WHERE id = ?")) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Error al hacer rollback", ex);
                }
            }
            throw new RuntimeException("Error al eliminar factura", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException("Error al cerrar conexion", e);
                }
            }
        }
    }

    private Factura mapearFactura(ResultSet rs) throws SQLException {
        Factura factura = new Factura();
        factura.setId(rs.getLong("id"));
        factura.setClienteId(rs.getLong("cliente_id"));
        factura.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        factura.setTotal(rs.getBigDecimal("total"));
        factura.setEstado(rs.getString("estado"));
        try {
            factura.setClienteNombre(rs.getString("cliente_nombre"));
        } catch (SQLException ignored) {
        }
        return factura;
    }
}
