package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.Usuario;
import com.codewise.facturaexpress.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder;

    public UsuarioAdminService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Usuario crearUsuario(Usuario usuario, String passwordPlano) {
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        if (passwordPlano == null || passwordPlano.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }
        usuario.setPasswordHash(encoder.encode(passwordPlano));
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("VENDEDOR");
        }
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizarUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            throw new IllegalArgumentException("ID requerido para actualizar");
        }
        Usuario existente = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (usuario.getUsername() != null) existente.setUsername(usuario.getUsername());
        if (usuario.getNombre() != null) existente.setNombre(usuario.getNombre());
        if (usuario.getEmail() != null) existente.setEmail(usuario.getEmail());
        if (usuario.getRol() != null) existente.setRol(usuario.getRol());
        return usuarioRepository.save(existente);
    }

    @Transactional
    public void cambiarPassword(Long id, String nuevaPassword) {
        if (nuevaPassword == null || nuevaPassword.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }
        Usuario usr = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usr.setPasswordHash(encoder.encode(nuevaPassword));
        usuarioRepository.save(usr);
    }

    @Transactional
    public void activarODesactivar(Long id, boolean activo) {
        Usuario usr = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usr.setActivo(activo);
        usuarioRepository.save(usr);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}
