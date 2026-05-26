package com.budgetmap.service;

import com.budgetmap.dto.ConfigAlertaResponse;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.ConfigAlerta;
import com.budgetmap.model.Usuario;
import com.budgetmap.repository.ConfigAlertaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigAlertaService {

    private final ConfigAlertaRepository configAlertaRepository;

    public ConfigAlertaResponse obtenerPorUsuario(Long usuarioId) {
        log.debug("Consultando configuración de alertas para el usuario ID: {}", usuarioId);
        ConfigAlerta config = configAlertaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> {
                    log.warn("Configuración de alertas no encontrada para el usuario ID: {}", usuarioId);
                    return new ResourceNotFoundException("Configuración no encontrada");
                });
        return convertirAResponse(config);
    }

    @Transactional
    public ConfigAlertaResponse actualizarRadio(Long usuarioId, Integer nuevoRadio) {
        log.info("Actualizando radio de alertas a {} metros para el usuario ID: {}", nuevoRadio, usuarioId);
        ConfigAlerta config = configAlertaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada"));
        
        config.setRadioMetros(nuevoRadio);
        ConfigAlerta guardada = configAlertaRepository.save(config);
        log.debug("Radio de alertas actualizado correctamente para el usuario ID: {}", usuarioId);
        
        return convertirAResponse(guardada);
    }

    @Transactional
    public void crearConfiguracionInicial(Usuario usuario) {
        if (!configAlertaRepository.existsByUsuarioId(usuario.getId())) {
            log.info("Creando configuración de alertas por defecto para el nuevo usuario ID: {}", usuario.getId());
            ConfigAlerta config = ConfigAlerta.builder()
                    .usuario(usuario)
                    .radioMetros(500)
                    .notificarPromociones(true)
                    .notificarEventos(true)
                    .activo(true)
                    .build();
            configAlertaRepository.save(config);
        } else {
            log.debug("El usuario ID: {} ya tiene una configuración de alertas, se omite la creación inicial", usuario.getId());
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