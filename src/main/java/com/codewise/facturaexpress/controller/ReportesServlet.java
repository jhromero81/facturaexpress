package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.service.ReportesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet para la página de reportes y estadísticas del negocio.
 * Responde a GET /reportes y carga indicadores como ventas mensuales,
 * top productos, ventas y facturas del mes.
 */
public class ReportesServlet extends HttpServlet {

    private ReportesService reportesService;

    @Override
    public void init() {
        reportesService = new ReportesService();
    }

    /**
     * Carga los datos de reportes (ventas mensuales, top 10 productos,
     * ventas y facturas del mes) y los envía a la vista reportes.jsp.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            req.setAttribute("ventasMensuales", reportesService.ventasMensuales());
            req.setAttribute("topProductos", reportesService.topProductos(10));
            req.setAttribute("ventasMes", reportesService.ventasDelMes());
            req.setAttribute("facturasMes", reportesService.facturasDelMes());
            req.setAttribute("activeNav", "reportes");
            req.setAttribute("pageTitle", "Reportes");
            req.getRequestDispatcher("/WEB-INF/jsp/reportes.jsp").forward(req, resp);
        } catch (Exception e) {
            // Si falla la carga, se muestra el error en la misma página
            req.setAttribute("error", "Error al cargar reportes: " + e.getMessage());
            req.setAttribute("activeNav", "reportes");
            req.setAttribute("pageTitle", "Reportes");
            req.getRequestDispatcher("/WEB-INF/jsp/reportes.jsp").forward(req, resp);
        }
    }
}
