package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByTipo(String tipo);

    @Query("SELECT r FROM Reporte r LEFT JOIN Usuario u ON u.id = r.usuarioId ORDER BY r.id")
    List<Reporte> findAllWithUsuarioNombre();
}
