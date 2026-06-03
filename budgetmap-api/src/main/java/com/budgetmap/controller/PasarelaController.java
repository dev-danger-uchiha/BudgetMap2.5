package com.budgetmap.controller;

import com.budgetmap.model.Usuario;
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.PasarelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pasarela")
public class PasarelaController {

    @Autowired
    private PasarelaService pasarelaService;

    // Endpoint 1: Generar Link de Prueba
    @PostMapping("/test-link")
    public ResponseEntity<?> probarGeneracionDeLink(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            // Usamos el ID del usuario real que inició sesión (desde el JWT)
            Usuario usuarioReal = new Usuario();
            usuarioReal.setId(userDetails.getId());
            // Llamamos al servicio para que ascienda al usuario a PRO
            pasarelaService.procesarPagoSimulado(usuarioReal, "Suscripción Plan PRO - BudgetMap");

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Pago simulado procesado exitosamente"
            ));
        } catch (Exception e) {
            // Si Mercado Pago falla, devolvemos un 400 para evitar que la consola marque "Internal Server Error"
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint 2: Escuchar a Mercado Pago (El Webhook)
    @PostMapping("/webhook")
    public ResponseEntity<String> recibirNotificacionPago(
            @RequestParam(name = "data.id", required = false) Long paymentId,
            @RequestParam(name = "type", required = false) String type) {

        if ("payment".equals(type) && paymentId != null) {
            pasarelaService.procesarWebhook(type, String.valueOf(paymentId));
        }

        return ResponseEntity.ok("Notificación recibida");
    }
}