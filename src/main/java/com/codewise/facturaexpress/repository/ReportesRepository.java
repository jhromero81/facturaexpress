package com.codewise.facturaexpress.repository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

// Repositorio personalizado para consultas de reportes mediante SQL nativo
@Repository
public class ReportesRepository {

    private final EntityManager em;

    public ReportesRepository(EntityManager em) {
        this.em = em;
    }

    // Cuenta las facturas del día actual
    public int facturasDelDia() {
        Number result = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM facturas WHERE DATE(fecha) = CURDATE()")
                .getSingleResult();
        return result != null ? result.intValue() : 0;
    }

    // Suma el total de ventas del día actual
    public BigDecimal ventasDelDia() {
        BigDecimal result = (BigDecimal) em.createNativeQuery(
                "SELECT COALESCE(SUM(total), 0) FROM facturas WHERE DATE(fecha) = CURDATE()")
                .getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    // Calcula el ticket promedio del día actual
    public BigDecimal ticketPromedio() {
        BigDecimal result = (BigDecimal) em.createNativeQuery(
                "SELECT COALESCE(AVG(total), 0) FROM facturas WHERE DATE(fecha) = CURDATE()")
                .getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    // Cuenta las facturas del mes actual
    public int facturasDelMes() {
        Number result = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM facturas WHERE MONTH(fecha) = MONTH(CURDATE()) AND YEAR(fecha) = YEAR(CURDATE())")
                .getSingleResult();
        return result != null ? result.intValue() : 0;
    }

    // Suma el total de ventas del mes actual
    public BigDecimal ventasDelMes() {
        BigDecimal result = (BigDecimal) em.createNativeQuery(
                "SELECT COALESCE(SUM(total), 0) FROM facturas WHERE MONTH(fecha) = MONTH(CURDATE()) AND YEAR(fecha) = YEAR(CURDATE())")
                .getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    // Obtiene las ventas diarias de los últimos 7 días
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> ventasSemana() {
        List<Object[]> rows = em.createNativeQuery(
                "SELECT DATE(fecha) AS dia, COALESCE(SUM(total), 0) AS total " +
                "FROM facturas WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                "GROUP BY DATE(fecha) ORDER BY dia")
                .getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("dia", row[0] != null ? row[0].toString() : "");
            map.put("total", row[1]);
            result.add(map);
        }
        return result;
    }

    // Obtiene las ventas mensuales de los últimos 6 meses
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> ventasMensuales() {
        List<Object[]> rows = em.createNativeQuery(
                "SELECT DATE_FORMAT(fecha, '%Y-%m') AS mes, COALESCE(SUM(total), 0) AS total " +
                "FROM facturas WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
                "GROUP BY DATE_FORMAT(fecha, '%Y-%m') ORDER BY mes")
                .getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("mes", row[0]);
            map.put("total", row[1]);
            result.add(map);
        }
        return result;
    }

    // Obtiene los N productos más vendidos
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> topProductos(int limite) {
        List<Object[]> rows = em.createNativeQuery(
                "SELECT p.nombre, COALESCE(SUM(df.cantidad), 0) AS cantidad " +
                "FROM detalles_factura df JOIN productos p ON df.producto_id = p.id " +
                "GROUP BY p.id, p.nombre ORDER BY cantidad DESC LIMIT :limite")
                .setParameter("limite", limite)
                .getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("nombre", row[0]);
            map.put("cantidad", row[1]);
            result.add(map);
        }
        return result;
    }

    // Obtiene las ventas trimestrales del último año
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> ventasTrimestrales() {
        List<Object[]> rows = em.createNativeQuery(
                "SELECT CONCAT(YEAR(fecha), '-Q', QUARTER(fecha)) AS trimestre, COALESCE(SUM(total), 0) AS total " +
                "FROM facturas WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                "GROUP BY CONCAT(YEAR(fecha), '-Q', QUARTER(fecha)) ORDER BY MIN(fecha)")
                .getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("periodo", row[0]);
            map.put("total", row[1]);
            result.add(map);
        }
        return result;
    }

    // Obtiene las ventas anuales de los últimos 5 años
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> ventasAnuales() {
        List<Object[]> rows = em.createNativeQuery(
                "SELECT YEAR(fecha) AS anio, COALESCE(SUM(total), 0) AS total " +
                "FROM facturas WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 5 YEAR) " +
                "GROUP BY YEAR(fecha) ORDER BY MIN(fecha)")
                .getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("periodo", row[0].toString());
            map.put("total", row[1]);
            result.add(map);
        }
        return result;
    }

    // Obtiene las últimas N transacciones con datos del cliente
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> ultimasTransacciones(int limite) {
        List<Object[]> rows = em.createNativeQuery(
                "SELECT f.id, f.total, f.fecha, c.nombre AS cliente_nombre " +
                "FROM facturas f LEFT JOIN clientes c ON f.cliente_id = c.id " +
                "ORDER BY f.fecha DESC LIMIT :limite")
                .setParameter("limite", limite)
                .getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", row[0]);
            map.put("total", row[1]);
            map.put("fecha", row[2] != null ? row[2].toString() : "");
            map.put("cliente_nombre", row[3]);
            result.add(map);
        }
        return result;
    }
}
