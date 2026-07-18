package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
import com.codewise.facturaexpress.model.Producto;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.service.LogAuditoriaService;
import com.codewise.facturaexpress.service.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// Servlet para el CRUD de productos
public class ProductoServlet extends HttpServlet {

    private final ProductoService productoService;
    private final LogAuditoriaService logService;

    public ProductoServlet(ProductoService productoService, LogAuditoriaService logService) {
        this.productoService = productoService;
        this.logService = logService;
    }

    // Maneja GET: listar productos, mostrar formulario nuevo, editar o eliminar
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
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

    // Maneja POST: guardar o actualizar producto
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("activeNav", "productos");
        req.setAttribute("pageTitle", "Productos");
        String action = req.getParameter("action");
        if ("guardar".equals(action)) {
            guardarProducto(req, resp);
        } else if ("actualizar".equals(action)) {
            actualizarProducto(req, resp);
        }
    }

    // Obtiene la lista de productos y la envía a la vista
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

    // Muestra el formulario de edición con los datos del producto
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

    // Guarda un nuevo producto en la base de datos
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

            Producto guardado = productoService.guardarProducto(producto);
            Usuario usuario = AuthUtil.getUsuario(req);
            logService.registrar(usuario, "INSERT producto id=" + guardado.getId(), "productos", guardado.getId(), req);
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

    // Actualiza los datos de un producto existente
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
            Usuario usuario = AuthUtil.getUsuario(req);
            logService.registrar(usuario, "UPDATE producto id=" + producto.getId(), "productos", producto.getId(), req);
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

    // Elimina un producto por su ID
    private void eliminarProducto(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            productoService.eliminarProducto(id);
            Usuario usuario = AuthUtil.getUsuario(req);
            logService.registrar(usuario, "DELETE producto id=" + id, "productos", id, req);
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
