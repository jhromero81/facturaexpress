package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    @Query("SELECT f FROM Factura f LEFT JOIN Cliente c ON c.id = f.clienteId ORDER BY f.id")
    List<Factura> findAllWithClienteNombre();

    @Query("SELECT f FROM Factura f LEFT JOIN Cliente c ON c.id = f.clienteId WHERE f.id = :id")
    Optional<Factura> findByIdWithClienteNombre(Long id);
}
