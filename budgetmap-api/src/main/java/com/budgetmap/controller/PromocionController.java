package com.budgetmap.controller;

import com.budgetmap.dto.PromocionRequest;
import com.budgetmap.dto.PromocionResponse;
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.PromocionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @GetMapping("/promociones")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<List<PromocionResponse>> listarTodas() {
        return ResponseEntity.ok(promocionService.listarTodas());
    }

    @GetMapping("/promociones/activas")
    public ResponseEntity<Page<PromocionResponse>> listarActivas(Pageable pageable) {
        return ResponseEntity.ok(promocionService.listarActivas(pageable));
    }

    @GetMapping("/promociones/mis-promociones")
    @PreAuthorize("hasAnyRole('LOCAL_ALIADO', 'ANFITRION')")
    public ResponseEntity<List<PromocionResponse>> listarMisPromociones(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(promocionService.listarMisPromociones(userDetails.getId()));
    }

    @GetMapping("/promociones/{id}")
    public ResponseEntity<PromocionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(promocionService.obtenerPorId(id));
    }

    @GetMapping("/promociones/establecimiento/{estId}")
    public ResponseEntity<List<PromocionResponse>> listarPorEstablecimiento(@PathVariable Long estId) {
        return ResponseEntity.ok(promocionService.listarPorEstablecimiento(estId));
    }

    @GetMapping("/promociones/establecimiento/{estId}/activas")
    public ResponseEntity<List<PromocionResponse>> listarActivasPorEstablecimiento(@PathVariable Long estId) {
        return ResponseEntity.ok(promocionService.listarActivasPorEstablecimiento(estId));
    }

    @PostMapping("/promociones")
    @PreAuthorize("hasAnyRole('LOCAL_ALIADO', 'ANFITRION')")
    public ResponseEntity<PromocionResponse> crear(@Valid @RequestBody PromocionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(promocionService.crear(request, userDetails.getId()));
    }

    @PutMapping("/promociones/{id}")
    @PreAuthorize("hasAnyRole('LOCAL_ALIADO', 'ANFITRION')")
    public ResponseEntity<PromocionResponse> actualizar(@PathVariable Long id,
            @Valid @RequestBody PromocionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(promocionService.actualizar(id, request, userDetails.getId()));
    }

    @DeleteMapping("/promociones/{id}")
    @PreAuthorize("hasAnyRole('LOCAL_ALIADO', 'ANFITRION')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        promocionService.desactivar(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/promociones/{id}/usar")
    @PreAuthorize("hasRole('EXPLORADOR')")
    public ResponseEntity<Void> registrarUso(@PathVariable Long id) {
        promocionService.registrarUso(id);
        return ResponseEntity.ok().build();
    }
}