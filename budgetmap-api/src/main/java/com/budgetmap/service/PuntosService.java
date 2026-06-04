package com.budgetmap.service;

import lombok.RequiredArgsConstructor;

import com.budgetmap.exception.PuntosException;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Usuario;
import com.budgetmap.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;

@Slf4j
@Service
@RequiredArgsConstructor
public class PuntosService {

    private final UsuarioRepository usuarioRepository;

    // --- LÓGICA PARA GANAR PUNTOS ---
    @Transactional
    @CacheEvict(value = "leaderboard", allEntries = true)
    public void sumarPuntos(Long usuarioId, int cantidad) {
        log.debug("Iniciando suma de {} puntos al usuario ID: {}", cantidad, usuarioId);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    log.error("Fallo al sumar puntos: Usuario ID {} no encontrado", usuarioId);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });
                
        usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() + cantidad);
        usuarioRepository.save(usuario);
        
        log.info("Puntos sumados correctamente. Nuevo saldo del usuario ID {}: {}", usuarioId, usuario.getPuntosAcumulados());
    }

    // --- LÓGICA PARA GASTAR PUNTOS ---
    @Transactional
    @CacheEvict(value = "leaderboard", allEntries = true)
    public void restarPuntos(Long usuarioId, int puntosRequeridos) {
        log.debug("Procesando descuento de {} puntos para el usuario ID: {}", puntosRequeridos, usuarioId);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    log.error("Fallo al restar puntos: Usuario ID {} no encontrado", usuarioId);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });

        // Regla de negocio vital: Evitar saldos negativos
        if (usuario.getPuntosAcumulados() >= puntosRequeridos) {
            usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() - puntosRequeridos);
            usuarioRepository.save(usuario);
            log.info("Puntos descontados exitosamente. Nuevo saldo del usuario ID {}: {}", usuarioId, usuario.getPuntosAcumulados());
        } else {
            log.warn("Intento de fraude o saldo insuficiente. Usuario ID: {} tiene {} puntos, intentó gastar {}", 
                     usuarioId, usuario.getPuntosAcumulados(), puntosRequeridos);
            // Detenemos la operación y lanzamos un error de estado ilegal
            throw new PuntosException("Saldo insuficiente. Tienes " + 
                                       usuario.getPuntosAcumulados() + 
                                       " puntos y necesitas " + puntosRequeridos);
        }
    }
}