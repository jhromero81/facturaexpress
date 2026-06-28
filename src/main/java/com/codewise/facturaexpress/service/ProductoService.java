package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.Producto;
import com.codewise.facturaexpress.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

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

    public Optional<Producto> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }
        return productoRepository.findById(id);
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

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

    public void eliminarProducto(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }
        productoRepository.deleteById(id);
    }

    public void descontarStock(Long productoId, int cantidad) {
        int updated = productoRepository.descontarStock(productoId, cantidad);
        if (updated == 0) {
            throw new IllegalStateException("Stock insuficiente para el producto ID: " + productoId);
        }
    }
}
