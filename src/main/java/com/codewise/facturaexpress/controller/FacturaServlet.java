package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.model.Cliente;
import com.codewise.facturaexpress.model.DetalleFactura;
import com.codewise.facturaexpress.model.Factura;
import com.codewise.facturaexpress.model.Producto;
import com.codewise.facturaexpress.service.ClienteService;
import com.codewise.facturaexpress.service.FacturaService;
import com.codewise.facturaexpress.service.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servlet para la gestión de facturas (CRUD + cambio de estado).
 * Responde a GET /facturas (listar, nuevo, detalle, eliminar) y
 * POST /facturas (guardar, cambiarEstado).
 */
public class FacturaServlet extends HttpServlet {

    private FacturaService facturaService;
    private ClienteService clienteService;
    private ProductoService productoService;

    @Override
    public void init() {
        facturaService = new FacturaService();
        clienteService = new ClienteService();
        productoService = new ProductoService();
    }

    /**
     * Maneja las acciones de lectura sobre facturas:
     * listar (default), mostrar formulario de creación, ver detalle, o eliminar.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("activeNav", "facturas");
        req.setAttribute("pageTitle", "Facturas");
        String action = req.getParameter("action");
        if (action == null) action = "listar";

        switch (action) {
            case "nuevo":
                mostrarFormularioCreacion(req, resp);
                break;
            case "detalle":
                mostrarDetalle(req, resp);
                break;
            case "eliminar":
                eliminarFactura(req, resp);
                break;
            default:
                listarFacturas(req, resp);
        }
    }

    /**
     * Maneja las acciones de escritura sobre facturas: guardar (crear) o cambiarEstado.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("activeNav", "facturas");
        req.setAttribute("pageTitle", "Facturas");
        String action = req.getParameter("action");
        if ("guardar".equals(action)) {
            guardarFactura(req, resp);
        } else if ("cambiarEstado".equals(action)) {
            cambiarEstado(req, resp);
        }
    }

    /**
     * Obtiene todas las facturas y las envía a la vista de listado.
     */
    private void listarFacturas(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Factura> facturas = facturaService.listarFacturas();
            req.setAttribute("facturas", facturas);
            req.getRequestDispatcher("/WEB-INF/jsp/facturas.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al listar facturas: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Prepara el formulario de creación de factura cargando clientes y productos disponibles.
     */
    private void mostrarFormularioCreacion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Cliente> clientes = clienteService.listarClientes();
            List<Producto> productos = productoService.listarProductos();
            req.setAttribute("clientes", clientes);
            req.setAttribute("productos", productos);
            req.getRequestDispatcher("/WEB-INF/jsp/factura-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cargar formulario: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Muestra el detalle de una factura específica, incluyendo sus líneas de detalle.
     */
    private void mostrarDetalle(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            Optional<Factura> facturaOpt = facturaService.buscarPorId(id);
            if (facturaOpt.isPresent()) {
                req.setAttribute("factura", facturaOpt.get());
                req.getRequestDispatcher("/WEB-INF/jsp/factura-detalle.jsp").forward(req, resp);
            } else {
                req.setAttribute("error", "Factura no encontrada con ID: " + id);
                req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID de factura invalido");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Crea una nueva factura con cliente y líneas de detalle enviadas desde el formulario.
     * Las líneas de detalle llegan como arrays de parámetros (productoId, cantidad, precioUnitario).
     */
    private void guardarFactura(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Factura factura = new Factura();
            factura.setClienteId(Long.parseLong(req.getParameter("clienteId")));

            // Recupera los arrays de detalles del formulario
            String[] productosId = req.getParameterValues("productoId");
            String[] cantidades = req.getParameterValues("cantidad");
            String[] precios = req.getParameterValues("precioUnitario");

            // Valida que haya al menos un producto en la factura
            if (productosId == null || productosId.length == 0) {
                throw new IllegalArgumentException("Debe agregar al menos un detalle a la factura");
            }

            // Construye la lista de detalles a partir de los parámetros
            List<DetalleFactura> detalles = new ArrayList<>();
            for (int i = 0; i < productosId.length; i++) {
                if (productosId[i] == null || productosId[i].isEmpty()) continue;
                DetalleFactura detalle = new DetalleFactura();
                detalle.setProductoId(Long.parseLong(productosId[i]));
                detalle.setCantidad(Integer.parseInt(cantidades[i]));
                detalle.setPrecioUnitario(new BigDecimal(precios[i]));
                detalles.add(detalle);
            }

            factura.setDetalles(detalles);
            facturaService.crearFactura(factura);

            req.setAttribute("mensaje", "Factura creada exitosamente");
            req.setAttribute("redirectUrl", req.getContextPath() + "/facturas");
            req.getRequestDispatcher("/WEB-INF/jsp/confirmacion.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            // Error de validación: recarga los datos del formulario y muestra el error
            req.setAttribute("error", e.getMessage());
            cargarDatosFormulario(req);
            req.getRequestDispatcher("/WEB-INF/jsp/factura-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al guardar factura: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Cambia el estado de una factura (ej: PENDIENTE, PAGADA, ANULADA).
     */
    private void cambiarEstado(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            String nuevoEstado = req.getParameter("estado");
            facturaService.actualizarEstado(id, nuevoEstado);
            resp.sendRedirect(req.getContextPath() + "/facturas?action=detalle&id=" + id);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cambiar estado: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Elimina una factura por ID y redirige al listado.
     */
    private void eliminarFactura(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            facturaService.eliminarFactura(id);
            resp.sendRedirect(req.getContextPath() + "/facturas");
        } catch (Exception e) {
            req.setAttribute("error", "Error al eliminar factura: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Carga clientes y productos en la request para re-poblar el formulario
     * después de un error de validación.
     */
    private void cargarDatosFormulario(HttpServletRequest req) {
        try {
            req.setAttribute("clientes", clienteService.listarClientes());
            req.setAttribute("productos", productoService.listarProductos());
        } catch (Exception e) {
            // Si falla la recarga se omite silenciosamente
        }
    }
}
