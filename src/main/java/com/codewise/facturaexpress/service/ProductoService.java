package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.Producto;
import com.codewise.facturaexpress.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// Servicio que implementa la logica de negocio para la entidad Producto
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Guarda un nuevo producto con validaciones de nombre, precio y stock
    @Transactional
    public Producto guardarProducto(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        return productoRepository.save(producto);
    }

    // Busca un producto por su ID
    public Optional<Producto> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }
        return productoRepository.findById(id);
    }

    // Obtiene todos los productos registrados
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // Actualiza los datos de un producto existente
    @Transactional
    public Producto actualizarProducto(Producto producto) {
        if (producto.getId() == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio para actualizar");
        }
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        return productoRepository.save(producto);
    }

    // Elimina un producto por su ID
    @Transactional
    public void eliminarProducto(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }
        productoRepository.deleteById(id);
    }

    // Descuenta la cantidad del stock de un producto
    @Transactional
    public void descontarStock(Long productoId, int cantidad) {
        int updated = productoRepository.descontarStock(productoId, cantidad);
        if (updated == 0) {
            throw new IllegalStateException("Stock insuficiente para el producto ID: " + productoId);
        }
    }
}
