package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {

    @Query("SELECT l FROM LogAuditoria l LEFT JOIN Usuario u ON u.id = l.usuarioId ORDER BY l.id")
    List<LogAuditoria> findAllWithUsuarioNombre();

    @Query("SELECT l FROM LogAuditoria l LEFT JOIN Usuario u ON u.id = l.usuarioId WHERE l.usuarioId = :usuarioId ORDER BY l.id")
    List<LogAuditoria> findByUsuarioIdWithNombre(Long usuarioId);

    @Query("SELECT l FROM LogAuditoria l LEFT JOIN Usuario u ON u.id = l.usuarioId WHERE l.tablaAfectada = :tabla ORDER BY l.id")
    List<LogAuditoria> findByTablaAfectadaWithNombre(String tabla);
}
