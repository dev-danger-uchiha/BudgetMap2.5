package com.budgetmap.controller;

import com.budgetmap.dto.AprobacionRequest;
import com.budgetmap.dto.LugarRequest;
import com.budgetmap.model.Lugar;
import com.budgetmap.model.enums.EstadoAprobacion;
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.LugarService;
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
public class LugarController {

    @Autowired
    private LugarService lugarService;

    @GetMapping("/lugares")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<List<Lugar>> listarTodos() {
        return ResponseEntity.ok(lugarService.listarTodos());
    }

    @GetMapping("/lugares/aprobados")
    public ResponseEntity<List<Lugar>> listarAprobados() {
        return ResponseEntity.ok(lugarService.listarAprobados());
    }

    @GetMapping("/lugares/aprobados/paginado")
    public ResponseEntity<Page<Lugar>> listarAprobadosPaginado(Pageable pageable) {
        return ResponseEntity.ok(lugarService.listarAprobadosPaginado(pageable));
    }

    @GetMapping("/lugares/pendientes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<List<Lugar>> listarPendientes() {
        return ResponseEntity.ok(lugarService.listarPendientesAprobacion());
    }

    @PostMapping("/lugares/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Void> aprobar(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        lugarService.aprobar(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lugares/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Void> rechazar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody(required = false) AprobacionRequest request) { // <-- Se hizo opcional

        String motivo = (request != null && request.getMotivoRechazo() != null)
                ? request.getMotivoRechazo()
                : "El lugar no cumple con los criterios para ser público.";

        lugarService.rechazar(id, userDetails.getId(), motivo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/lugares/cercanos")
    public ResponseEntity<?> buscarCercanos(
            @RequestParam Double latitud,
            @RequestParam Double longitud,
            @RequestParam(defaultValue = "5.0") Double radioKm) {

        if (radioKm > 50.0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El radio de búsqueda no puede exceder los 50km por seguridad de red"));
        }
        return ResponseEntity.ok(lugarService.buscarCercanos(latitud, longitud, radioKm));
    }

    @GetMapping("/admin/estadisticas/lugares")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Long>> estadisticas() {
        return ResponseEntity.ok(Map.of(
                "aprobados", lugarService.contarPorEstado(EstadoAprobacion.APROBADO),
                "pendientes", lugarService.contarPorEstado(EstadoAprobacion.PENDIENTE),
                "rechazados", lugarService.contarPorEstado(EstadoAprobacion.RECHAZADO)));
    }

    @GetMapping("/lugares/{id}")
    public ResponseEntity<Lugar> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(lugarService.obtenerPorId(id));
    }

    @PostMapping("/lugares")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR', 'ANFITRION')")
    public ResponseEntity<Lugar> crear(@Valid @RequestBody LugarRequest request) {
        return ResponseEntity.ok(lugarService.crear(request));
    }

    @PutMapping("/lugares/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Lugar> actualizar(@PathVariable Long id, @Valid @RequestBody LugarRequest request) {
        return ResponseEntity.ok(lugarService.actualizar(id, request));
    }

    @DeleteMapping("/lugares/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        lugarService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}