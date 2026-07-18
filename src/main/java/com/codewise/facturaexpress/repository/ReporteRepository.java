package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// Repositorio JPA para la entidad Reporte.
// La consulta findAllWithUsuario usa LEFT JOIN FETCH para cargar la relación con Usuario.
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    // Filtra reportes por tipo
    List<Reporte> findByTipo(String tipo);

    // Obtiene todos los reportes con el usuario asociado
    @Query("SELECT r FROM Reporte r LEFT JOIN FETCH r.usuario ORDER BY r.id")
    List<Reporte> findAllWithUsuario();
}
