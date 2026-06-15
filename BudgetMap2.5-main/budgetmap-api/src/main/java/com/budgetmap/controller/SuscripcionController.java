package com.budgetmap.controller;

import com.budgetmap.dto.CompraPlanRequest;
import com.budgetmap.dto.PlanSuscripcionDTO;
import com.budgetmap.dto.TransaccionResponse;
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.SuscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suscripciones")
public class SuscripcionController {

    @Autowired
    private SuscripcionService suscripcionService;

    // Ruta pública o para usuarios logueados que quieren ver los precios
    @GetMapping("/planes")
    public ResponseEntity<List<PlanSuscripcionDTO>> obtenerPlanes() {
        return ResponseEntity.ok(suscripcionService.obtenerPlanesActivos());
    }

    // Ruta protegida: El usuario logueado compra un plan
    @PostMapping("/comprar")
    public ResponseEntity<TransaccionResponse> comprarPlan(
            @RequestBody CompraPlanRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
            
        // Extraemos el ID del usuario del Token JWT directamente!
        TransaccionResponse recibo = suscripcionService.comprarPlan(userDetails.getId(), request);
        return ResponseEntity.ok(recibo);
    }
}