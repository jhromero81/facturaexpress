package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.ErrorSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ErrorSistemaRepository extends JpaRepository<ErrorSistema, Long> {

    List<ErrorSistema> findByTipo(String tipo);

    List<ErrorSistema> findByResueltoFalse();

    @Modifying
    @Transactional
    @Query("UPDATE ErrorSistema e SET e.resuelto = true, e.fechaResolucion = :fecha WHERE e.id = :id")
    void marcarResuelto(Long id, LocalDateTime fecha);
}
