package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.LogAuditoria;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.repository.LogAuditoriaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Servicio para registrar y consultar la bitacora de auditoria del sistema
@Service
public class LogAuditoriaService {

    private final LogAuditoriaRepository logRepository;

    public LogAuditoriaService(LogAuditoriaRepository logRepository) {
        this.logRepository = logRepository;
    }

    // Registra una accion de auditoria asociada a un usuario y solicitud HTTP
    @Transactional
    public void registrar(Usuario usuario, String accion, String tabla, Long registroId, HttpServletRequest request) {
        if (usuario == null) { registrar(null, accion, tabla, registroId, obtenerIp(request)); return; }
        LogAuditoria log = new LogAuditoria();
        log.setUsuarioId(usuario.getId());
        log.setAccion(accion);
        log.setTablaAfectada(tabla);
        log.setRegistroId(registroId);
        log.setIpOrigen(obtenerIp(request));
        logRepository.save(log);
    }

    // Registra una accion de auditoria con datos directamente proporcionados
    @Transactional
    public void registrar(Long usuarioId, String accion, String tabla, Long registroId, String ip) {
        LogAuditoria log = new LogAuditoria();
        log.setUsuarioId(usuarioId);
        log.setAccion(accion);
        log.setTablaAfectada(tabla);
        log.setRegistroId(registroId);
        log.setIpOrigen(ip);
        logRepository.save(log);
    }

    // Obtiene todos los registros de auditoria
    public List<LogAuditoria> listarTodos() {
        return logRepository.findAllWithUsuarioNombre();
    }

    // Filtra registros por ID de usuario
    public List<LogAuditoria> listarPorUsuarioId(Long usuarioId) {
        return logRepository.findByUsuarioIdWithNombre(usuarioId);
    }

    // Filtra registros por tabla afectada
    public List<LogAuditoria> listarPorTabla(String tabla) {
        return logRepository.findByTablaAfectadaWithNombre(tabla);
    }

    // Obtiene la direccion IP del cliente desde la solicitud HTTP
    private String obtenerIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip;
    }
}
