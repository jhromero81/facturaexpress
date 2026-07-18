package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.service.PdfService;
import com.codewise.facturaexpress.service.ReporteService;
import com.codewise.facturaexpress.service.ReportesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

// Servlet para generar y exportar reportes de ventas
public class ReportesServlet extends HttpServlet {

    private final ReportesService reportesService;
    private final ReporteService reporteService;
    private final PdfService pdfService;

    public ReportesServlet(ReportesService reportesService, ReporteService reporteService, PdfService pdfService) {
        this.reportesService = reportesService;
        this.reporteService = reporteService;
        this.pdfService = pdfService;
    }

    // Carga los datos de reportes y los envía a la vista
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("ventasMensuales", reportesService.ventasMensuales());
            req.setAttribute("ventasTrimestrales", reportesService.ventasTrimestrales());
            req.setAttribute("ventasAnuales", reportesService.ventasAnuales());
            req.setAttribute("topProductos", reportesService.topProductos(10));
            req.setAttribute("ventasMes", reportesService.ventasDelMes());
            req.setAttribute("facturasMes", reportesService.facturasDelMes());
            req.setAttribute("ventasDia", reportesService.ventasDelDia());
            req.setAttribute("facturasDia", reportesService.facturasDelDia());
            req.setAttribute("ticketPromedio", reportesService.ticketPromedio());
            req.setAttribute("ventasSemana", reportesService.ventasSemana());
            req.setAttribute("reportesGuardados", reporteService.listarReportes());
            req.setAttribute("activeNav", "reportes");
            req.setAttribute("pageTitle", "Reportes");
            req.getRequestDispatcher("/WEB-INF/jsp/reportes.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cargar reportes: " + e.getMessage());
            req.setAttribute("activeNav", "reportes");
            req.setAttribute("pageTitle", "Reportes");
            req.getRequestDispatcher("/WEB-INF/jsp/reportes.jsp").forward(req, resp);
        }
    }

    // Maneja POST: generar un reporte o exportar a PDF
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("generar".equals(action)) {
            try {
                String tipo = req.getParameter("tipo");
                LocalDate inicio = LocalDate.parse(req.getParameter("fechaInicio"));
                LocalDate fin = LocalDate.parse(req.getParameter("fechaFin"));
                Usuario usuario = AuthUtil.getUsuario(req);
                reporteService.generarReporte(tipo, inicio, fin, usuario.getId());
                resp.sendRedirect(req.getContextPath() + "/reportes");
            } catch (Exception e) {
                req.setAttribute("error", "Error al generar reporte: " + e.getMessage());
                doGet(req, resp);
            }
        } else if ("exportarPdf".equals(action)) {
            // Exporta los datos del reporte a PDF
            try {
                String chartBase64 = req.getParameter("chartImage");
                String periodo = req.getParameter("periodo");
                byte[] chartBytes = null;
                if (chartBase64 != null && !chartBase64.isEmpty()) {
                    String raw = chartBase64.contains(",") ? chartBase64.split(",")[1] : chartBase64;
                    chartBytes = Base64.getDecoder().decode(raw);
                }

                BigDecimal ventasMes = reportesService.ventasDelMes();
                Integer facturasMes = reportesService.facturasDelMes();
                BigDecimal ventasDia = reportesService.ventasDelDia();
                BigDecimal ticketPromedio = reportesService.ticketPromedio();
                List<Map<String, Object>> topProductos = reportesService.topProductos(10);

                byte[] pdf = pdfService.generarPdfReporte(
                        periodo != null ? periodo : "Resumen",
                        chartBytes, ventasMes, facturasMes,
                        ventasDia, ticketPromedio, topProductos
                );

                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "attachment; filename=\"reporte_ventas_" + LocalDate.now() + ".pdf\"");
                resp.setContentLength(pdf.length);
                resp.getOutputStream().write(pdf);
                resp.getOutputStream().flush();
            } catch (Exception e) {
                req.setAttribute("error", "Error al exportar PDF: " + e.getMessage());
                doGet(req, resp);
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/reportes");
        }
    }
}
