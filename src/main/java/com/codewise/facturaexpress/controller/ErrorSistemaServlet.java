package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.service.ErrorSistemaService;
import com.codewise.facturaexpress.service.LogAuditoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Servlet para gestionar los errores del sistema
public class ErrorSistemaServlet extends HttpServlet {

    private final ErrorSistemaService errorService;
    private final LogAuditoriaService logService;

    public ErrorSistemaServlet(ErrorSistemaService errorService, LogAuditoriaService logService) {
        this.errorService = errorService;
        this.logService = logService;
    }

    // Lista los errores y permite filtrar por tipo o marcar como resueltos
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("activeNav", "errores");
        req.setAttribute("pageTitle", "Errores del Sistema");
        req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
        String action = req.getParameter("action");

        if ("resolver".equals(action)) {
            marcarResuelto(req, resp);
            return;
        }

        try {
            String tipo = req.getParameter("tipo");
            if (tipo != null && !tipo.isEmpty()) {
                req.setAttribute("errores", errorService.listarPorTipo(tipo));
            } else {
                req.setAttribute("errores", errorService.listarErrores());
            }
            req.setAttribute("noResueltos", errorService.listarNoResueltos());
            req.getRequestDispatcher("/WEB-INF/jsp/errores.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cargar errores: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/errores.jsp").forward(req, resp);
        }
    }

    // Marca un error como resuelto
    private void marcarResuelto(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            Long id = Long.parseLong(req.getParameter("id"));
            errorService.marcarResuelto(id);
            Usuario usuario = AuthUtil.getUsuario(req);
            logService.registrar(usuario, "RESOLVER error_sistema id=" + id, "errores_sistema", id, req);
            resp.sendRedirect(req.getContextPath() + "/errores");
        } catch (Exception e) {
            req.setAttribute("error", "Error al resolver error: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/errores.jsp").forward(req, resp);
        }
    }
}
