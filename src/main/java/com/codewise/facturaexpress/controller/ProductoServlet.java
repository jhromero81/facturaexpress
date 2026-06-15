package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
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

public class ProductoServlet extends HttpServlet {

    private ProductoService productoService;

    @Override
    public void init() {
        productoService = new ProductoService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("activeNav", "productos");
        req.setAttribute("pageTitle", "Productos");
        req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!AuthUtil.validarCsrfToken(req)) {
            req.setAttribute("error", "Token CSRF invalido");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
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

    private void guardarProducto(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Producto producto = new Producto();
            producto.setNombre(req.getParameter("nombre"));
            producto.setDescripcion(req.getParameter("descripcion"));
            String precioStr = req.getParameter("precio");
            if (precioStr == null || precioStr.trim().isEmpty()) {
                throw new IllegalArgumentException("El precio es obligatorio");
            }
            producto.setPrecio(new BigDecimal(precioStr));
            String stockStr = req.getParameter("stock");
            if (stockStr == null || stockStr.trim().isEmpty()) {
                throw new IllegalArgumentException("El stock es obligatorio");
            }
            producto.setStock(Integer.parseInt(stockStr));

            productoService.guardarProducto(producto);
            req.setAttribute("mensaje", "Producto registrado exitosamente");
            req.setAttribute("redirectUrl", req.getContextPath() + "/productos");
            req.getRequestDispatcher("/WEB-INF/jsp/confirmacion.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Precio o stock invalido: deben ser numeros");
            req.getRequestDispatcher("/WEB-INF/jsp/producto-form.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/producto-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al guardar producto: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private void actualizarProducto(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Producto producto = new Producto();
            producto.setId(Long.parseLong(req.getParameter("id")));
            producto.setNombre(req.getParameter("nombre"));
            producto.setDescripcion(req.getParameter("descripcion"));
            String precioStr = req.getParameter("precio");
            if (precioStr == null || precioStr.trim().isEmpty()) {
                throw new IllegalArgumentException("El precio es obligatorio");
            }
            producto.setPrecio(new BigDecimal(precioStr));
            String stockStr = req.getParameter("stock");
            if (stockStr == null || stockStr.trim().isEmpty()) {
                throw new IllegalArgumentException("El stock es obligatorio");
            }
            producto.setStock(Integer.parseInt(stockStr));

            productoService.actualizarProducto(producto);
            resp.sendRedirect(req.getContextPath() + "/productos");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Precio o stock invalido: deben ser numeros");
            req.getRequestDispatcher("/WEB-INF/jsp/producto-form.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/producto-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al actualizar producto: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private void eliminarProducto(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            productoService.eliminarProducto(id);
            resp.sendRedirect(req.getContextPath() + "/productos");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID de producto invalido");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al eliminar producto: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }
}
