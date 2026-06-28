package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {

    @Query("SELECT d FROM DetalleFactura d LEFT JOIN Producto p ON p.id = d.productoId WHERE d.facturaId = :facturaId ORDER BY d.id")
    List<DetalleFactura> findByFacturaIdWithProductoNombre(Long facturaId);

    void deleteByFacturaId(Long facturaId);

    @Query("SELECT d FROM DetalleFactura d LEFT JOIN Producto p ON p.id = d.productoId ORDER BY d.id")
    List<DetalleFactura> findAllWithProductoNombre();

    @Query("SELECT d FROM DetalleFactura d LEFT JOIN Producto p ON p.id = d.productoId WHERE d.id = :id")
    java.util.Optional<DetalleFactura> findByIdWithProductoNombre(Long id);
}
