package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// Repositorio JPA para la entidad LogAuditoria
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    // Obtiene todos los logs con nombre de usuario
    @Query("SELECT l FROM LogAuditoria l LEFT JOIN Usuario u ON u.id = l.usuarioId ORDER BY l.id")
    List<LogAuditoria> findAllWithUsuarioNombre();

    // Filtra logs por ID de usuario
    @Query("SELECT l FROM LogAuditoria l LEFT JOIN Usuario u ON u.id = l.usuarioId WHERE l.usuarioId = :usuarioId ORDER BY l.id")
    List<LogAuditoria> findByUsuarioIdWithNombre(Long usuarioId);

    // Filtra logs por tabla afectada
    @Query("SELECT l FROM LogAuditoria l LEFT JOIN Usuario u ON u.id = l.usuarioId WHERE l.tablaAfectada = :tabla ORDER BY l.id")
    List<LogAuditoria> findByTablaAfectadaWithNombre(String tabla);
}
