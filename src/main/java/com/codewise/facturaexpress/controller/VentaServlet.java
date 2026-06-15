package com.codewise.facturaexpress.controller;

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

/**
 * Servlet para el Punto de Venta (POS).
 * Responde a GET /ventas (carga la interfaz POS con clientes y productos) y
 * POST /ventas (búsqueda de clientes o finalización de una venta).
 * La venta se modela como una Factura con sus detalles.
 */
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

    /**
     * Carga la interfaz del punto de venta con la lista de clientes y productos disponibles.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            req.setAttribute("clientes", clienteService.listarClientes());
            req.setAttribute("productos", productoService.listarProductos());
            req.setAttribute("activeNav", "ventas");
            req.setAttribute("pageTitle", "Punto de Venta");
            req.getRequestDispatcher("/WEB-INF/jsp/pos.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cargar POS: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        }
    }

    /**
     * Procesa acciones del POS: búsqueda de clientes por nombre/ID o finalización de una venta.
     * La finalización crea una factura con los productos seleccionados y redirige a confirmación.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            String action = req.getParameter("action");
            // Búsqueda de clientes en el POS por nombre o ID
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
                // Si no hay coincidencias, muestra la lista completa
                req.setAttribute("clientes", filtrados.isEmpty() ? resultados : filtrados);
                req.setAttribute("productos", productoService.listarProductos());
                req.setAttribute("activeNav", "ventas");
                req.setAttribute("pageTitle", "Punto de Venta");
                req.getRequestDispatcher("/WEB-INF/jsp/pos.jsp").forward(req, resp);
                return;
            }

            // Finalización de la venta: crea la factura con los detalles
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

                // Valida que haya al menos un producto en la venta
                if (productosId == null || productosId.length == 0) {
                    throw new IllegalArgumentException("Debe agregar al menos un producto");
                }

                // Construye los detalles de la factura a partir de los arrays del formulario
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

            // Acción desconocida: redirige al POS
            resp.sendRedirect(req.getContextPath() + "/ventas");
        } catch (IllegalArgumentException e) {
            // Error de validación: recarga el POS con el mensaje de error
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

    /**
     * Recarga los datos de clientes y productos en la request para re-poblar el POS
     * después de un error de validación.
     */
    private void cargarDatos(HttpServletRequest req) {
        try {
            req.setAttribute("clientes", clienteService.listarClientes());
            req.setAttribute("productos", productoService.listarProductos());
        } catch (Exception e) {
            // Si falla la recarga se omite silenciosamente
        }
    }
}
