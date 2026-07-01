package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio JPA para la entidad Cliente
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
