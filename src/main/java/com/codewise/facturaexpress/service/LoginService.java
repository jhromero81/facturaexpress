package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Servicio para la autenticación de usuarios mediante username y password
@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Autentica un usuario verificando credenciales y estado activo
    public Optional<Usuario> autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return Optional.empty();
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username.trim());
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

    // Genera el hash BCrypt de una contraseña en texto plano
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
