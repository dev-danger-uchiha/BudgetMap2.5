package com.budgetmap.controller;

import com.budgetmap.dto.PQRSRequest;
import com.budgetmap.dto.PQRSResponse;
import com.budgetmap.dto.PQRSRespuestaRequest;
import com.budgetmap.model.enums.EstadoPQRS;
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.PQRSService;
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
@CrossOrigin(origins = "*")
public class PQRSController {

    @Autowired
    private PQRSService pqrsService;

    @GetMapping("/pqrs")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<List<PQRSResponse>> listarTodos() {
        return ResponseEntity.ok(pqrsService.listarTodos());
    }

    @GetMapping("/mis-pqrs")
    public ResponseEntity<List<PQRSResponse>> listarMisPQRS(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(pqrsService.listarPorUsuario(userDetails.getId()));
    }

    @GetMapping("/mis-pqrs/paginado")
    public ResponseEntity<Page<PQRSResponse>> listarMisPQRSPaginado(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(pqrsService.listarPorUsuarioPaginado(userDetails.getId(), pageable));
    }

    @GetMapping("/pqrs/pendientes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<List<PQRSResponse>> listarPendientes() {
        return ResponseEntity.ok(pqrsService.listarPendientesRespuesta());
    }

    @GetMapping("/pqrs/asignados")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<List<PQRSResponse>> listarAsignados(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(pqrsService.listarAsignadosAModerador(userDetails.getId()));
    }

    @GetMapping("/pqrs/{id}")
    public ResponseEntity<PQRSResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pqrsService.obtenerPorId(id));
    }

    @PostMapping("/pqrs")
    public ResponseEntity<PQRSResponse> crear(@Valid @RequestBody PQRSRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(pqrsService.crear(request, userDetails.getId()));
    }

    @PutMapping("/pqrs/{id}/responder")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<PQRSResponse> responder(@PathVariable Long id,
            @Valid @RequestBody PQRSRespuestaRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(pqrsService.responder(id, request.getRespuesta(), userDetails.getId()));
    }

    @PutMapping("/pqrs/{id}/asignar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Void> asignar(@PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        pqrsService.asignarAModerador(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/pqrs/{id}/cerrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Void> cerrar(@PathVariable Long id) {
        pqrsService.cerrar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/estadisticas/pqrs")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Long>> estadisticas() {
        return ResponseEntity.ok(Map.of(
                "abiertos", pqrsService.contarPorEstado(EstadoPQRS.ABIERTO),
                "enProceso", pqrsService.contarPorEstado(EstadoPQRS.EN_PROCESO),
                "respondidos", pqrsService.contarPorEstado(EstadoPQRS.RESPONDIDO),
                "cerrados", pqrsService.contarPorEstado(EstadoPQRS.CERRADO)));
    }
}
