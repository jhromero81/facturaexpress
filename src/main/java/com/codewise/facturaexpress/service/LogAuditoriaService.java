package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.LogAuditoria;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.repository.LogAuditoriaRepository;
import com.codewise.facturaexpress.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Servicio que gestiona el registro de la bitácora de auditoria del sistema.
// Registra acciones sobre entidades con usuario, tabla afectada y origen IP.
@Service
public class LogAuditoriaService {

    private final LogAuditoriaRepository logRepository;
    private final UsuarioRepository usuarioRepository;

    public LogAuditoriaService(LogAuditoriaRepository logRepository, UsuarioRepository usuarioRepository) {
        this.logRepository = logRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Registra un evento de auditoria con usuario completo
    @Transactional
    public void registrar(Usuario usuario, String accion, String tabla, Long registroId, HttpServletRequest request) {
        LogAuditoria log = new LogAuditoria();
        log.setUsuario(usuario);
        log.setAccion(accion);
        log.setTablaAfectada(tabla);
        log.setRegistroId(registroId);
        log.setIpOrigen(obtenerIp(request));
        logRepository.save(log);
    }

    // Registra un evento de auditoria con ID de usuario e IP directa
    @Transactional
    public void registrar(Long usuarioId, String accion, String tabla, Long registroId, String ip) {
        LogAuditoria log = new LogAuditoria();
        if (usuarioId != null) {
            usuarioRepository.findById(usuarioId).ifPresent(log::setUsuario);
        }
        log.setAccion(accion);
        log.setTablaAfectada(tabla);
        log.setRegistroId(registroId);
        log.setIpOrigen(ip);
        logRepository.save(log);
    }

    // Lista todos los logs con el usuario asociado
    public List<LogAuditoria> listarTodos() {
        return logRepository.findAllWithUsuario();
    }

    // Filtra logs por ID de usuario
    public List<LogAuditoria> listarPorUsuarioId(Long usuarioId) {
        return logRepository.findByUsuarioIdWithNombre(usuarioId);
    }

    // Filtra logs por tabla afectada
    public List<LogAuditoria> listarPorTabla(String tabla) {
        return logRepository.findByTablaAfectadaWithNombre(tabla);
    }

    // Extrae la IP real del cliente, considerando el header X-Forwarded-For
    private String obtenerIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip;
    }
}
