package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// Repositorio JPA para la entidad DetalleFactura
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {

    // Busca detalles por factura incluyendo el nombre del producto
    @Query("SELECT d FROM DetalleFactura d LEFT JOIN Producto p ON p.id = d.productoId WHERE d.facturaId = :facturaId ORDER BY d.id")
    List<DetalleFactura> findByFacturaIdWithProductoNombre(Long facturaId);

    // Elimina todos los detalles de una factura
    void deleteByFacturaId(Long facturaId);

    // Obtiene todos los detalles con nombre de producto
    @Query("SELECT d FROM DetalleFactura d LEFT JOIN Producto p ON p.id = d.productoId ORDER BY d.id")
    List<DetalleFactura> findAllWithProductoNombre();

    // Busca un detalle por ID con nombre de producto
    @Query("SELECT d FROM DetalleFactura d LEFT JOIN Producto p ON p.id = d.productoId WHERE d.id = :id")
    java.util.Optional<DetalleFactura> findByIdWithProductoNombre(Long id);
}
