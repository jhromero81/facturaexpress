package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.config.DatabaseConfig;
import com.codewise.facturaexpress.model.DetalleFactura;
import com.codewise.facturaexpress.model.Factura;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
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
        String sqlCabecera = "INSERT INTO facturas (cliente_id, usuario_id, fecha, total, estado, xml, pdf, cune, firma_estado, intentos_dian, correo_enviado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalles_factura (factura_id, producto_id, cantidad, precio_unitario, subtotal, descuento) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = databaseConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlCabecera, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, factura.getClienteId());
                if (factura.getUsuarioId() != null) {
                    stmt.setLong(2, factura.getUsuarioId());
                } else {
                    stmt.setNull(2, Types.BIGINT);
                }
                stmt.setTimestamp(3, Timestamp.valueOf(factura.getFecha()));
                stmt.setBigDecimal(4, factura.getTotal());
                stmt.setString(5, factura.getEstado());
                stmt.setString(6, factura.getXml());
                if (factura.getPdf() != null) {
                    stmt.setBytes(7, factura.getPdf());
                } else {
                    stmt.setNull(7, Types.BLOB);
                }
                stmt.setString(8, factura.getCune());
                stmt.setString(9, factura.getFirmaEstado() != null ? factura.getFirmaEstado() : "pendiente");
                stmt.setInt(10, factura.getIntentosDian() != null ? factura.getIntentosDian() : 0);
                stmt.setInt(11, factura.getCorreoEnviado() != null ? factura.getCorreoEnviado() : 0);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        factura.setId(rs.getLong(1));
                    }
                }
            }

            if (factura.getDetalles() != null) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlDetalle, Statement.RETURN_GENERATED_KEYS)) {
                    for (DetalleFactura detalle : factura.getDetalles()) {
                        stmt.setLong(1, factura.getId());
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
                        stmt.clearParameters();
                    }
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
                    System.err.println("Error al cerrar conexion: " + e.getMessage());
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
            throw new RuntimeException("Error al listar facturas: " + e.getMessage(), e);
        }
    }

    @Override
    public Factura actualizar(Factura factura) {
        String sql = "UPDATE facturas SET cliente_id=?, usuario_id=?, fecha=?, total=?, estado=?, xml=?, pdf=?, cune=?, firma_estado=?, intentos_dian=?, correo_enviado=? WHERE id=?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, factura.getClienteId());
            if (factura.getUsuarioId() != null) {
                stmt.setLong(2, factura.getUsuarioId());
            } else {
                stmt.setNull(2, Types.BIGINT);
            }
            stmt.setTimestamp(3, Timestamp.valueOf(factura.getFecha()));
            stmt.setBigDecimal(4, factura.getTotal());
            stmt.setString(5, factura.getEstado());
            stmt.setString(6, factura.getXml());
            if (factura.getPdf() != null) {
                stmt.setBytes(7, factura.getPdf());
            } else {
                stmt.setNull(7, Types.BLOB);
            }
            stmt.setString(8, factura.getCune());
            stmt.setString(9, factura.getFirmaEstado() != null ? factura.getFirmaEstado() : "pendiente");
            stmt.setInt(10, factura.getIntentosDian() != null ? factura.getIntentosDian() : 0);
            stmt.setInt(11, factura.getCorreoEnviado() != null ? factura.getCorreoEnviado() : 0);
            stmt.setLong(12, factura.getId());
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

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM detalles_factura WHERE factura_id = ?")) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

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
                    System.err.println("Error al cerrar conexion: " + e.getMessage());
                }
            }
        }
    }

    private Factura mapearFactura(ResultSet rs) throws SQLException {
        Factura factura = new Factura();
        factura.setId(rs.getLong("id"));
        factura.setClienteId(rs.getLong("cliente_id"));
        long uid = rs.getLong("usuario_id");
        if (!rs.wasNull()) factura.setUsuarioId(uid);
        Timestamp ts = rs.getTimestamp("fecha");
        factura.setFecha(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        factura.setTotal(rs.getBigDecimal("total"));
        factura.setEstado(rs.getString("estado"));
        factura.setXml(rs.getString("xml"));
        byte[] pdfBytes = rs.getBytes("pdf");
        if (pdfBytes != null) factura.setPdf(pdfBytes);
        factura.setCune(rs.getString("cune"));
        factura.setFirmaEstado(rs.getString("firma_estado"));
        factura.setIntentosDian(rs.getInt("intentos_dian"));
        factura.setCorreoEnviado(rs.getInt("correo_enviado"));
        factura.setClienteNombre(rs.getString("cliente_nombre"));
        return factura;
    }
}
