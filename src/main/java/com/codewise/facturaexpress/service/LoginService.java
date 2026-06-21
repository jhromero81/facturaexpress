package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.dao.UsuarioDAO;
import com.codewise.facturaexpress.dao.UsuarioDAOImpl;
import com.codewise.facturaexpress.model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

public class LoginService {

    private final UsuarioDAO usuarioDAO;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginService() {
        this.usuarioDAO = new UsuarioDAOImpl();
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Optional<Usuario> autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return Optional.empty();
        }
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorUsername(username.trim());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (!usuario.isActivo()) {
                return Optional.empty();
            }
            if (passwordEncoder.matches(password, usuario.getPasswordHash())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    public static String hashPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }
}
