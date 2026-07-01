package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.ErrorSistema;
import com.codewise.facturaexpress.repository.ErrorSistemaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Servicio para el registro y gestion de errores del sistema
@Service
public class ErrorSistemaService {

    private final ErrorSistemaRepository errorRepository;

    public ErrorSistemaService(ErrorSistemaRepository errorRepository) {
        this.errorRepository = errorRepository;
    }

    // Registra un nuevo error en el sistema
    @Transactional
    public ErrorSistema registrarError(String mensaje, String tipo, Long facturaId) {
        ErrorSistema error = new ErrorSistema();
        error.setMensaje(mensaje);
        error.setTipo(tipo);
        error.setFacturaId(facturaId);
        return errorRepository.save(error);
    }

    // Obtiene todos los errores registrados
    public List<ErrorSistema> listarErrores() {
        return errorRepository.findAll();
    }

    // Obtiene los errores que aun no han sido resueltos
    public List<ErrorSistema> listarNoResueltos() {
        return errorRepository.findByResueltoFalse();
    }

    // Filtra errores por tipo
    public List<ErrorSistema> listarPorTipo(String tipo) {
        return errorRepository.findByTipo(tipo);
    }

    // Busca un error por su ID
    public Optional<ErrorSistema> buscarPorId(Long id) {
        return errorRepository.findById(id);
    }

    // Marca un error como resuelto con la fecha actual
    public void marcarResuelto(Long id) {
        errorRepository.marcarResuelto(id, LocalDateTime.now());
    }
}
