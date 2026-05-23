package com.budgetmap.controller;

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
@CrossOrigin(origins = "*")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping("/eventos")
    public ResponseEntity<List<EventoResponse>> listarTodos() {
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    @GetMapping("/eventos/activos")
    public ResponseEntity<List<EventoResponse>> listarActivos() {
        return ResponseEntity.ok(eventoService.listarActivos());
    }

    @GetMapping("/eventos/activos/paginado")
    public ResponseEntity<Page<EventoResponse>> listarActivosPaginado(Pageable pageable) {
        return ResponseEntity.ok(eventoService.listarActivosPaginado(pageable));
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

    @GetMapping("/mis-eventos")
    @PreAuthorize("hasRole('ANFITRION')")
    public ResponseEntity<List<EventoResponse>> listarMisEventos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(eventoService.listarPorCreador(userDetails.getId()));
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