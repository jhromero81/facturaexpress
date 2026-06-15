package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.dao.UsuarioDAO;
import com.codewise.facturaexpress.dao.UsuarioDAOImpl;
import com.codewise.facturaexpress.model.Usuario;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * Servicio de autenticación de usuarios. Compara la contraseña ingresada
 * contra el hash SHA-256 almacenado (sin salt ni iteraciones).
 */
public class LoginService {

    private final UsuarioDAO usuarioDAO;

    public LoginService() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    /**
     * Autentica un usuario verificando username y contraseña.
     * Retorna Optional vacío si las credenciales son inválidas o no coinciden.
     */
    public Optional<Usuario> autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return Optional.empty();
        }
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorUsername(username.trim());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Compara el hash SHA-256 de la contraseña ingresada contra el almacenado
            if (usuario.getPasswordHash().equals(hashPassword(password))) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    /**
     * Genera el hash SHA-256 de la contraseña en formato hexadecimal.
     * ADVERTENCIA: no se aplica salt, lo que hace el hash vulnerable
     * a ataques de tabla arcoíris. No apto para producción real.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    /**
     * Método de prueba para generar hashes durante desarrollo.
     */
    public static void main(String[] args) {
        System.out.println(hashPassword("admin123"));
    }
}
