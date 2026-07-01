package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// Repositorio JPA para la entidad Reporte
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    // Filtra reportes por tipo
    List<Reporte> findByTipo(String tipo);

    // Obtiene todos los reportes con nombre de usuario
    @Query("SELECT r FROM Reporte r LEFT JOIN Usuario u ON u.id = r.usuarioId ORDER BY r.id")
    List<Reporte> findAllWithUsuarioNombre();
}
