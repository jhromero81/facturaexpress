package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.Reporte;
import com.codewise.facturaexpress.repository.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Servicio para la generacion y gestion de reportes
@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteService(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    // Genera un nuevo reporte con tipo, rango de fechas y usuario
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

    // Obtiene todos los reportes registrados
    public List<Reporte> listarReportes() {
        return reporteRepository.findAllWithUsuarioNombre();
    }

    // Filtra reportes por tipo
    public List<Reporte> listarPorTipo(String tipo) {
        return reporteRepository.findByTipo(tipo);
    }

    // Busca un reporte por su ID
    public Optional<Reporte> buscarPorId(Long id) {
        return reporteRepository.findById(id);
    }

    // Elimina un reporte por su ID
    @Transactional
    public void eliminarReporte(Long id) {
        reporteRepository.deleteById(id);
    }
}
