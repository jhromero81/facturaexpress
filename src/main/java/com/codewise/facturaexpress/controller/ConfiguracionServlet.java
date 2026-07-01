package com.codewise.facturaexpress.controller;

import com.codewise.facturaexpress.config.AuthUtil;
import com.codewise.facturaexpress.model.ConfiguracionEmpresa;
import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.service.ConfiguracionEmpresaService;
import com.codewise.facturaexpress.service.LogAuditoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

// Servlet para gestionar la configuracion de la empresa
public class ConfiguracionServlet extends HttpServlet {

    private final ConfiguracionEmpresaService configService;
    private final LogAuditoriaService logService;

    public ConfiguracionServlet(ConfiguracionEmpresaService configService, LogAuditoriaService logService) {
        this.configService = configService;
        this.logService = logService;
    }

    // Muestra el formulario de configuracion con los datos actuales
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthUtil.getUsuario(req) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            ConfiguracionEmpresa config = configService.obtenerConfiguracion();
            if (config != null) {
                req.setAttribute("config", config);
            }
            req.setAttribute("activeNav", "configuracion");
            req.setAttribute("pageTitle", "Configuración");
            req.setAttribute("csrfToken", AuthUtil.getCsrfToken(req.getSession()));
            req.getRequestDispatcher("/WEB-INF/jsp/configuracion.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Error al cargar configuración: " + e.getMessage());
            req.setAttribute("activeNav", "configuracion");
            req.setAttribute("pageTitle", "Configuración");
            req.getRequestDispatcher("/WEB-INF/jsp/configuracion.jsp").forward(req, resp);
        }
    }

    // Guarda o actualiza la configuracion de la empresa
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
        try {
            ConfiguracionEmpresa config = new ConfiguracionEmpresa();
            config.setNit(req.getParameter("nit"));
            config.setRazonSocial(req.getParameter("razonSocial"));
            config.setEmailFacturacion(req.getParameter("emailFacturacion"));
            config.setTelefono(req.getParameter("telefono"));
            config.setDireccion(req.getParameter("direccion"));
            config.setResolucionDian(req.getParameter("resolucionDian"));
            String certVence = req.getParameter("certificadoVence");
            if (certVence != null && !certVence.isEmpty()) {
                config.setCertificadoVence(LocalDate.parse(certVence));
            }
            config.setNotifEmail("true".equals(req.getParameter("notifEmail")));
            config.setNotifPush("true".equals(req.getParameter("notifPush")));
            config.setAlertasDian("true".equals(req.getParameter("alertasDian")));
            config.setRecordatorios("true".equals(req.getParameter("recordatorios")));

            configService.guardarConfiguracion(config);

            Usuario usuario = AuthUtil.getUsuario(req);
            logService.registrar(usuario, "ACTUALIZAR configuracion_empresa", "configuracion_empresa", null, req);

            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\": true, \"message\": \"Configuracion guardada exitosamente\"}");
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}
