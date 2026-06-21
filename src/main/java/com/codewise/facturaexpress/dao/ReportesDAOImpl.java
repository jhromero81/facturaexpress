package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.config.DatabaseConfig;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Implementaci&oacute;n de ReportesDAO con JDBC.
 * Proporciona m&eacute;todos de agregaci&oacute;n para el panel de reportes
 * del sistema, incluyendo ventas por d&iacute;a, mes, semana y productos m&aacute;s vendidos.
 */
public class ReportesDAOImpl implements ReportesDAO {

    @Override
    public int facturasDelDia() {
        // Cuenta cu&aacute;ntas facturas se emitieron hoy
        String sql = "SELECT COUNT(*) FROM facturas WHERE DATE(fecha) = CURDATE()";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar facturas del día", e);
        }
    }

    @Override
    public BigDecimal ventasDelDia() {
        // Suma el total de todas las facturas del d&iacute;a de hoy
        String sql = "SELECT COALESCE(SUM(total), 0) FROM facturas WHERE DATE(fecha) = CURDATE()";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new RuntimeException("Error al sumar ventas del día", e);
        }
    }

    @Override
    public BigDecimal ticketPromedio() {
        // Calcula el valor promedio de las facturas del d&iacute;a
        String sql = "SELECT COALESCE(AVG(total), 0) FROM facturas WHERE DATE(fecha) = CURDATE()";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new RuntimeException("Error al calcular ticket promedio", e);
        }
    }

    @Override
    public int facturasDelMes() {
        // Cuenta cu&aacute;ntas facturas se emitieron en el mes actual
        String sql = "SELECT COUNT(*) FROM facturas WHERE MONTH(fecha) = MONTH(CURDATE()) AND YEAR(fecha) = YEAR(CURDATE())";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar facturas del mes", e);
        }
    }

    @Override
    public BigDecimal ventasDelMes() {
        // Suma el total de todas las facturas del mes actual
        String sql = "SELECT COALESCE(SUM(total), 0) FROM facturas WHERE MONTH(fecha) = MONTH(CURDATE()) AND YEAR(fecha) = YEAR(CURDATE())";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new RuntimeException("Error al sumar ventas del mes", e);
        }
    }

    @Override
    public List<Map<String, Object>> ventasSemana() {
        // Agrupa las ventas por d&iacute;a de los &uacute;ltimos 7 d&iacute;as
        String sql = "SELECT DATE(fecha) AS dia, COALESCE(SUM(total), 0) AS total " +
                     "FROM facturas WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                     "GROUP BY DATE(fecha) ORDER BY dia";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("dia", rs.getDate("dia") != null ? rs.getDate("dia").toString() : "");
                row.put("total", rs.getBigDecimal("total"));
                result.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar ventas semanales", e);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> ventasMensuales() {
        // Agrupa las ventas por mes de los &uacute;ltimos 6 meses
        String sql = "SELECT DATE_FORMAT(fecha, '%Y-%m') AS mes, COALESCE(SUM(total), 0) AS total " +
                     "FROM facturas WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
                     "GROUP BY DATE_FORMAT(fecha, '%Y-%m') ORDER BY mes";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("mes", rs.getString("mes"));
                row.put("total", rs.getBigDecimal("total"));
                result.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar ventas mensuales", e);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> topProductos(int limite) {
        // Obtiene los N productos m&aacute;s vendidos por cantidad total
        String sql = "SELECT p.nombre, COALESCE(SUM(df.cantidad), 0) AS cantidad " +
                     "FROM detalles_factura df JOIN productos p ON df.producto_id = p.id " +
                     "GROUP BY p.id, p.nombre ORDER BY cantidad DESC LIMIT ?";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("nombre", rs.getString("nombre"));
                    row.put("cantidad", rs.getInt("cantidad"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar top productos", e);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> ventasTrimestrales() {
        String sql = "SELECT CONCAT(YEAR(fecha), '-Q', QUARTER(fecha)) AS trimestre, COALESCE(SUM(total), 0) AS total " +
                     "FROM facturas WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                     "GROUP BY CONCAT(YEAR(fecha), '-Q', QUARTER(fecha)) ORDER BY MIN(fecha)";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("periodo", rs.getString("trimestre"));
                row.put("total", rs.getBigDecimal("total"));
                result.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar ventas trimestrales", e);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> ventasAnuales() {
        String sql = "SELECT YEAR(fecha) AS anio, COALESCE(SUM(total), 0) AS total " +
                     "FROM facturas WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 5 YEAR) " +
                     "GROUP BY YEAR(fecha) ORDER BY MIN(fecha)";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("periodo", rs.getString("anio"));
                row.put("total", rs.getBigDecimal("total"));
                result.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar ventas anuales", e);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> ultimasTransacciones(int limite) {
        String sql = "SELECT f.id, f.total, f.fecha, c.nombre AS cliente_nombre " +
                     "FROM facturas f LEFT JOIN clientes c ON f.cliente_id = c.id " +
                     "ORDER BY f.fecha DESC LIMIT ?";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put("total", rs.getBigDecimal("total"));
                    row.put("fecha", rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toString() : "");
                    row.put("cliente_nombre", rs.getString("cliente_nombre"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar ultimas transacciones", e);
        }
        return result;
    }
}
