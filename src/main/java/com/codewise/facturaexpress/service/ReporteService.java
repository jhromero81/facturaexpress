package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.Reporte;
import com.codewise.facturaexpress.repository.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteService(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    @Transactional
    public Reporte generarReporte(String tipo, LocalDate inicio, LocalDate fin, Long usuarioId) {
        Reporte reporte = new Reporte();
        reporte.setTipo(tipo);
        reporte.setFechaInicio(inicio);
        reporte.setFechaFin(fin);
        reporte.setUsuarioId(usuarioId);
        reporte.setArchivo("reportes/" + tipo + "_" + inicio + "_" + fin + ".pdf");
        return reporteRepository.save(reporte);
    }

    public List<Reporte> listarReportes() {
        return reporteRepository.findAllWithUsuarioNombre();
    }

    public List<Reporte> listarPorTipo(String tipo) {
        return reporteRepository.findByTipo(tipo);
    }

    public Optional<Reporte> buscarPorId(Long id) {
        return reporteRepository.findById(id);
    }

    @Transactional
    public void eliminarReporte(Long id) {
        reporteRepository.deleteById(id);
    }
}
