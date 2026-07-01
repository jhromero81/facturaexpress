package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// Repositorio JPA para la entidad Factura
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // Obtiene todas las facturas con el nombre del cliente
    @Query("SELECT f FROM Factura f LEFT JOIN Cliente c ON c.id = f.clienteId ORDER BY f.id")
    List<Factura> findAllWithClienteNombre();

    // Busca una factura por ID incluyendo el nombre del cliente
    @Query("SELECT f FROM Factura f LEFT JOIN Cliente c ON c.id = f.clienteId WHERE f.id = :id")
    Optional<Factura> findByIdWithClienteNombre(Long id);
}
