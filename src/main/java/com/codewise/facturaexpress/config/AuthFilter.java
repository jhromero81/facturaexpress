package com.codewise.facturaexpress.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Filtro centralizado de autenticacion y CSRF. Reemplaza las validaciones
// duplicadas que antes existian en cada servlet individualmente.
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI();

        if (isExcluded(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String action = req.getParameter("action");
            if (!"exportarPdf".equals(action)) {
                if (!AuthUtil.validarCsrfToken(req)) {
                    req.setAttribute("error", "Token CSRF invalido");
                    req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isExcluded(String path) {
        return path.equals("/login")
                || path.equals("/logout")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/actuator/");
    }
}
