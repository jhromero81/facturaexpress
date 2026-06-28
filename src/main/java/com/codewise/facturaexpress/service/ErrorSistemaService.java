package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.ErrorSistema;
import com.codewise.facturaexpress.repository.ErrorSistemaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ErrorSistemaService {

    private final ErrorSistemaRepository errorRepository;

    public ErrorSistemaService(ErrorSistemaRepository errorRepository) {
        this.errorRepository = errorRepository;
    }

    public ErrorSistema registrarError(String mensaje, String tipo, Long facturaId) {
        ErrorSistema error = new ErrorSistema();
        error.setMensaje(mensaje);
        error.setTipo(tipo);
        error.setFacturaId(facturaId);
        return errorRepository.save(error);
    }

    public List<ErrorSistema> listarErrores() {
        return errorRepository.findAll();
    }

    public List<ErrorSistema> listarNoResueltos() {
        return errorRepository.findByResueltoFalse();
    }

    public List<ErrorSistema> listarPorTipo(String tipo) {
        return errorRepository.findByTipo(tipo);
    }

    public Optional<ErrorSistema> buscarPorId(Long id) {
        return errorRepository.findById(id);
    }

    public void marcarResuelto(Long id) {
        errorRepository.marcarResuelto(id, LocalDateTime.now());
    }
}
