package com.codewise.facturaexpress.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ReportesDAO {
    /** Retorna el número de facturas emitidas en el día de hoy. */
    int facturasDelDia();
    /** Retorna la suma total de ventas del día de hoy. */
    BigDecimal ventasDelDia();
    /** Retorna el ticket promedio (media del total) de las facturas del día. */
    BigDecimal ticketPromedio();
    /** Retorna el número de facturas emitidas en el mes actual. */
    int facturasDelMes();
    /** Retorna la suma total de ventas del mes actual. */
    BigDecimal ventasDelMes();
    /** Retorna las ventas agrupadas por día de los últimos 7 días. */
    List<Map<String, Object>> ventasSemana();
    /** Retorna las ventas agrupadas por mes de los últimos 6 meses. */
    List<Map<String, Object>> ventasMensuales();
    /** Retorna las ventas agrupadas por trimestre (12 meses agrupados). */
    List<Map<String, Object>> ventasTrimestrales();
    /** Retorna las ventas agrupadas por año (últimos 5 años). */
    List<Map<String, Object>> ventasAnuales();
    /** Retorna los N productos más vendidos por cantidad. */
    List<Map<String, Object>> topProductos(int limite);

    /** Retorna las últimas N facturas con datos de cliente para el dashboard. */
    List<Map<String, Object>> ultimasTransacciones(int limite);
}
