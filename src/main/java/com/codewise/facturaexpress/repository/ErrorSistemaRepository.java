package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.ErrorSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

// Repositorio JPA para la entidad ErrorSistema
public interface ErrorSistemaRepository extends JpaRepository<ErrorSistema, Long> {

    // Busca errores por tipo
    List<ErrorSistema> findByTipo(String tipo);

    // Busca errores no resueltos
    List<ErrorSistema> findByResueltoFalse();

    // Marca un error como resuelto con la fecha proporcionada
    @Modifying
    @Query("UPDATE ErrorSistema e SET e.resuelto = true, e.fechaResolucion = :fecha WHERE e.id = :id")
    void marcarResuelto(Long id, LocalDateTime fecha);
}
