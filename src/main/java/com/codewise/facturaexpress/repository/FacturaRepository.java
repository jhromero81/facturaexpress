package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// Repositorio JPA para la entidad Factura.
// Las consultas usan LEFT JOIN FETCH para cargar la relación con Cliente en una sola query.
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Obtiene todas las facturas con el cliente asociado
    @Query("SELECT f FROM Factura f LEFT JOIN FETCH f.cliente ORDER BY f.id")
    List<Factura> findAllWithCliente();

    // Busca una factura por ID incluyendo el cliente
    @Query("SELECT f FROM Factura f LEFT JOIN FETCH f.cliente WHERE f.id = :id")
    Optional<Factura> findByIdWithCliente(Long id);
}
