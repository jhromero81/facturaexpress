package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.service.ReportesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Servlet que muestra el panel principal con indicadores y resúmenes
public class DashboardServlet extends HttpServlet {

    private final ReportesService reportesService;

    public DashboardServlet(ReportesService reportesService) {
        this.reportesService = reportesService;
    }

    // Carga los indicadores del dashboard y los envia a la vista
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("facturasDia", reportesService.facturasDelDia());
            req.setAttribute("ventasDia", reportesService.ventasDelDia());
            req.setAttribute("ticketPromedio", reportesService.ticketPromedio());
            req.setAttribute("facturasMes", reportesService.facturasDelMes());
            req.setAttribute("ventasMes", reportesService.ventasDelMes());
            req.setAttribute("ventasSemana", reportesService.ventasSemana());
            req.setAttribute("topProductos", reportesService.topProductos(5));
            req.setAttribute("ultimasTransacciones", reportesService.ultimasTransacciones(5));
            req.setAttribute("activeNav", "dashboard");
            req.setAttribute("pageTitle", "Dashboard");
            req.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cargar dashboard: " + e.getMessage());
            req.setAttribute("activeNav", "dashboard");
            req.setAttribute("pageTitle", "Dashboard");
            req.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(req, resp);
        }
    }
}
