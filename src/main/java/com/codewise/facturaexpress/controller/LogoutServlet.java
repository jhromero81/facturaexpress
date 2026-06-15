package com.codewise.facturaexpress.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet para cerrar la sesión del usuario.
 * Responde a GET /logout, invalida la sesión actual y redirige al login.
 */
public class LogoutServlet extends HttpServlet {

    /**
     * Invalida la sesión HTTP si existe y redirige al formulario de login.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
