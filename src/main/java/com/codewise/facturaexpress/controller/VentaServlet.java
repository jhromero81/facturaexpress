package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
import com.codewise.facturaexpress.model.*;
import com.codewise.facturaexpress.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VentaServlet extends HttpServlet {

    private final FacturaService facturaService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final LogAuditoriaService logService;

    public VentaServlet(FacturaService facturaService, ClienteService clienteService,
                        ProductoService productoService, LogAuditoriaService logService) {
        this.facturaService = facturaService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.logService = logService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            req.setAttribute("clientes", clienteService.listarClientes());
            req.setAttribute("productos", productoService.listarProductos());
            req.setAttribute("activeNav", "ventas");
            req.setAttribute("pageTitle", "Punto de Venta");
            req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/jsp/pos.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cargar POS: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
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
        try {
            String action = req.getParameter("action");
            if ("finalizar".equals(action)) {
                String clienteIdParam = req.getParameter("clienteId");
                String[] productosId = req.getParameterValues("productoId");
                String[] cantidades = req.getParameterValues("cantidad");
                String[] precios = req.getParameterValues("precioUnitario");
                String dtoParam = req.getParameter("descuentoPorcentaje");
                int descuentoPorcentaje = (dtoParam != null && !dtoParam.isEmpty()) ? Integer.parseInt(dtoParam) : 0;

                Factura factura = new Factura();
                if (clienteIdParam != null && !clienteIdParam.isEmpty()) {
                    factura.setClienteId(Long.parseLong(clienteIdParam));
                } else {
                    throw new IllegalArgumentException("Debe seleccionar un cliente");
                }

                if (productosId == null || productosId.length == 0) {
                    throw new IllegalArgumentException("Debe agregar al menos un producto");
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
                Factura creada = facturaService.crearFactura(factura, descuentoPorcentaje);
                Usuario usuario = AuthUtil.getUsuario(req);
                logService.registrar(usuario, "INSERT venta factura id=" + creada.getId(), "facturas", creada.getId(), req);

                req.setAttribute("mensaje", "Venta registrada exitosamente");
                req.setAttribute("redirectUrl", req.getContextPath() + "/ventas");
                req.getRequestDispatcher("/WEB-INF/jsp/confirmacion.jsp").forward(req, resp);
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/ventas");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Error en el formato de los numeros ingresados");
            req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
            cargarDatos(req);
            req.setAttribute("activeNav", "ventas");
            req.setAttribute("pageTitle", "Punto de Venta");
            req.getRequestDispatcher("/WEB-INF/jsp/pos.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
            cargarDatos(req);
            req.setAttribute("activeNav", "ventas");
            req.setAttribute("pageTitle", "Punto de Venta");
            req.getRequestDispatcher("/WEB-INF/jsp/pos.jsp").forward(req, resp);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("Error al procesar venta: ").append(e.getMessage());
            for (Throwable c = e.getCause(); c != null; c = c.getCause()) {
                sb.append(" | causa: ").append(c.getMessage());
            }
            req.setAttribute("error", sb.toString());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private void cargarDatos(HttpServletRequest req) {
        try {
            req.setAttribute("clientes", clienteService.listarClientes());
            req.setAttribute("productos", productoService.listarProductos());
        } catch (Exception e) {
            System.err.println("Error al cargar datos para POS: " + e.getMessage());
        }
    }
}
