package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.Reporte;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.repository.ReporteRepository;
import com.codewise.facturaexpress.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Servicio que gestiona la creación y consulta de reportes.
// Permite generar reportes filtrados por tipo, rango de fechas y usuario.
@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteService(ReporteRepository reporteRepository, UsuarioRepository usuarioRepository) {
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Genera un reporte con tipo, rango de fechas y opcionalmente usuario asociado
    @Transactional
    public Reporte generarReporte(String tipo, LocalDate inicio, LocalDate fin, Long usuarioId) {
        Reporte reporte = new Reporte();
        reporte.setTipo(tipo);
        reporte.setFechaInicio(inicio);
        reporte.setFechaFin(fin);
        if (usuarioId != null) {
            usuarioRepository.findById(usuarioId).ifPresent(reporte::setUsuario);
        }
        reporte.setArchivo("reportes/" + tipo + "_" + inicio + "_" + fin + ".pdf");
        return reporteRepository.save(reporte);
    }

    // Lista todos los reportes con el usuario asociado
    public List<Reporte> listarReportes() {
        return reporteRepository.findAllWithUsuario();
    }

    // Filtra reportes por tipo
    public List<Reporte> listarPorTipo(String tipo) {
        return reporteRepository.findByTipo(tipo);
    }

    // Busca un reporte por ID
    public Optional<Reporte> buscarPorId(Long id) {
        return reporteRepository.findById(id);
    }

    // Elimina un reporte por ID
    @Transactional
    public void eliminarReporte(Long id) {
        reporteRepository.deleteById(id);
    }
}
