package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.LogAuditoria;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.repository.LogAuditoriaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogAuditoriaService {

    private final LogAuditoriaRepository logRepository;

    public LogAuditoriaService(LogAuditoriaRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void registrar(Usuario usuario, String accion, String tabla, Long registroId, HttpServletRequest request) {
        LogAuditoria log = new LogAuditoria();
        log.setUsuarioId(usuario.getId());
        log.setAccion(accion);
        log.setTablaAfectada(tabla);
        log.setRegistroId(registroId);
        log.setIpOrigen(obtenerIp(request));
        logRepository.save(log);
    }

    public void registrar(Long usuarioId, String accion, String tabla, Long registroId, String ip) {
        LogAuditoria log = new LogAuditoria();
        log.setUsuarioId(usuarioId);
        log.setAccion(accion);
        log.setTablaAfectada(tabla);
        log.setRegistroId(registroId);
        log.setIpOrigen(ip);
        logRepository.save(log);
    }

    public List<LogAuditoria> listarTodos() {
        return logRepository.findAllWithUsuarioNombre();
    }

    public List<LogAuditoria> listarPorUsuarioId(Long usuarioId) {
        return logRepository.findByUsuarioIdWithNombre(usuarioId);
    }

    public List<LogAuditoria> listarPorTabla(String tabla) {
        return logRepository.findByTablaAfectadaWithNombre(tabla);
    }

    private String obtenerIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip;
    }
}
