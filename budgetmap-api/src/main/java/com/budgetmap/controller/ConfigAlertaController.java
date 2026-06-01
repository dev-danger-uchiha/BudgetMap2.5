package com.budgetmap.controller;

import com.budgetmap.dto.ConfigAlertaResponse;
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.ConfigAlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/config-alertas")
public class ConfigAlertaController {

    @Autowired
    private ConfigAlertaService configAlertaService;

    @GetMapping("/mi-radar")
    public ResponseEntity<ConfigAlertaResponse> obtenerMiRadar(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(configAlertaService.obtenerPorUsuario(userDetails.getId()));
    }

    @PutMapping("/radio")
    public ResponseEntity<ConfigAlertaResponse> actualizarRadio(
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(configAlertaService.actualizarRadio(userDetails.getId(), body.get("radio")));
    }

    @PutMapping("/update")
    public ResponseEntity<ConfigAlertaResponse> actualizarConfiguracionCompleta(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Integer radioMetros = body.containsKey("radioMetros") ? (Integer) body.get("radioMetros") : null;
        Boolean notificarPromociones = body.containsKey("notificarPromociones") ? (Boolean) body.get("notificarPromociones") : null;
        Boolean notificarEventos = body.containsKey("notificarEventos") ? (Boolean) body.get("notificarEventos") : null;
        
        return ResponseEntity.ok(configAlertaService.actualizarConfiguracionCompleta(
                userDetails.getId(), radioMetros, notificarPromociones, notificarEventos));
    }
}