package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.repository.ReportesRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ReportesService {

    private final ReportesRepository reportesRepository;

    public ReportesService(ReportesRepository reportesRepository) {
        this.reportesRepository = reportesRepository;
    }

    public int facturasDelDia() { return reportesRepository.facturasDelDia(); }
    public BigDecimal ventasDelDia() { return reportesRepository.ventasDelDia(); }
    public BigDecimal ticketPromedio() { return reportesRepository.ticketPromedio(); }
    public int facturasDelMes() { return reportesRepository.facturasDelMes(); }
    public BigDecimal ventasDelMes() { return reportesRepository.ventasDelMes(); }
    public List<Map<String, Object>> ventasSemana() { return reportesRepository.ventasSemana(); }
    public List<Map<String, Object>> ventasMensuales() { return reportesRepository.ventasMensuales(); }
    public List<Map<String, Object>> topProductos(int limite) { return reportesRepository.topProductos(limite); }
    public List<Map<String, Object>> ultimasTransacciones(int limite) { return reportesRepository.ultimasTransacciones(limite); }
    public List<Map<String, Object>> ventasTrimestrales() { return reportesRepository.ventasTrimestrales(); }
    public List<Map<String, Object>> ventasAnuales() { return reportesRepository.ventasAnuales(); }
}
