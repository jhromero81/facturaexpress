package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.repository.ReportesRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

// Servicio que expone los metodos de consulta de reportes y estadisticas
@Service
public class ReportesService {

    private final ReportesRepository reportesRepository;

    public ReportesService(ReportesRepository reportesRepository) {
        this.reportesRepository = reportesRepository;
    }

    // Cantidad de facturas del dia actual
    public int facturasDelDia() { return reportesRepository.facturasDelDia(); }
    // Total de ventas del dia actual
    public BigDecimal ventasDelDia() { return reportesRepository.ventasDelDia(); }
    // Ticket promedio del dia actual
    public BigDecimal ticketPromedio() { return reportesRepository.ticketPromedio(); }
    // Cantidad de facturas del mes actual
    public int facturasDelMes() { return reportesRepository.facturasDelMes(); }
    // Total de ventas del mes actual
    public BigDecimal ventasDelMes() { return reportesRepository.ventasDelMes(); }
    // Ventas de los ultimos 7 dias
    public List<Map<String, Object>> ventasSemana() { return reportesRepository.ventasSemana(); }
    // Ventas mensuales de los ultimos 6 meses
    public List<Map<String, Object>> ventasMensuales() { return reportesRepository.ventasMensuales(); }
    // Top N productos mas vendidos
    public List<Map<String, Object>> topProductos(int limite) { return reportesRepository.topProductos(limite); }
    // Ultimas N transacciones realizadas
    public List<Map<String, Object>> ultimasTransacciones(int limite) { return reportesRepository.ultimasTransacciones(limite); }
    // Ventas trimestrales del ultimo ano
    public List<Map<String, Object>> ventasTrimestrales() { return reportesRepository.ventasTrimestrales(); }
    // Ventas anuales de los ultimos 5 anos
    public List<Map<String, Object>> ventasAnuales() { return reportesRepository.ventasAnuales(); }
}
