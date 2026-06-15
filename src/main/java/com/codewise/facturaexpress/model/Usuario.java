package com.codewise.facturaexpress.model;

import java.time.LocalDateTime;

/**
 * Entidad que mapea la tabla "usuarios" de la base de datos.
 * Almacena las credenciales y datos de los usuarios del sistema.
 */
public class Usuario {
    private Long id;
    private String username;
    private String passwordHash; // hash de la contraseña (BCrypt)
    private String nombre;
    private String email;
    private String rol;          // rol del usuario: ADMIN, USER, etc.
    private boolean activo;      // indica si la cuenta está activa o deshabilitada
    private LocalDateTime fechaCreacion;

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
