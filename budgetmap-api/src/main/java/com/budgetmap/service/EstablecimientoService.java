package com.budgetmap.service;

import com.budgetmap.dto.EstablecimientoRequest;
import com.budgetmap.dto.EstablecimientoResponse;
import com.budgetmap.dto.EstablecimientoUpdateRequest;
import com.budgetmap.exception.EstablecimientoException;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Establecimiento;
import com.budgetmap.model.Lugar;
import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.EstadoAprobacion;
import com.budgetmap.repository.EstablecimientoRepository;
import com.budgetmap.repository.UsuarioRepository;
import com.budgetmap.repository.LugarRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EstablecimientoService {

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LugarRepository lugarRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public EstablecimientoResponse obtenerPorUsuarioId(Long propietarioId) {
        log.debug("Consultando establecimiento del propietario ID: {}", propietarioId);
        return establecimientoRepository.findByPropietarioId(propietarioId).stream()
                .findFirst()
                .map(this::convertirAResponse)
                .orElseThrow(() -> {
                    log.warn("El usuario ID {} no tiene un establecimiento registrado", propietarioId);
                    return new ResourceNotFoundException("El usuario no tiene un establecimiento registrado");
                });
    }

    public List<EstablecimientoResponse> listarTodos() {
        return establecimientoRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<EstablecimientoResponse> listarAprobados() {
        return establecimientoRepository.findByEstado(EstadoAprobacion.APROBADO).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public Page<EstablecimientoResponse> listarAprobadosPaginado(Pageable pageable) {
        return establecimientoRepository.findByEstadoAndActivoTrue(EstadoAprobacion.APROBADO, pageable)
                .map(this::convertirAResponse);
    }

    public List<EstablecimientoResponse> listarPendientesAprobacion() {
        return establecimientoRepository.findPendientesAprobacion().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public EstablecimientoResponse obtenerPorId(Long id) {
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Intento de acceder a un establecimiento inexistente. ID: {}", id);
                    return new ResourceNotFoundException("Establecimiento no encontrado");
                });
        return convertirAResponse(est);
    }

    public List<EstablecimientoResponse> listarPorPropietario(Long propietarioId) {
        return establecimientoRepository.findByPropietarioId(propietarioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EstablecimientoResponse crear(EstablecimientoRequest request, Long propietarioId) {
        log.info("Iniciando creación de establecimiento '{}' para el propietario ID: {}", request.getNombre(), propietarioId);
        
        Usuario propietario = usuarioRepository.findById(propietarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Propietario no encontrado"));

        if (request.getNit() != null && establecimientoRepository.existsByNit(request.getNit())) {
            log.warn("Rechazo de creación: Ya existe un establecimiento con el NIT {}", request.getNit());
            throw new EstablecimientoException("Ya existe un establecimiento con ese NIT");
        }

        Point puntoUbicacion = geometryFactory.createPoint(
                new Coordinate(request.getLongitud(), request.getLatitud()));

        Establecimiento establecimiento = Establecimiento.builder()
                .nombre(request.getNombre())
                .nit(request.getNit())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .propietario(propietario)
                .direccion(request.getDireccion())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .ubicacion(puntoUbicacion)
                .imagenUrl(request.getImagenUrl())
                .rutPdfUrl(request.getRutPdfUrl())
                .aforoMaximo(request.getAforoMaximo())
                .telefono(request.getTelefono())
                .horarioAtencion(request.getHorarioAtencion())
                .estado(EstadoAprobacion.PENDIENTE)
                .aforoActual(0)
                .activo(true)
                .build();

        Establecimiento guardado = establecimientoRepository.save(establecimiento);
        log.info("Establecimiento creado exitosamente con ID: {}. Estado: PENDIENTE", guardado.getId());
        return convertirAResponse(guardado);
    }

    @Transactional
    public EstablecimientoResponse actualizarDesdePerfil(Long propietarioId, EstablecimientoUpdateRequest request) {
        log.info("Actualizando perfil del establecimiento para el propietario ID: {}", propietarioId);
        
        Establecimiento est = establecimientoRepository.findByPropietarioId(propietarioId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un establecimiento para este usuario"));

        if (request.getNombre() != null) est.setNombre(request.getNombre());
        if (request.getDescripcion() != null) est.setDescripcion(request.getDescripcion());
        if (request.getDireccion() != null) est.setDireccion(request.getDireccion());
        if (request.getTelefono() != null) est.setTelefono(request.getTelefono());
        if (request.getCategoria() != null) est.setCategoria(request.getCategoria());
        if (request.getHorarioAtencion() != null) est.setHorarioAtencion(request.getHorarioAtencion());
        if (request.getAforoMaximo() != null) est.setAforoMaximo(request.getAforoMaximo());
        if (request.getImagenUrl() != null) est.setImagenUrl(request.getImagenUrl());
        if (request.getRutPdfUrl() != null) est.setRutPdfUrl(request.getRutPdfUrl());

        return convertirAResponse(establecimientoRepository.save(est));
    }

    @Transactional
    public EstablecimientoResponse actualizar(Long id, EstablecimientoRequest request, Long propietarioId) {
        log.info("Actualización completa del establecimiento ID: {} solicitada por usuario ID: {}", id, propietarioId);
        
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));

        if (!est.getPropietario().getId().equals(propietarioId)) {
            log.warn("Intento de actualización no autorizada. Usuario ID: {} en Establecimiento ID: {}", propietarioId, id);
            throw new EstablecimientoException("No tiene permisos para editar este establecimiento");
        }

        Point puntoUbicacion = geometryFactory.createPoint(
                new Coordinate(request.getLongitud(), request.getLatitud()));

        est.setNombre(request.getNombre());
        est.setDescripcion(request.getDescripcion());
        est.setCategoria(request.getCategoria());
        est.setDireccion(request.getDireccion());
        est.setLatitud(request.getLatitud());
        est.setLongitud(request.getLongitud());
        est.setUbicacion(puntoUbicacion);
        est.setImagenUrl(request.getImagenUrl());
        est.setRutPdfUrl(request.getRutPdfUrl());
        est.setAforoMaximo(request.getAforoMaximo());
        est.setTelefono(request.getTelefono());
        est.setHorarioAtencion(request.getHorarioAtencion());
        est.setEstado(EstadoAprobacion.PENDIENTE);
        est.setMotivoRechazo(null);
        est.setFechaAprobacion(null);

        log.info("Establecimiento ID: {} actualizado. Vuelve a estado PENDIENTE.", id);
        return convertirAResponse(establecimientoRepository.save(est));
    }

    @Transactional
    public void aprobar(Long id, Long moderadorId) {
        log.info("Moderador ID: {} aprobando establecimiento ID: {}", moderadorId, id);
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));

        Usuario moderador = usuarioRepository.findById(moderadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Moderador no encontrado"));

        est.setEstado(EstadoAprobacion.APROBADO);
        est.setModerador(moderador);
        est.setFechaAprobacion(LocalDateTime.now());
        est.setMotivoRechazo(null);
        establecimientoRepository.save(est);
    }

    @Transactional
    public void rechazar(Long id, Long moderadorId, String motivo) {
        log.warn("Moderador ID: {} rechazando establecimiento ID: {}. Motivo: {}", moderadorId, id, motivo);
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));

        Usuario moderador = usuarioRepository.findById(moderadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Moderador no encontrado"));

        est.setEstado(EstadoAprobacion.RECHAZADO);
        est.setModerador(moderador);
        est.setMotivoRechazo(motivo);
        establecimientoRepository.save(est);
    }

    @Transactional
    public void actualizarAforo(Long id, Integer nuevoAforo) {
        log.debug("Actualizando aforo del establecimiento ID: {} a {}", id, nuevoAforo);
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));
        est.setAforoActual(nuevoAforo);
        establecimientoRepository.save(est);
    }

    @Transactional
    public void eliminar(Long id, Long propietarioId) {
        log.info("Solicitud de eliminación de establecimiento ID: {} por usuario ID: {}", id, propietarioId);
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));

        if (!est.getPropietario().getId().equals(propietarioId)) {
            log.error("Intento de eliminación no autorizada. Usuario ID: {} en Establecimiento ID: {}", propietarioId, id);
            throw new EstablecimientoException("No tiene permisos para eliminar este establecimiento");
        }

        est.setActivo(false);
        establecimientoRepository.save(est);
        log.info("Establecimiento ID: {} marcado como inactivo (Soft Delete).", id);
    }

    public List<Lugar> buscarCercanos(Double latitud, Double longitud, Double radioKm) {
        String pointWKT = String.format(java.util.Locale.US, "POINT(%.8f %.8f)", longitud, latitud);
        Double radioMetros = radioKm * 1000;
        return lugarRepository.findLugaresCercanos(pointWKT, radioMetros);
    }

    public Long contarPorEstado(EstadoAprobacion estado) {
        return establecimientoRepository.countByEstado(estado);
    }

    public EstablecimientoResponse convertirAResponse(Establecimiento est) {
        return EstablecimientoResponse.builder()
                .id(est.getId())
                .nombre(est.getNombre())
                .nit(est.getNit())
                .descripcion(est.getDescripcion())
                .categoria(est.getCategoria())
                .direccion(est.getDireccion())
                .latitud(est.getLatitud())
                .longitud(est.getLongitud())
                .imagenUrl(est.getImagenUrl())
                .rutPdfUrl(est.getRutPdfUrl())
                .aforoMaximo(est.getAforoMaximo())
                .aforoActual(est.getAforoActual())
                .telefono(est.getTelefono())
                .horarioAtencion(est.getHorarioAtencion())
                .estado(est.getEstado())
                .motivoRechazo(est.getMotivoRechazo())
                .activo(est.getActivo())
                .createdAt(est.getCreatedAt())
                .propietarioId(est.getPropietario().getId())
                .propietarioNombre(est.getPropietario().getNombre())
                .build();
    }
}