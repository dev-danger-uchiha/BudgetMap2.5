package com.budgetmap.service;

import com.budgetmap.dto.EventoRequest;
import com.budgetmap.dto.EventoResponse;
import com.budgetmap.model.Establecimiento;
import com.budgetmap.model.Evento;
import com.budgetmap.model.Lugar;
import com.budgetmap.model.Usuario;
import com.budgetmap.repository.EstablecimientoRepository;
import com.budgetmap.repository.EventoRepository;
import com.budgetmap.repository.LugarRepository;
import com.budgetmap.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
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
        Usuario creador = usuarioRepository.findById(creadorId)
                .orElseThrow(() -> new RuntimeException("Creador no encontrado"));

        Lugar lugar = null;
        Establecimiento establecimiento = null;
        // Validar dónde se realizará el evento
        if (request.getLugarId() != null) {
            lugar = lugarRepository.findById(request.getLugarId())
                    .orElseThrow(() -> new RuntimeException("Lugar no encontrado"));
        } else if (request.getEstablecimientoId() != null) {
            establecimiento = establecimientoRepository.findById(request.getEstablecimientoId())
                    .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));
        } else {
            throw new RuntimeException("El evento debe estar asociado a un Lugar o a un Establecimiento");
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

        return convertirAResponse(eventoRepository.save(evento));
    }

    @Transactional
    public EventoResponse actualizar(Long id, EventoRequest request, Long creadorId) {
        Evento evento = obtenerPorIdEntity(id);

        if (!evento.getCreador().getId().equals(creadorId)) {
            throw new RuntimeException("No tiene permisos para editar este evento");
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

        return convertirAResponse(eventoRepository.save(evento));
    }

    @Transactional
    public void destacar(Long id, Boolean destacado) {
        Evento evento = obtenerPorIdEntity(id);
        evento.setDestacado(destacado);
        eventoRepository.save(evento);
    }

    @Transactional
    public void eliminar(Long id, Long creadorId) {
        Evento evento = obtenerPorIdEntity(id);

        if (!evento.getCreador().getId().equals(creadorId)) {
            throw new RuntimeException("No tiene permisos para eliminar este evento");
        }

        evento.setActivo(false);
        eventoRepository.save(evento);
    }

    public List<EventoResponse> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return eventoRepository.findByRangoFechas(inicio, fin).stream()
                .map(this::convertirAResponse).collect(Collectors.toList());
    }

    // --- MAPPER ---
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