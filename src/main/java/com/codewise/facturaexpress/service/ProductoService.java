package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.dao.ProductoDAO;
import com.codewise.facturaexpress.dao.ProductoDAOImpl;
import com.codewise.facturaexpress.model.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio CRUD para productos con validaciones de negocio:
 * nombre obligatorio, precio mayor a cero y stock no negativo.
 */
public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService() {
        this.productoDAO = new ProductoDAOImpl();
    }

    /**
     * Guarda un nuevo producto tras validar nombre, precio (> 0) y stock (>= 0).
     */
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

    /**
     * Busca un producto por su ID.
     */
    public Optional<Producto> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }
        return productoDAO.buscarPorId(id);
    }

    /**
     * Retorna la lista completa de productos.
     */
    public List<Producto> listarProductos() {
        return productoDAO.listarTodos();
    }

    /**
     * Actualiza un producto existente. El ID es obligatorio.
     */
    public Producto actualizarProducto(Producto producto) {
        if (producto.getId() == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio para actualizar");
        }
        return productoDAO.actualizar(producto);
    }

    /**
     * Elimina un producto por su ID.
     */
    public void eliminarProducto(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto invalido");
        }
        productoDAO.eliminar(id);
    }
}
