package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.dao.ReportesDAO;
import com.codewise.facturaexpress.dao.ReportesDAOImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Servicio de reportes y estadísticas. Delega en ReportesDAO para
 * obtener métricas de facturación diarias, mensuales y productos top.
 */
public class ReportesService {

    private final ReportesDAO reportesDAO;

    public ReportesService() {
        this.reportesDAO = new ReportesDAOImpl();
    }

    /** Total de facturas emitidas hoy. */
    public int facturasDelDia() { return reportesDAO.facturasDelDia(); }

    /** Suma de ventas del día de hoy. */
    public BigDecimal ventasDelDia() { return reportesDAO.ventasDelDia(); }

    /** Valor promedio de las facturas del día. */
    public BigDecimal ticketPromedio() { return reportesDAO.ticketPromedio(); }

    /** Total de facturas emitidas en el mes actual. */
    public int facturasDelMes() { return reportesDAO.facturasDelMes(); }

    /** Suma de ventas del mes actual. */
    public BigDecimal ventasDelMes() { return reportesDAO.ventasDelMes(); }

    /** Ventas agregadas por día de la semana actual. */
    public List<Map<String, Object>> ventasSemana() { return reportesDAO.ventasSemana(); }

    /** Ventas agregadas por mes del año actual. */
    public List<Map<String, Object>> ventasMensuales() { return reportesDAO.ventasMensuales(); }

    /** Retorna los N productos más vendidos. */
    public List<Map<String, Object>> topProductos(int limite) { return reportesDAO.topProductos(limite); }

    /** Retorna las últimas N facturas para el dashboard. */
    public List<Map<String, Object>> ultimasTransacciones(int limite) { return reportesDAO.ultimasTransacciones(limite); }

    /** Retorna ventas agrupadas por trimestre (últimos 12 meses). */
    public List<Map<String, Object>> ventasTrimestrales() { return reportesDAO.ventasTrimestrales(); }

    /** Retorna ventas agrupadas por año (últimos 5 años). */
    public List<Map<String, Object>> ventasAnuales() { return reportesDAO.ventasAnuales(); }
}
