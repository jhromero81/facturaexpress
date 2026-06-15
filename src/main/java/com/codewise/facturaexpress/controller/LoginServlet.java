package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.service.LoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * Servlet para la autenticación de usuarios.
 * Maneja GET /login (muestra el formulario) y POST /login (procesa el login).
 */
public class LoginServlet extends HttpServlet {

    private LoginService loginService;

    @Override
    public void init() {
        loginService = new LoginService();
    }

    /**
     * Muestra el formulario de inicio de sesión.
     * Si el usuario ya tiene sesión activa, redirige al dashboard.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Si ya hay sesión iniciada, evitar mostrar el login
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    /**
     * Procesa las credenciales enviadas desde el formulario de login.
     * Si la autenticación es exitosa crea la sesión y redirige al dashboard;
     * en caso contrario muestra un mensaje de error en la misma página.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Optional<Usuario> usuarioOpt = loginService.autenticar(username, password);
        // Autenticación exitosa: crea sesión y redirige
        if (usuarioOpt.isPresent()) {
            HttpSession session = req.getSession();
            session.setAttribute("usuario", usuarioOpt.get());
            AuthUtil.generarCsrfToken(session);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            // Credenciales inválidas: devuelve error al formulario
            req.setAttribute("error", "Usuario o contraseña incorrectos");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
    }
}
