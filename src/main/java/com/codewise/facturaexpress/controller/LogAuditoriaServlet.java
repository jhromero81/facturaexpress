package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.service.LogAuditoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Servlet para consultar los registros de auditoria del sistema
public class LogAuditoriaServlet extends HttpServlet {

    private final LogAuditoriaService logService;

    public LogAuditoriaServlet(LogAuditoriaService logService) {
        this.logService = logService;
    }

    // Lista los logs con opción de filtrar por tabla o por usuario
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("activeNav", "logs");
        req.setAttribute("pageTitle", "Auditoría");
        try {
            String tabla = req.getParameter("tabla");
            String usuarioId = req.getParameter("usuarioId");
            if (tabla != null && !tabla.isEmpty()) {
                req.setAttribute("logs", logService.listarPorTabla(tabla));
            } else if (usuarioId != null && !usuarioId.isEmpty()) {
                try {
                    req.setAttribute("logs", logService.listarPorUsuarioId(Long.parseLong(usuarioId)));
                } catch (NumberFormatException e) {
                    req.setAttribute("error", "ID de usuario invalido: " + usuarioId);
                    req.getRequestDispatcher("/WEB-INF/jsp/logs-auditoria.jsp").forward(req, resp);
                    return;
                }
            } else {
                req.setAttribute("logs", logService.listarTodos());
            }
            req.getRequestDispatcher("/WEB-INF/jsp/logs-auditoria.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cargar logs: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/logs-auditoria.jsp").forward(req, resp);
        }
    }
}
