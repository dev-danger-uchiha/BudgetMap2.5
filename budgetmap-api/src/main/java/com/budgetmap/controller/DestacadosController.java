package com.budgetmap.controller;

import com.budgetmap.dto.EstablecimientoDestacadoDTO;
import com.budgetmap.dto.EventoDestacadoDTO;
import com.budgetmap.dto.LugarDestacadoDTO;
import com.budgetmap.repository.EstablecimientoRepository;
import com.budgetmap.repository.EventoRepository;
import com.budgetmap.repository.LugarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller público para contenido destacado en la página de inicio.
 *
 * Todos los endpoints son públicos (configurado en WebSecurityConfig
 * bajo /api/destacados/**).
 *
 * ✅ Usa DTOs en lugar de entidades JPA directamente para evitar:
 *    - LazyInitializationException (relaciones @ManyToOne LAZY fuera de sesión)
 *    - Bucles de serialización circulares (A → B → A → ...)
 *    - Exposición de campos internos (nit, motivoRechazo, moderador, etc.)
 */
@RestController
@RequestMapping("/api/destacados")
public class DestacadosController {

    @Autowired
    private EstablecimientoRepository estRepo;

    @Autowired
    private EventoRepository evtRepo;

    @Autowired
    private LugarRepository lugRepo;

    /**
     * GET /api/destacados/establecimientos
     *
     * Devuelve los establecimientos aprobados marcados como destacados.
     * Útil para el carrusel o sección "Aliados destacados" del home.
     *
     * Antes (❌ problemático):
     *   return estRepo.findDestacados();
     *   → Devuelve List<Establecimiento> con relaciones LAZY sin resolver
     *   → Jackson intenta serializar propietario y moderador → LazyInitializationException
     *
     * Ahora (✅ correcto):
     *   Convierte cada entidad a DTO dentro de la sesión activa de Hibernate,
     *   extrayendo solo los campos necesarios.
     */
    @GetMapping("/establecimientos")
    public ResponseEntity<List<EstablecimientoDestacadoDTO>> getEstablecimientos() {
        List<EstablecimientoDestacadoDTO> resultado = estRepo.findDestacados()
                .stream()
                .map(EstablecimientoDestacadoDTO::from)  // convierte cada entidad al DTO
                .toList();

        return ResponseEntity.ok(resultado);
    }

    /**
     * GET /api/destacados/eventos
     *
     * Devuelve los eventos activos y destacados a partir de hoy.
     * Útil para la sección "Eventos próximos" del home.
     *
     * Antes (❌ problemático):
     *   return evtRepo.findDestacados(LocalDate.now());
     *   → Entidad Evento tiene 3 relaciones LAZY: lugar, establecimiento, creador
     *   → Cualquiera de las tres puede lanzar LazyInitializationException
     *
     * Ahora (✅ correcto):
     *   EventoDestacadoDTO.from() accede a lugar.getId() y lugar.getNombre()
     *   DENTRO de la sesión, resolviendo lo necesario sin cargar el objeto completo.
     */
    @GetMapping("/eventos")
    public ResponseEntity<List<EventoDestacadoDTO>> getEventos() {
        List<EventoDestacadoDTO> resultado = evtRepo.findDestacados(LocalDate.now())
                .stream()
                .map(EventoDestacadoDTO::from)
                .toList();

        return ResponseEntity.ok(resultado);
    }

    /**
     * GET /api/destacados/lugares
     *
     * Devuelve los lugares aprobados y marcados como destacados.
     * Útil para el mapa o sección "Lugares por descubrir" del home.
     *
     * Antes (❌ problemático):
     *   return lugRepo.findDestacados();
     *   → Lugar tiene moderador (LAZY) y List<Evento> (LAZY)
     *   → Aunque tengan @JsonIgnore, exponer la entidad completa
     *     es mala práctica y frágil ante futuros cambios.
     *
     * Ahora (✅ correcto):
     *   LugarDestacadoDTO.from() extrae solo los campos del mapa.
     */
    @GetMapping("/lugares")
    public ResponseEntity<List<LugarDestacadoDTO>> getLugares() {
        List<LugarDestacadoDTO> resultado = lugRepo.findDestacados()
                .stream()
                .map(LugarDestacadoDTO::from)
                .toList();

        return ResponseEntity.ok(resultado);
    }
}
