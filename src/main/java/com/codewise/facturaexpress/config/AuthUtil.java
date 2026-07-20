package com.codewise.facturaexpress.config;

import com.codewise.facturaexpress.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

// Clase utilitaria para manejo de autenticación y seguridad CSRF
public class AuthUtil {

    // Nombre del atributo de sesión donde se almacena el token CSRF
    private static final String CSRF_TOKEN_ATTR = "_csrf_token";

    // Obtiene el usuario autenticado desde la sesión HTTP
    public static Usuario getUsuario(HttpServletRequest req) {
        jakarta.servlet.http.HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (Usuario) session.getAttribute("usuario");
    }

    // Genera un token CSRF aleatorio y lo guarda en la sesión
    public static String generarCsrfToken(HttpSession session) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(CSRF_TOKEN_ATTR, token);
        return token;
    }

    // Obtiene el token CSRF de la sesión o genera uno nuevo si no existe
    public static String getCsrfToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF_TOKEN_ATTR);
        if (token == null) {
            token = generarCsrfToken(session);
        }
        return token;
    }

    // Válida que el token CSRF enviado coincida con el almacenado en sesión
    public static boolean validarCsrfToken(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        String expected = (String) session.getAttribute(CSRF_TOKEN_ATTR);
        if (expected == null) return false;
        String actual = req.getParameter(CSRF_TOKEN_ATTR);
        return expected.equals(actual);
    }
}
