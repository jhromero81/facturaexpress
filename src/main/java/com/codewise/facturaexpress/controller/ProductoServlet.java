package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.model.Producto;
import com.codewise.facturaexpress.service.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servlet para el CRUD de productos.
 * Responde a GET /productos (listar, nuevo, editar, eliminar) y
 * POST /productos (guardar, actualizar).
 */
public class ProductoServlet extends HttpServlet {

    private ProductoService productoService;

    @Override
    public void init() {
        productoService = new ProductoService();
    }

    /**
     * Maneja las acciones de lectura sobre productos:
     * listar (default), mostrar formulario nuevo, mostrar formulario edición, o eliminar.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("activeNav", "productos");
        req.setAttribute("pageTitle", "Productos");
        String action = req.getParameter("action");
        if (action == null) action = "listar";

        switch (action) {
            case "nuevo":
                req.getRequestDispatcher("/WEB-INF/jsp/producto-form.jsp").forward(req, resp);
                break;
            case "editar":
                mostrarFormularioEdicion(req, resp);
                break;
            case "eliminar":
                eliminarProducto(req, resp);
                break;
            default:
                listarProductos(req, resp);
        }
    }

    /**
     * Maneja las acciones de escritura sobre productos: guardar (crear) o actualizar.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("activeNav", "productos");
        req.setAttribute("pageTitle", "Productos");
        String action = req.getParameter("action");
        if ("guardar".equals(action)) {
            guardarProducto(req, resp);
        } else if ("actualizar".equals(action)) {
            actualizarProducto(req, resp);
        }
    }

    /**
     * Obtiene todos los productos y los envía a la vista de listado.
     */
    private void listarProductos(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Producto> productos = productoService.listarProductos();
            req.setAttribute("productos", productos);
            req.getRequestDispatcher("/WEB-INF/jsp/productos.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al listar productos: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Busca un producto por ID y carga sus datos en el formulario de edición.
     */
    private void mostrarFormularioEdicion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Optional<Producto> productoOpt = productoService.buscarPorId(id);
            if (productoOpt.isPresent()) {
                req.setAttribute("producto", productoOpt.get());
                req.getRequestDispatcher("/WEB-INF/jsp/producto-form.jsp").forward(req, resp);
            } else {
                req.setAttribute("error", "Producto no encontrado con ID: " + id);
                req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID de producto invalido");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Crea un nuevo producto con los datos del formulario y lo persiste.
     */
    private void guardarProducto(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Producto producto = new Producto();
            producto.setNombre(req.getParameter("nombre"));
            producto.setDescripcion(req.getParameter("descripcion"));
            producto.setPrecio(new BigDecimal(req.getParameter("precio")));
            producto.setStock(Integer.parseInt(req.getParameter("stock")));

            productoService.guardarProducto(producto);
            req.setAttribute("mensaje", "Producto registrado exitosamente");
            req.setAttribute("redirectUrl", req.getContextPath() + "/productos");
            req.getRequestDispatcher("/WEB-INF/jsp/confirmacion.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            // Error de validación: regresa al formulario con el mensaje
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/producto-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al guardar producto: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Actualiza un producto existente identificado por su ID.
     */
    private void actualizarProducto(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Producto producto = new Producto();
            producto.setId(Long.parseLong(req.getParameter("id")));
            producto.setNombre(req.getParameter("nombre"));
            producto.setDescripcion(req.getParameter("descripcion"));
            producto.setPrecio(new BigDecimal(req.getParameter("precio")));
            producto.setStock(Integer.parseInt(req.getParameter("stock")));

            productoService.actualizarProducto(producto);
            resp.sendRedirect(req.getContextPath() + "/productos");
        } catch (IllegalArgumentException e) {
            // Error de validación: regresa al formulario con el mensaje
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/producto-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al actualizar producto: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Elimina un producto por ID y redirige al listado.
     */
    private void eliminarProducto(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            productoService.eliminarProducto(id);
            resp.sendRedirect(req.getContextPath() + "/productos");
        } catch (Exception e) {
            req.setAttribute("error", "Error al eliminar producto: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }
}
