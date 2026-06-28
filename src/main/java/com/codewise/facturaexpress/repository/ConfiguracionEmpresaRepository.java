package com.codewise.facturaexpress.repository;

import com.codewise.facturaexpress.model.ConfiguracionEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface ConfiguracionEmpresaRepository extends JpaRepository<ConfiguracionEmpresa, Long> {

    Optional<ConfiguracionEmpresa> findFirstByOrderByIdAsc();
}
