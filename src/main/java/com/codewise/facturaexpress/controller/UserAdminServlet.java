package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.service.LogAuditoriaService;
import com.codewise.facturaexpress.service.UsuarioAdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class UserAdminServlet extends HttpServlet {

    private final UsuarioAdminService usuarioAdminService;
    private final LogAuditoriaService logService;

    public UserAdminServlet(UsuarioAdminService usuarioAdminService, LogAuditoriaService logService) {
        this.usuarioAdminService = usuarioAdminService;
        this.logService = logService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("activeNav", "usuarios");
        req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
        String action = req.getParameter("action");
        if ("nuevo".equals(action)) {
            req.setAttribute("pageTitle", "Nuevo Usuario");
            req.getRequestDispatcher("/WEB-INF/jsp/usuario-form.jsp").forward(req, resp);
        } else if ("editar".equals(action)) {
            req.setAttribute("pageTitle", "Editar Usuario");
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                usuarioAdminService.buscarPorId(id).ifPresent(u -> req.setAttribute("usuario", u));
            } catch (NumberFormatException ignored) {}
            req.getRequestDispatcher("/WEB-INF/jsp/usuario-form.jsp").forward(req, resp);
        } else {
            req.setAttribute("pageTitle", "Usuarios");
            try {
                List<Usuario> usuarios = usuarioAdminService.listarUsuarios();
                req.setAttribute("usuarios", usuarios);
            } catch (Exception e) {
                req.setAttribute("error", "Error al cargar usuarios: " + e.getMessage());
            }
            req.getRequestDispatcher("/WEB-INF/jsp/usuarios.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!AuthUtil.validarCsrfToken(req)) {
            req.setAttribute("error", "Token CSRF invalido");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
            return;
        }
        req.setAttribute("activeNav", "usuarios");
        String action = req.getParameter("action");
        try {
            if ("guardar".equals(action)) {
                String idStr = req.getParameter("id");
                if (idStr != null && !idStr.isBlank()) {
                    Long id = Long.parseLong(idStr);
                    Usuario u = new Usuario();
                    u.setId(id);
                    u.setUsername(req.getParameter("username"));
                    u.setNombre(req.getParameter("nombre"));
                    u.setEmail(req.getParameter("email"));
                    u.setRol(req.getParameter("rol"));
                    usuarioAdminService.actualizarUsuario(u);
                    String pass = req.getParameter("password");
                    if (pass != null && !pass.isBlank()) {
                        usuarioAdminService.cambiarPassword(id, pass);
                    }
                    logService.registrar(AuthUtil.getUsuario(req), "UPDATE usuario id=" + id, "usuarios", id, req);
                } else {
                    Usuario u = new Usuario();
                    u.setUsername(req.getParameter("username"));
                    u.setNombre(req.getParameter("nombre"));
                    u.setEmail(req.getParameter("email"));
                    u.setRol(req.getParameter("rol"));
                    usuarioAdminService.crearUsuario(u, req.getParameter("password"));
                    logService.registrar(AuthUtil.getUsuario(req), "INSERT usuario username=" + u.getUsername(), "usuarios", null, req);
                }
                resp.sendRedirect(req.getContextPath() + "/usuarios");
            } else if ("toggleActivo".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                boolean activo = "true".equals(req.getParameter("activo"));
                usuarioAdminService.activarODesactivar(id, activo);
                resp.sendRedirect(req.getContextPath() + "/usuarios");
            } else if ("eliminar".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                usuarioAdminService.eliminarUsuario(id);
                logService.registrar(AuthUtil.getUsuario(req), "DELETE usuario id=" + id, "usuarios", id, req);
                resp.sendRedirect(req.getContextPath() + "/usuarios");
            } else {
                resp.sendRedirect(req.getContextPath() + "/usuarios");
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            List<Usuario> usuarios = usuarioAdminService.listarUsuarios();
            req.setAttribute("usuarios", usuarios);
            req.getRequestDispatcher("/WEB-INF/jsp/usuarios.jsp").forward(req, resp);
        }
    }
}
