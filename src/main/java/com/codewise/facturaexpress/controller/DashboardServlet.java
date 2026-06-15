package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.service.ReportesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servlet para la página principal del panel de control (dashboard).
 * Responde a GET /dashboard y carga los indicadores resumidos del negocio
 * (facturas/ventas del día, semana, mes, ticket promedio y top productos).
 */
public class DashboardServlet extends HttpServlet {

    private ReportesService reportesService;

    @Override
    public void init() {
        reportesService = new ReportesService();
    }

    /**
     * Carga todos los indicadores del dashboard desde ReportesService
     * y los envía a la vista dashboard.jsp. Si ocurre un error, lo muestra
     * en la misma página.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Verifica que el usuario tenga sesión activa
        if (req.getSession().getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            // Indicadores del día, semana y mes
            req.setAttribute("facturasDia", reportesService.facturasDelDia());
            req.setAttribute("ventasDia", reportesService.ventasDelDia());
            req.setAttribute("ticketPromedio", reportesService.ticketPromedio());
            req.setAttribute("facturasMes", reportesService.facturasDelMes());
            req.setAttribute("ventasMes", reportesService.ventasDelMes());
            req.setAttribute("ventasSemana", reportesService.ventasSemana());
            // Top 5 productos más vendidos
            req.setAttribute("topProductos", reportesService.topProductos(5));
            req.setAttribute("activeNav", "dashboard");
            req.setAttribute("pageTitle", "Dashboard");
            req.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            // Si falla la carga, se muestra el error en la misma página
            req.setAttribute("error", "Error al cargar dashboard: " + e.getMessage());
            req.setAttribute("activeNav", "dashboard");
            req.setAttribute("pageTitle", "Dashboard");
            req.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(req, resp);
        }
    }
}
