package com.budgetmap.service;

import com.budgetmap.dto.ConfigAlertaResponse;
import com.budgetmap.model.ConfigAlerta;
import com.budgetmap.model.Usuario;
import com.budgetmap.repository.ConfigAlertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfigAlertaService {

    private final ConfigAlertaRepository configAlertaRepository;

    public ConfigAlertaResponse obtenerPorUsuario(Long usuarioId) {
        ConfigAlerta config = configAlertaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));
        return convertirAResponse(config);
    }

    @Transactional
    public ConfigAlertaResponse actualizarRadio(Long usuarioId, Integer nuevoRadio) {
        ConfigAlerta config = configAlertaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada"));
        config.setRadioMetros(nuevoRadio);
        return convertirAResponse(configAlertaRepository.save(config));
    }

    @Transactional
    public void crearConfiguracionInicial(Usuario usuario) {
        if (!configAlertaRepository.existsByUsuarioId(usuario.getId())) {
            ConfigAlerta config = ConfigAlerta.builder()
                    .usuario(usuario)
                    .radioMetros(500)
                    .notificarPromociones(true)
                    .notificarEventos(true)
                    .activo(true)
                    .build();
            configAlertaRepository.save(config);
        }
    }

    private ConfigAlertaResponse convertirAResponse(ConfigAlerta config) {
        return ConfigAlertaResponse.builder()
                .id(config.getId())
                .radioMetros(config.getRadioMetros())
                .notificarPromociones(config.getNotificarPromociones())
                .notificarEventos(config.getNotificarEventos())
                .activo(config.getActivo())
                .build();
    }
}