package com.budgetmap.service;

import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Usuario;
import com.budgetmap.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PuntosService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- LÓGICA PARA GANAR PUNTOS ---
    @Transactional
    public void sumarPuntos(Long usuarioId, int cantidad) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
                
        usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() + cantidad);
        usuarioRepository.save(usuario);
    }

    // --- LÓGICA PARA GASTAR PUNTOS ---
    @Transactional
    public void restarPuntos(Long usuarioId, int puntosRequeridos) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Regla de negocio vital: Evitar saldos negativos
        if (usuario.getPuntosAcumulados() >= puntosRequeridos) {
            usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() - puntosRequeridos);
            usuarioRepository.save(usuario);
        } else {
            // Detenemos la operación y lanzamos un error que el controlador atrapará
            throw new RuntimeException("Saldo insuficiente. Tienes " + 
                                       usuario.getPuntosAcumulados() + 
                                       " puntos y necesitas " + puntosRequeridos);
        }
    }
}