package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.dao.ProductoDAO;
import com.codewise.facturaexpress.dao.ProductoDAOImpl;
import com.codewise.facturaexpress.model.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService() {
        this.productoDAO = new ProductoDAOImpl();
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
        return productoDAO.guardar(producto);
    }

    public Optional<Producto> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }
        return productoDAO.buscarPorId(id);
    }

    public List<Producto> listarProductos() {
        return productoDAO.listarTodos();
    }

    public Producto actualizarProducto(Producto producto) {
        if (producto.getId() == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio para actualizar");
        }
        return productoDAO.actualizar(producto);
    }

    public void eliminarProducto(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }
        productoDAO.eliminar(id);
    }
}
