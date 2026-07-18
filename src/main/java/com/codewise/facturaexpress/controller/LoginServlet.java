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

// Servlet para la autenticación de usuarios (inicio de sesión)
public class LoginServlet extends HttpServlet {

    private final LoginService loginService;

    public LoginServlet(LoginService loginService) {
        this.loginService = loginService;
    }

    // Muestra el formulario de login o redirige al dashboard si ya hay sesión
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    // Procesa el formulario de login: autentifica y crea la sesión
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Optional<Usuario> usuarioOpt = loginService.autenticar(username, password);
        if (usuarioOpt.isPresent()) {
            HttpSession session = req.getSession();
            session.setAttribute("usuario", usuarioOpt.get());
            AuthUtil.generarCsrfToken(session);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("error", "Usuario o contraseña incorrectos");
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        }
    }
}
