package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Repositorio JPA para la entidad Producto
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Descuenta una cantidad del stock de un producto si hay suficiente
    @Modifying
    @Query("UPDATE Producto p SET p.stock = p.stock - :cantidad WHERE p.id = :id AND p.stock >= :cantidad")
    int descontarStock(@Param("id") Long productoId, @Param("cantidad") int cantidad);
}
