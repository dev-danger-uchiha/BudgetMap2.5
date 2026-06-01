package com.budgetmap.controller;

import com.budgetmap.dto.AprobacionRequest;
import com.budgetmap.dto.EstablecimientoRequest;
import com.budgetmap.dto.EstablecimientoResponse;
import com.budgetmap.dto.EstablecimientoUpdateRequest; // El nuevo DTO
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.EstablecimientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EstablecimientoController {

    @Autowired
    private EstablecimientoService establecimientoService;

    // --- ENDPOINTS PARA EL PERFIL DEL ALIADO ---

    @GetMapping("/establecimientos/mi-establecimiento")
    @PreAuthorize("hasRole('LOCAL_ALIADO')")
    public ResponseEntity<EstablecimientoResponse> obtenerMiEstablecimiento(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(establecimientoService.obtenerPorUsuarioId(userDetails.getId()));
    }

    @PutMapping("/establecimientos/mi-establecimiento/update")
    @PreAuthorize("hasRole('LOCAL_ALIADO')")
    public ResponseEntity<EstablecimientoResponse> actualizarDesdePerfil(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody EstablecimientoUpdateRequest request) {

        // Llamamos al nuevo método del Service que creamos antes
        return ResponseEntity.ok(establecimientoService.actualizarDesdePerfil(userDetails.getId(), request));
    }

    // --- ENDPOINTS DE CONSULTA PÚBLICA ---

    @GetMapping("/establecimientos/aprobados")
    public ResponseEntity<List<EstablecimientoResponse>> listarAprobados() {
        return ResponseEntity.ok(establecimientoService.listarAprobados());
    }

    @GetMapping("/establecimientos/{id}")
    public ResponseEntity<EstablecimientoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(establecimientoService.obtenerPorId(id));
    }

    // --- ENDPOINTS DE ADMINISTRACIÓN (ADMIN/MODERADOR) ---

    @GetMapping("/establecimientos/pendientes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<List<EstablecimientoResponse>> listarPendientes() {
        return ResponseEntity.ok(establecimientoService.listarPendientesAprobacion());
    }

    @GetMapping("/establecimientos/admin/paginado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<org.springframework.data.domain.Page<EstablecimientoResponse>> listarFiltradoAdmin(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) com.budgetmap.model.enums.EstadoAprobacion estado,
            @RequestParam(required = false) String nit,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(establecimientoService.listarFiltradoAdmin(texto, estado, nit, pageable));
    }

    @PostMapping("/establecimientos/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Void> aprobar(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        establecimientoService.aprobar(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/establecimientos/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Void> rechazar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody(required = false) AprobacionRequest request) {

        String motivo = (request != null && request.getMotivoRechazo() != null)
                ? request.getMotivoRechazo()
                : "No cumple con las políticas de la plataforma";

        establecimientoService.rechazar(id, userDetails.getId(), motivo);
        return ResponseEntity.ok().build();
    }

    // --- ENDPOINTS DE CREACIÓN/EDICIÓN COMPLETA (ALIADO) ---

    @PostMapping("/establecimientos")
    @PreAuthorize("hasRole('LOCAL_ALIADO')")
    public ResponseEntity<EstablecimientoResponse> crear(
            @Valid @RequestBody EstablecimientoRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(establecimientoService.crear(request, userDetails.getId()));
    }

    @PutMapping("/establecimientos/{id}")
    @PreAuthorize("hasRole('LOCAL_ALIADO')")
    public ResponseEntity<EstablecimientoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstablecimientoRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(establecimientoService.actualizar(id, request, userDetails.getId()));
    }
}