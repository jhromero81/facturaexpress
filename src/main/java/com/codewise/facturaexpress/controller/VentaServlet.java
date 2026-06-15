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

    private FacturaService facturaService;
    private ClienteService clienteService;
    private ProductoService productoService;

    @Override
    public void init() {
        facturaService = new FacturaService();
        clienteService = new ClienteService();
        productoService = new ProductoService();
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
            if ("buscarCliente".equals(action)) {
                String termino = req.getParameter("termino");
                List<Cliente> resultados = clienteService.listarClientes();
                List<Cliente> filtrados = new ArrayList<>();
                if (termino != null && !termino.trim().isEmpty()) {
                    for (Cliente c : resultados) {
                        if (c.getNombre().toLowerCase().contains(termino.toLowerCase()) ||
                            (c.getId().toString().equals(termino.trim()))) {
                            filtrados.add(c);
                        }
                    }
                }
                req.setAttribute("clientes", filtrados.isEmpty() ? resultados : filtrados);
                req.setAttribute("productos", productoService.listarProductos());
                req.setAttribute("activeNav", "ventas");
                req.setAttribute("pageTitle", "Punto de Venta");
                req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
                req.getRequestDispatcher("/WEB-INF/jsp/pos.jsp").forward(req, resp);
                return;
            }

            if ("finalizar".equals(action)) {
                String clienteIdParam = req.getParameter("clienteId");
                String[] productosId = req.getParameterValues("productoId");
                String[] cantidades = req.getParameterValues("cantidad");
                String[] precios = req.getParameterValues("precioUnitario");

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
                facturaService.crearFactura(factura);

                req.setAttribute("mensaje", "Venta registrada exitosamente");
                req.setAttribute("redirectUrl", req.getContextPath() + "/ventas");
                req.getRequestDispatcher("/WEB-INF/jsp/confirmacion.jsp").forward(req, resp);
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/ventas");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Error en el formato de los numeros ingresados");
            cargarDatos(req);
            req.setAttribute("activeNav", "ventas");
            req.setAttribute("pageTitle", "Punto de Venta");
            req.getRequestDispatcher("/WEB-INF/jsp/pos.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            cargarDatos(req);
            req.setAttribute("activeNav", "ventas");
            req.setAttribute("pageTitle", "Punto de Venta");
            req.getRequestDispatcher("/WEB-INF/jsp/pos.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al procesar venta: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    private void cargarDatos(HttpServletRequest req) {
        try {
            req.setAttribute("clientes", clienteService.listarClientes());
            req.setAttribute("productos", productoService.listarProductos());
        } catch (Exception e) {
        }
    }
}
