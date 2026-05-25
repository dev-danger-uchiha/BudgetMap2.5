package com.budgetmap.controller;

import com.budgetmap.service.PasarelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, String>> probarGeneracionDeLink() {
        
        String linkDePago = pasarelaService.crearPreferenciaDePago(
                "Suscripción Plan PRO - BudgetMap", 
                new BigDecimal("29900"), 
                "PREF-PRUEBA-001"
        );

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("urlPago", linkDePago);
        
        return ResponseEntity.ok(respuesta);
    }

    // Endpoint 2: Escuchar a Mercado Pago (El Webhook)
    @PostMapping("/webhook")
    public ResponseEntity<String> recibirNotificacionPago(
            @RequestParam(name = "data.id", required = false) Long paymentId,
            @RequestParam(name = "type", required = false) String type) {

        if ("payment".equals(type) && paymentId != null) {
            pasarelaService.procesarNotificacionDePago(paymentId);
        }

        return ResponseEntity.ok("Notificación recibida");
    }
}