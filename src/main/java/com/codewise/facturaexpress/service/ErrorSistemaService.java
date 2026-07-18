package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.ErrorSistema;
import com.codewise.facturaexpress.model.Factura;
import com.codewise.facturaexpress.repository.ErrorSistemaRepository;
import com.codewise.facturaexpress.repository.FacturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Servicio que gestiona el registro y consulta de errores del sistema.
// Permite registrar errores asociados a facturas y marcarlos como resueltos.
@Service
public class ErrorSistemaService {

    private final ErrorSistemaRepository errorRepository;
    private final FacturaRepository facturaRepository;

    public ErrorSistemaService(ErrorSistemaRepository errorRepository, FacturaRepository facturaRepository) {
        this.errorRepository = errorRepository;
        this.facturaRepository = facturaRepository;
    }

    // Registra un error opcionalmente asociado a una factura
    @Transactional
    public ErrorSistema registrarError(String mensaje, String tipo, Long facturaId) {
        ErrorSistema error = new ErrorSistema();
        error.setMensaje(mensaje);
        error.setTipo(tipo);
        if (facturaId != null) {
            facturaRepository.findById(facturaId).ifPresent(error::setFactura);
        }
        return errorRepository.save(error);
    }

    // Lista todos los errores registrados
    public List<ErrorSistema> listarErrores() {
        return errorRepository.findAll();
    }

    // Lista los errores que aún no han sido resueltos
    public List<ErrorSistema> listarNoResueltos() {
        return errorRepository.findByResueltoFalse();
    }

    // Filtra errores por tipo
    public List<ErrorSistema> listarPorTipo(String tipo) {
        return errorRepository.findByTipo(tipo);
    }

    // Busca un error por ID
    public Optional<ErrorSistema> buscarPorId(Long id) {
        return errorRepository.findById(id);
    }

    // Marca un error como resuelto con la fecha/hora actual
    public void marcarResuelto(Long id) {
        errorRepository.marcarResuelto(id, LocalDateTime.now());
    }
}
