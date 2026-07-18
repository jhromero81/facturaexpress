package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// Repositorio JPA para la entidad LogAuditoria.
// Las consultas usan LEFT JOIN FETCH para cargar la relación con Usuario en una sola query.
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    // Obtiene todos los logs con el usuario asociado
    @Query("SELECT l FROM LogAuditoria l LEFT JOIN FETCH l.usuario ORDER BY l.id")
    List<LogAuditoria> findAllWithUsuario();

    // Filtra logs por ID de usuario incluyendo el nombre
    @Query("SELECT l FROM LogAuditoria l LEFT JOIN FETCH l.usuario WHERE l.usuario.id = :usuarioId ORDER BY l.id")
    List<LogAuditoria> findByUsuarioIdWithNombre(Long usuarioId);

    // Filtra logs por tabla afectada incluyendo el nombre del usuario
    @Query("SELECT l FROM LogAuditoria l LEFT JOIN FETCH l.usuario WHERE l.tablaAfectada = :tabla ORDER BY l.id")
    List<LogAuditoria> findByTablaAfectadaWithNombre(String tabla);
}
