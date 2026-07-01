package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.ConfiguracionEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repositorio JPA para la entidad ConfiguracionEmpresa
public interface ConfiguracionEmpresaRepository extends JpaRepository<ConfiguracionEmpresa, Long> {

    // Obtiene el primer registro de configuracion (solo debe existir uno)
    Optional<ConfiguracionEmpresa> findFirstByOrderByIdAsc();
}
