package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// Repositorio JPA para la entidad DetalleFactura.
// Las consultas usan LEFT JOIN FETCH para cargar la relación con Producto en una sola query.
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {

    // Obtiene los detalles de una factura con el producto asociado
    @Query("SELECT d FROM DetalleFactura d LEFT JOIN FETCH d.producto WHERE d.factura.id = :facturaId ORDER BY d.id")
    List<DetalleFactura> findByFacturaIdWithProducto(Long facturaId);

    // Elimina todos los detalles de una factura
    void deleteByFacturaId(Long facturaId);

    // Obtiene todos los detalles con el producto asociado
    @Query("SELECT d FROM DetalleFactura d LEFT JOIN FETCH d.producto ORDER BY d.id")
    List<DetalleFactura> findAllWithProducto();

    // Busca un detalle por ID con el producto asociado
    @Query("SELECT d FROM DetalleFactura d LEFT JOIN FETCH d.producto WHERE d.id = :id")
    java.util.Optional<DetalleFactura> findByIdWithProducto(Long id);
}
