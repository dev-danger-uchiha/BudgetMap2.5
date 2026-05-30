package com.budgetmap.service;

import com.budgetmap.dto.EventoRequest;
import com.budgetmap.dto.EventoResponse;
import com.budgetmap.exception.EventoException;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Establecimiento;
import com.budgetmap.model.Evento;
import com.budgetmap.model.Lugar;
import com.budgetmap.model.Usuario;
import com.budgetmap.repository.EstablecimientoRepository;
import com.budgetmap.repository.EventoRepository;
import com.budgetmap.repository.LugarRepository;
import com.budgetmap.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.budgetmap.model.enums.EstadoAprobacion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private LugarRepository lugarRepository;

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<EventoResponse> listarTodos() {
        return eventoRepository.findAll().stream().map(this::convertirAResponse).collect(Collectors.toList());
    }

    public List<EventoResponse> listarPendientesAprobacion() {
        return eventoRepository.findByEstado(EstadoAprobacion.PENDIENTE).stream()
                .map(this::convertirAResponse).collect(Collectors.toList());
    }

    @Transactional
    public void aprobar(Long id, Long moderadorId) {
        log.info("Moderador ID: {} aprobando evento ID: {}", moderadorId, id);
        Evento evento = obtenerPorIdEntity(id);
        
        Usuario moderador = usuarioRepository.findById(moderadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Moderador no encontrado"));

        evento.setEstado(EstadoAprobacion.APROBADO);
        evento.setModerador(moderador);
        evento.setFechaAprobacion(LocalDateTime.now());
        evento.setMotivoRechazo(null);
        eventoRepository.save(evento);
    }

    @Transactional
    public void rechazar(Long id, Long moderadorId, String motivo) {
        log.warn("Moderador ID: {} rechazando evento ID: {}. Motivo: {}", moderadorId, id, motivo);
        Evento evento = obtenerPorIdEntity(id);
        
        Usuario moderador = usuarioRepository.findById(moderadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Moderador no encontrado"));

        evento.setEstado(EstadoAprobacion.RECHAZADO);
        evento.setModerador(moderador);
        evento.setMotivoRechazo(motivo);
        eventoRepository.save(evento);
    }

    public List<EventoResponse> listarActivos() {
        return eventoRepository.findByActivoTrueAndFechaInicioGreaterThanEqualOrderByFechaInicioAsc(
                LocalDate.now(), Pageable.unpaged()).getContent().stream()
                .map(this::convertirAResponse).collect(Collectors.toList());
    }

    public Page<EventoResponse> listarActivosPaginado(Pageable pageable) {
        return eventoRepository.findByActivoTrueAndFechaInicioGreaterThanEqualOrderByFechaInicioAsc(
                LocalDate.now(), pageable).map(this::convertirAResponse);
    }

    public List<EventoResponse> listarDestacados() {
        return eventoRepository.findDestacados(LocalDate.now()).stream()
                .map(this::convertirAResponse).collect(Collectors.toList());
    }

    public Evento obtenerPorIdEntity(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Intento de consulta a un evento inexistente: ID {}", id);
                    return new ResourceNotFoundException("Evento no encontrado");
                });
    }

    public EventoResponse obtenerPorId(Long id) {
        return convertirAResponse(obtenerPorIdEntity(id));
    }

    public List<EventoResponse> listarPorLugar(Long lugarId) {
        return eventoRepository.findByLugarId(lugarId).stream()
                .map(this::convertirAResponse).collect(Collectors.toList());
    }

    public List<EventoResponse> listarPorCreador(Long creadorId) {
        return eventoRepository.findByCreadorIdAndActivoTrue(creadorId).stream()
                .map(this::convertirAResponse).collect(Collectors.toList());
    }

    @Transactional
    public EventoResponse crear(EventoRequest request, Long creadorId) {
        log.info("Iniciando creación de evento '{}' por el usuario ID: {}", request.getNombre(), creadorId);

        Usuario creador = usuarioRepository.findById(creadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Creador no encontrado"));

        Lugar lugar = null;
        Establecimiento establecimiento = null;
        
        // Validar dónde se realizará el evento
        if (request.getLugarId() != null) {
            lugar = lugarRepository.findById(request.getLugarId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lugar no encontrado"));
        } else if (request.getEstablecimientoId() != null) {
            establecimiento = establecimientoRepository.findById(request.getEstablecimientoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));
        } else {
            log.warn("Rechazo de creación: El evento no tiene Lugar ni Establecimiento asignado.");
            throw new EventoException("El evento debe estar asociado a un Lugar o a un Establecimiento");
        }

        Evento evento = Evento.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .tipoEvento(request.getTipoEvento())
                .lugar(lugar)
                .establecimiento(establecimiento)
                .creador(creador)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .aforoMaximo(request.getAforoMaximo())
                .precio(request.getPrecio())
                .imagenUrl(request.getImagenUrl())
                .aforoActual(0)
                .activo(true)
                .destacado(false)
                .build();

        Evento guardado = eventoRepository.save(evento);
        log.info("Evento creado exitosamente con ID: {}", guardado.getId());
        return convertirAResponse(guardado);
    }

    @Transactional
    public EventoResponse actualizar(Long id, EventoRequest request, Long creadorId) {
        log.info("Usuario ID: {} solicita actualizar el evento ID: {}", creadorId, id);
        Evento evento = obtenerPorIdEntity(id);

        if (!evento.getCreador().getId().equals(creadorId)) {
            log.warn("Intento de edición no autorizada. Usuario ID: {} en Evento ID: {}", creadorId, id);
            throw new EventoException("No tiene permisos para editar este evento");
        }

        evento.setNombre(request.getNombre());
        evento.setDescripcion(request.getDescripcion());
        evento.setTipoEvento(request.getTipoEvento());
        evento.setFechaInicio(request.getFechaInicio());
        evento.setFechaFin(request.getFechaFin());
        evento.setHoraInicio(request.getHoraInicio());
        evento.setHoraFin(request.getHoraFin());
        evento.setAforoMaximo(request.getAforoMaximo());
        evento.setPrecio(request.getPrecio());
        evento.setImagenUrl(request.getImagenUrl());

        log.debug("Evento ID: {} actualizado correctamente", id);
        return convertirAResponse(eventoRepository.save(evento));
    }

    @Transactional
    public void destacar(Long id, Boolean destacado) {
        log.info("Cambiando estado de destaque a {} para el evento ID: {}", destacado, id);
        Evento evento = obtenerPorIdEntity(id);
        evento.setDestacado(destacado);
        eventoRepository.save(evento);
    }

    @Transactional
    public void eliminar(Long id, Long creadorId) {
        log.info("Solicitud de eliminación de evento ID: {} por usuario ID: {}", id, creadorId);
        Evento evento = obtenerPorIdEntity(id);

        if (!evento.getCreador().getId().equals(creadorId)) {
            log.error("Intento de eliminación no autorizada. Usuario ID: {} en Evento ID: {}", creadorId, id);
            throw new EventoException("No tiene permisos para eliminar este evento");
        }

        evento.setActivo(false);
        eventoRepository.save(evento);
        log.info("Evento ID: {} marcado como inactivo (Soft Delete).", id);
    }

    public List<EventoResponse> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return eventoRepository.findByRangoFechas(inicio, fin).stream()
                .map(this::convertirAResponse).collect(Collectors.toList());
    }

    public Page<EventoResponse> listarMisEventosPaginado(Long creadorId, Pageable pageable, String tipo, String nombre) {
        List<Evento> eventos = eventoRepository.findByCreadorIdAndActivoTrue(creadorId);

        if (tipo != null && !tipo.isBlank()) {
            eventos = eventos.stream()
                    .filter(e -> e.getTipoEvento() != null && e.getTipoEvento().name().equalsIgnoreCase(tipo))
                    .collect(Collectors.toList());
        }

        if (nombre != null && !nombre.isBlank()) {
            eventos = eventos.stream()
                    .filter(e -> e.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .collect(Collectors.toList());
        }

        eventos.sort((e1, e2) -> e2.getFechaInicio().compareTo(e1.getFechaInicio()));

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), eventos.size());
        List<EventoResponse> dtos = eventos.subList(start, end).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, eventos.size());
    }

    public java.util.Map<String, Object> obtenerEstadisticasAnfitrion(Long creadorId) {
        List<Evento> eventos = eventoRepository.findByCreadorIdAndActivoTrue(creadorId);
        long totalEventos = eventos.size();
        long totalReservas = eventos.stream().mapToLong(e -> e.getAforoActual() != null ? e.getAforoActual() : 0).sum();
        double recaudacionEstimada = eventos.stream().mapToDouble(e -> (e.getAforoActual() != null ? e.getAforoActual() : 0) * (e.getPrecio() != null ? e.getPrecio().doubleValue() : 0.0)).sum();

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalEventos", totalEventos);
        stats.put("totalReservas", totalReservas);
        stats.put("recaudacionEstimada", recaudacionEstimada);
        return stats;
    }

    private EventoResponse convertirAResponse(Evento evento) {
        return EventoResponse.builder()
                .id(evento.getId())
                .nombre(evento.getNombre())
                .descripcion(evento.getDescripcion())
                .tipoEvento(evento.getTipoEvento())
                .fechaInicio(evento.getFechaInicio())
                .fechaFin(evento.getFechaFin())
                .horaInicio(evento.getHoraInicio())
                .horaFin(evento.getHoraFin())
                .aforoMaximo(evento.getAforoMaximo())
                .aforoActual(evento.getAforoActual())
                .precio(evento.getPrecio())
                .imagenUrl(evento.getImagenUrl())
                .activo(evento.getActivo())
                .destacado(evento.getDestacado())
                .estado(evento.getEstado())
                .motivoRechazo(evento.getMotivoRechazo())
                .createdAt(evento.getCreatedAt())
                .creadorId(evento.getCreador() != null ? evento.getCreador().getId() : null)
                .creadorNombre(evento.getCreador() != null ? evento.getCreador().getNombre() : null)
                .lugarId(evento.getLugar() != null ? evento.getLugar().getId() : null)
                .lugarNombre(evento.getLugar() != null ? evento.getLugar().getNombre() : null)
                .establecimientoId(evento.getEstablecimiento() != null ? evento.getEstablecimiento().getId() : null)
                .establecimientoNombre(
                        evento.getEstablecimiento() != null ? evento.getEstablecimiento().getNombre() : null)
                .build();
    }
}