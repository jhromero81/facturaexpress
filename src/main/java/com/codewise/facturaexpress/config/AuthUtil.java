package com.codewise.facturaexpress.config;

import com.codewise.facturaexpress.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

public class AuthUtil {

    private static final String CSRF_TOKEN_ATTR = "_csrf_token";

    public static Usuario getUsuario(HttpServletRequest req) {
        return (Usuario) req.getSession().getAttribute("usuario");
    }

    public static boolean tieneRol(HttpServletRequest req, String... roles) {
        Usuario usuario = getUsuario(req);
        if (usuario == null) return false;
        for (String rol : roles) {
            if (usuario.getRol().equalsIgnoreCase(rol)) return true;
        }
        return false;
    }

    public static String generarCsrfToken(HttpSession session) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(CSRF_TOKEN_ATTR, token);
        return token;
    }

    public static String getCsrfToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF_TOKEN_ATTR);
        if (token == null) {
            token = generarCsrfToken(session);
        }
        return token;
    }

    public static boolean validarCsrfToken(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        String expected = (String) session.getAttribute(CSRF_TOKEN_ATTR);
        if (expected == null) return false;
        String actual = req.getParameter(CSRF_TOKEN_ATTR);
        return expected.equals(actual);
    }
}
