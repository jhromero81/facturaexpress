package com.codewise.facturaexpress.dao;

import com.codewise.facturaexpress.model.Usuario;
import java.util.Optional;

public interface UsuarioDAO {
    /** Busca un usuario por su nombre de usuario. Retorna Optional vacío si no existe. */
    Optional<Usuario> buscarPorUsername(String username);
    /** Guarda un nuevo usuario en la base de datos y asigna el ID generado. */
    Usuario guardar(Usuario usuario);
}
