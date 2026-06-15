package com.budgetmap.controller;

import com.budgetmap.dto.AprobacionRequest;
import com.budgetmap.dto.EventoRequest;
import com.budgetmap.dto.EventoResponse;
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping("/eventos")
    public ResponseEntity<Page<EventoResponse>> listarTodos(@org.springframework.data.web.PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(eventoService.listarTodos(pageable));
    }

    @GetMapping("/eventos/pendientes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Page<EventoResponse>> listarPendientes(@org.springframework.data.web.PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(eventoService.listarPendientesAprobacion(pageable));
    }

    @GetMapping("/eventos/admin/paginado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Page<EventoResponse>> listarFiltradoAdmin(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) com.budgetmap.model.enums.EstadoAprobacion estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Pageable pageable) {
        return ResponseEntity.ok(eventoService.listarFiltradoAdmin(texto, estado, fechaInicio, fechaFin, pageable));
    }

    @PostMapping("/eventos/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Void> aprobar(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        eventoService.aprobar(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/eventos/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
    public ResponseEntity<Void> rechazar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody(required = false) AprobacionRequest request) {

        String motivo = (request != null && request.getMotivoRechazo() != null)
                ? request.getMotivoRechazo()
                : "El evento no cumple con los criterios para ser público.";

        eventoService.rechazar(id, userDetails.getId(), motivo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/eventos/activos")
    public ResponseEntity<Page<EventoResponse>> listarActivos(@org.springframework.data.web.PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(eventoService.listarActivos(pageable));
    }

    @GetMapping("/eventos/destacados")
    public ResponseEntity<List<EventoResponse>> listarDestacados() {
        return ResponseEntity.ok(eventoService.listarDestacados());
    }

    @GetMapping("/eventos/{id}")
    public ResponseEntity<EventoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.obtenerPorId(id));
    }

    @GetMapping("/eventos/lugar/{lugarId}")
    public ResponseEntity<List<EventoResponse>> listarPorLugar(@PathVariable Long lugarId) {
        return ResponseEntity.ok(eventoService.listarPorLugar(lugarId));
    }

    @GetMapping("/eventos/mis-eventos")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<Page<EventoResponse>> listarMisEventos(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(eventoService.listarMisEventosPaginado(userDetails.getId(), pageable, tipo, nombre, estado));
    }

    @GetMapping("/eventos/mis-estadisticas")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<java.util.Map<String, Object>> misEstadisticas(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(eventoService.obtenerEstadisticasAnfitrion(userDetails.getId()));
    }

    @PostMapping("/eventos")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<EventoResponse> crear(@Valid @RequestBody EventoRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(eventoService.crear(request, userDetails.getId()));
    }

    @PutMapping("/eventos/{id}")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<EventoResponse> actualizar(@PathVariable Long id,
            @Valid @RequestBody EventoRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(eventoService.actualizar(id, request, userDetails.getId()));
    }

    @PutMapping("/eventos/{id}/destacar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ANFITRION')")
    public ResponseEntity<Void> destacar(@PathVariable Long id, @RequestParam Boolean destacado) {
        eventoService.destacar(id, destacado);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/eventos/{id}")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        eventoService.eliminar(id, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/eventos/rango-fechas")
    public ResponseEntity<List<EventoResponse>> buscarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(eventoService.buscarPorRangoFechas(inicio, fin));
    }
}