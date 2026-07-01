package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repositorio JPA para la entidad Usuario
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca un usuario por su nombre de usuario
    Optional<Usuario> findByUsername(String username);
}
