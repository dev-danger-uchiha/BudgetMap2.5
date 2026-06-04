package com.budgetmap.service;

import lombok.RequiredArgsConstructor;

import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Usuario;
import com.budgetmap.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasarelaService {

    private final UsuarioRepository usuarioRepository;
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void fixRolColumnType() {
        try {
            // Corrige el tipo de columna en MySQL para que acepte cualquier longitud de String de la Enum
            jdbcTemplate.execute("ALTER TABLE usuarios MODIFY COLUMN rol VARCHAR(50) NOT NULL");
            log.info("Columna 'rol' alterada correctamente a VARCHAR(50)");
        } catch (Exception e) {
            log.warn("No se pudo alterar la columna rol (puede que ya esté correcta o haya otro problema): {}", e.getMessage());
        }
    }

    /**
     * Procesa un pago simulado y asciende al usuario a PRO inmediatamente.
     */
    @Transactional
    public void procesarPagoSimulado(Usuario usuario, String nombrePlan) {
        log.info("Iniciando simulación de pago para plan: {} por el usuario ID {}", nombrePlan, usuario.getId());
        
        // Simular validación
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no válido para simulación.");
        }

        // Buscar el usuario en la base de datos para actualizarlo
        Usuario usuarioEntity = usuarioRepository.findById(usuario.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuario.getId()));

        // Actualizar el rol a EXPLORADOR_PRO
        usuarioEntity.setRol(com.budgetmap.model.enums.RolUsuario.EXPLORADOR_PRO);
        usuarioRepository.save(usuarioEntity);
        
        log.info("Simulación exitosa: El usuario ID {} ahora es PRO.", usuarioEntity.getId());
    }

    @Transactional
    public void procesarWebhook(String tipo, String dataId) {
        // Obsoleto en la versión simulada
        log.info("Webhook recibido en simulador tipo: {}, data: {}", tipo, dataId);
    }
}