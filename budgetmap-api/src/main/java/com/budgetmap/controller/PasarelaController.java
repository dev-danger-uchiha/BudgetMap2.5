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
            usuarioReal.setEmail(userDetails.getUsername());
            usuarioReal.setNombre(userDetails.getNombre());

            String linkDePago = pasarelaService.crearPreferenciaPago(
                    usuarioReal,
                    "Suscripción Plan PRO - BudgetMap", 
                    new BigDecimal("29900")
            );

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("urlPago", linkDePago);
            
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            // Si Mercado Pago falla, devolvemos el error al frontend en formato JSON
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
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