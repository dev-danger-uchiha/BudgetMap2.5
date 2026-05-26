package com.budgetmap.controller;

import com.budgetmap.dto.NotificacionResponse;
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping("/notificaciones")
    public ResponseEntity<List<NotificacionResponse>> listarMisNotificaciones(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(notificacionService.listarPorUsuario(userDetails.getId()));
    }

    @GetMapping("/notificaciones/paginado")
    public ResponseEntity<Page<NotificacionResponse>> listarMisNotificacionesPaginado(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(notificacionService.listarPorUsuarioPaginado(userDetails.getId(), pageable));
    }

    @GetMapping("/notificaciones/no-leidas")
    public ResponseEntity<List<NotificacionResponse>> listarNoLeidas(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(notificacionService.listarNoLeidas(userDetails.getId()));
    }

    @GetMapping("/notificaciones/contador")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(Map.of(
                "noLeidas", notificacionService.contarNoLeidas(userDetails.getId())));
    }

    @PutMapping("/notificaciones/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificacionService.marcarComoLeida(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/notificaciones/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificacionService.marcarTodasComoLeidas(userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/notificaciones/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}
