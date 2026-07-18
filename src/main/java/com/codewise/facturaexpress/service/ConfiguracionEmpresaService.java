package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.ConfiguracionEmpresa;
import com.codewise.facturaexpress.repository.ConfiguracionEmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// Servicio que gestiona la configuración de la empresa
@Service
public class ConfiguracionEmpresaService {

    private final ConfiguracionEmpresaRepository configRepository;

    public ConfiguracionEmpresaService(ConfiguracionEmpresaRepository configRepository) {
        this.configRepository = configRepository;
    }

    // Obtiene la configuración actual de la empresa
    public ConfiguracionEmpresa obtenerConfiguracion() {
        Optional<ConfiguracionEmpresa> opt = configRepository.findFirstByOrderByIdAsc();
        return opt.orElse(null);
    }

    // Guarda o actualiza la configuración de la empresa (solo un registro)
    @Transactional
    public ConfiguracionEmpresa guardarConfiguracion(ConfiguracionEmpresa config) {
        Optional<ConfiguracionEmpresa> existente = configRepository.findFirstByOrderByIdAsc();
        if (existente.isPresent()) {
            config.setId(existente.get().getId());
            return configRepository.save(config);
        }
        return configRepository.save(config);
    }
}
