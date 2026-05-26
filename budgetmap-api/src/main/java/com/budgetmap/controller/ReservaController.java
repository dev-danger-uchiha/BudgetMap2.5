package com.budgetmap.controller;

import com.budgetmap.dto.ReservaRequest;
import com.budgetmap.dto.ReservaResponse;
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/reservas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<List<ReservaResponse>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @PutMapping("/reservas/confirmar/{codigo}")
    @PreAuthorize("hasRole('LOCAL_ALIADO')")
    public ResponseEntity<ReservaResponse> confirmarAsistencia(
            @PathVariable String codigo,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(reservaService.confirmarAsistencia(codigo, userDetails.getId()));
    }

    @GetMapping("/mis-reservas")
    @PreAuthorize("hasRole('EXPLORADOR')")
    public ResponseEntity<List<ReservaResponse>> listarMisReservas(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(reservaService.listarPorUsuario(userDetails.getId()));
    }

    @GetMapping("/mis-reservas/paginado")
    @PreAuthorize("hasRole('EXPLORADOR')")
    public ResponseEntity<Page<ReservaResponse>> listarMisReservasPaginado(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(reservaService.listarPorUsuarioPaginado(userDetails.getId(), pageable));
    }

    @GetMapping("/mis-reservas/establecimiento/{estId}")
    @PreAuthorize("hasRole('LOCAL_ALIADO')")
    public ResponseEntity<List<ReservaResponse>> listarReservasEstablecimiento(@PathVariable Long estId) {
        return ResponseEntity.ok(reservaService.listarPorEstablecimiento(estId));
    }

    @GetMapping("/reservas/{id}")
    public ResponseEntity<ReservaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @PostMapping("/reservas")
    @PreAuthorize("hasRole('EXPLORADOR')")
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(reservaService.crear(request, userDetails.getId()));
    }

    @PostMapping("/reservas/{id}/cancelar")
    @PreAuthorize("hasRole('EXPLORADOR')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody(required = false) Map<String, String> body) {
        String motivo = body != null ? body.get("motivo") : null;
        reservaService.cancelar(id, userDetails.getId(), motivo);
        return ResponseEntity.ok().build();
    }
}
