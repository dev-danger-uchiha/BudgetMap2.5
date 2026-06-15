package com.budgetmap.service;

import com.budgetmap.dto.ConfigAlertaResponse;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.ConfigAlerta;
import com.budgetmap.model.Usuario;
import com.budgetmap.repository.ConfigAlertaRepository;
import com.budgetmap.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigAlertaService {

    private final ConfigAlertaRepository configAlertaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ConfigAlertaResponse obtenerPorUsuario(Long usuarioId) {
        log.debug("Consultando configuración de alertas para el usuario ID: {}", usuarioId);
        ConfigAlerta config = configAlertaRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearConfiguracionPorDefecto(usuarioId));
        return convertirAResponse(config);
    }

    @Transactional
    public ConfigAlertaResponse actualizarRadio(Long usuarioId, Integer nuevoRadio) {
        log.info("Actualizando radio de alertas a {} metros para el usuario ID: {}", nuevoRadio, usuarioId);
        ConfigAlerta config = configAlertaRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearConfiguracionPorDefecto(usuarioId));
        
        config.setRadioMetros(nuevoRadio);
        ConfigAlerta guardada = configAlertaRepository.save(config);
        log.debug("Radio de alertas actualizado correctamente para el usuario ID: {}", usuarioId);
        
        return convertirAResponse(guardada);
    }

    @Transactional
    public ConfigAlertaResponse actualizarConfiguracionCompleta(Long usuarioId, Integer radioMetros, Boolean notificarPromociones, Boolean notificarEventos) {
        log.info("Actualizando configuración completa para el usuario ID: {}", usuarioId);
        ConfigAlerta config = configAlertaRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearConfiguracionPorDefecto(usuarioId));
        
        if (radioMetros != null) config.setRadioMetros(radioMetros);
        if (notificarPromociones != null) config.setNotificarPromociones(notificarPromociones);
        if (notificarEventos != null) config.setNotificarEventos(notificarEventos);

        ConfigAlerta guardada = configAlertaRepository.save(config);
        log.debug("Configuración actualizada correctamente para el usuario ID: {}", usuarioId);
        
        return convertirAResponse(guardada);
    }

    @Transactional
    public void crearConfiguracionInicial(Usuario usuario) {
        if (!configAlertaRepository.existsByUsuarioId(usuario.getId())) {
            crearConfiguracionPorDefecto(usuario.getId());
        } else {
            log.debug("El usuario ID: {} ya tiene una configuración de alertas, se omite la creación inicial", usuario.getId());
        }
    }

    private ConfigAlerta crearConfiguracionPorDefecto(Long usuarioId) {
        log.info("Creando configuración de alertas por defecto para el usuario ID: {}", usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado para crear alertas"));
        ConfigAlerta config = ConfigAlerta.builder()
                .usuario(usuario)
                .radioMetros(2000)
                .notificarPromociones(true)
                .notificarEventos(true)
                .activo(true)
                .build();
        return configAlertaRepository.save(config);
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