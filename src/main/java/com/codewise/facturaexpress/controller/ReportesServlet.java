package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
import com.codewise.facturaexpress.service.ReportesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ReportesServlet extends HttpServlet {

    private ReportesService reportesService;

    @Override
    public void init() {
        reportesService = new ReportesService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
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
            req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/jsp/reportes.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cargar reportes: " + e.getMessage());
            req.setAttribute("activeNav", "reportes");
            req.setAttribute("pageTitle", "Reportes");
            req.getRequestDispatcher("/WEB-INF/jsp/reportes.jsp").forward(req, resp);
        }
    }
}
