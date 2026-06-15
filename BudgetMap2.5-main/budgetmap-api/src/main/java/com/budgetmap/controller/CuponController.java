package com.budgetmap.controller;

import com.budgetmap.dto.CanjearCuponRequest;
import com.budgetmap.dto.CuponRedimidoDTO;
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.CuponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cupones")
public class CuponController {

    @Autowired
    private CuponService cuponService;

    // El explorador gasta sus puntos y obtiene un código
    @PostMapping("/canjear")
    public ResponseEntity<CuponRedimidoDTO> canjearPuntosPorCupon(
            @RequestBody CanjearCuponRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
            
        CuponRedimidoDTO cupon = cuponService.canjearCupon(userDetails.getId(), request);
        return ResponseEntity.ok(cupon);
    }

    // El explorador revisa su "Billetera de Cupones"
    @GetMapping("/mis-cupones")
    public ResponseEntity<List<CuponRedimidoDTO>> verMisCupones(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
            
        List<CuponRedimidoDTO> misCupones = cuponService.obtenerMisCupones(userDetails.getId());
        return ResponseEntity.ok(misCupones);
    }

    // El Aliado (Restaurante) ingresa el código en su panel para validarlo
    @PutMapping("/validar/{codigo}")
    public ResponseEntity<CuponRedimidoDTO> validarCuponEnCaja(
            @PathVariable String codigo,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
            
        CuponRedimidoDTO cuponValidado = cuponService.validarYQuemarCupon(codigo, userDetails.getId());
        return ResponseEntity.ok(cuponValidado);
    }
}