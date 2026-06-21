package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
import com.codewise.facturaexpress.model.Cliente;
import com.codewise.facturaexpress.model.DetalleFactura;
import com.codewise.facturaexpress.model.Factura;
import com.codewise.facturaexpress.model.Producto;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.service.ClienteService;
import com.codewise.facturaexpress.service.FacturaService;
import com.codewise.facturaexpress.service.LogAuditoriaService;
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

public class FacturaServlet extends HttpServlet {

    private FacturaService facturaService;
    private ClienteService clienteService;
    private ProductoService productoService;
    private LogAuditoriaService logService;

    @Override
    public void init() {
        facturaService = new FacturaService();
        clienteService = new ClienteService();
        productoService = new ProductoService();
        logService = new LogAuditoriaService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("activeNav", "facturas");
        req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
        String action = req.getParameter("action");
        if (action == null) action = "listar";

        switch (action) {
            case "nuevo":
                req.setAttribute("pageTitle", "Nueva Factura");
                mostrarFormularioCreacion(req, resp);
                break;
            case "detalle":
                req.setAttribute("pageTitle", "Detalle Factura");
                mostrarDetalle(req, resp);
                break;
            case "eliminar":
                req.setAttribute("pageTitle", "Facturas");
                eliminarFactura(req, resp);
                break;
            default:
                req.setAttribute("pageTitle", "Historial de Facturacion");
                listarFacturas(req, resp);
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
        req.setAttribute("activeNav", "facturas");
        req.setAttribute("pageTitle", "Facturas");
        String action = req.getParameter("action");
        if ("guardar".equals(action)) {
            guardarFactura(req, resp);
        } else if ("cambiarEstado".equals(action)) {
            cambiarEstado(req, resp);
        }
    }

    private void listarFacturas(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Factura> facturas = facturaService.listarFacturas();
            req.setAttribute("facturas", facturas);
            req.getRequestDispatcher("/WEB-INF/jsp/facturas.jsp").forward(req, resp);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("Error al listar facturas: ").append(e.getMessage());
            for (Throwable c = e.getCause(); c != null; c = c.getCause()) {
                sb.append(" | causa: ").append(c.getMessage());
            }
            req.setAttribute("error", sb.toString());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

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

    private void guardarFactura(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Factura factura = new Factura();
            String clienteIdStr = req.getParameter("clienteId");
            if (clienteIdStr == null || clienteIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Debe seleccionar un cliente");
            }
            factura.setClienteId(Long.parseLong(clienteIdStr));

            String[] productosId = req.getParameterValues("productoId");
            String[] cantidades = req.getParameterValues("cantidad");
            String[] precios = req.getParameterValues("precioUnitario");

            if (productosId == null || productosId.length == 0) {
                throw new IllegalArgumentException("Debe agregar al menos un detalle a la factura");
            }

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
            Factura creada = facturaService.crearFactura(factura);
            Usuario usuario = AuthUtil.getUsuario(req);
            logService.registrar(usuario, "INSERT factura id=" + creada.getId(), "facturas", creada.getId(), req);

            req.setAttribute("mensaje", "Factura creada exitosamente");
            req.setAttribute("redirectUrl", req.getContextPath() + "/facturas");
            req.getRequestDispatcher("/WEB-INF/jsp/confirmacion.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Error en el formato de los numeros ingresados");
            cargarDatosFormulario(req);
            req.getRequestDispatcher("/WEB-INF/jsp/factura-form.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            cargarDatosFormulario(req);
            req.getRequestDispatcher("/WEB-INF/jsp/factura-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al guardar factura: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private void cambiarEstado(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            String nuevoEstado = req.getParameter("estado");
            facturaService.actualizarEstado(id, nuevoEstado);
            Usuario usuario = AuthUtil.getUsuario(req);
            logService.registrar(usuario, "UPDATE factura id=" + id + " estado=" + nuevoEstado, "facturas", id, req);
            resp.sendRedirect(req.getContextPath() + "/facturas?action=detalle&id=" + id);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID de factura invalido");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cambiar estado: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private void eliminarFactura(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            facturaService.eliminarFactura(id);
            Usuario usuario = AuthUtil.getUsuario(req);
            logService.registrar(usuario, "DELETE factura id=" + id, "facturas", id, req);
            resp.sendRedirect(req.getContextPath() + "/facturas");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID de factura invalido");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al eliminar factura: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private void cargarDatosFormulario(HttpServletRequest req) {
        try {
            req.setAttribute("clientes", clienteService.listarClientes());
            req.setAttribute("productos", productoService.listarProductos());
            req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
        } catch (Exception e) {
            System.err.println("Error al cargar datos del formulario: " + e.getMessage());
        }
    }
}
