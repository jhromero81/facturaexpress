package com.codewise.facturaexpress.service;

import com.codewise.facturaexpress.model.ConfiguracionEmpresa;
import com.codewise.facturaexpress.repository.ConfiguracionEmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfiguracionEmpresaService {

    private final ConfiguracionEmpresaRepository configRepository;

    public ConfiguracionEmpresaService(ConfiguracionEmpresaRepository configRepository) {
        this.configRepository = configRepository;
    }

    public ConfiguracionEmpresa obtenerConfiguracion() {
        Optional<ConfiguracionEmpresa> opt = configRepository.findFirstByOrderByIdAsc();
        return opt.orElse(null);
    }

    public ConfiguracionEmpresa guardarConfiguracion(ConfiguracionEmpresa config) {
        Optional<ConfiguracionEmpresa> existente = configRepository.findFirstByOrderByIdAsc();
        if (existente.isPresent()) {
            config.setId(existente.get().getId());
            return configRepository.save(config);
        }
        return configRepository.save(config);
    }
}
