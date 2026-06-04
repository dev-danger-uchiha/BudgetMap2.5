package com.budgetmap.service;

import com.budgetmap.mapper.EstablecimientoMapper;
import lombok.RequiredArgsConstructor;

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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstablecimientoService {

    private final EstablecimientoRepository establecimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LugarRepository lugarRepository;
    private final EstablecimientoMapper establecimientoMapper;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public EstablecimientoResponse obtenerPorUsuarioId(Long propietarioId) {
        log.debug("Consultando establecimiento del propietario ID: {}", propietarioId);
        return establecimientoRepository.findByPropietarioId(propietarioId).stream()
                .findFirst()
                .map(establecimientoMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("El usuario ID {} no tiene un establecimiento registrado", propietarioId);
                    return new ResourceNotFoundException("El usuario no tiene un establecimiento registrado");
                });
    }

    public List<EstablecimientoResponse> listarTodos() {
        return establecimientoRepository.findAll().stream()
                .map(establecimientoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "establecimientos_aprobados")
    public Page<EstablecimientoResponse> listarAprobados(Pageable pageable) {
        return establecimientoRepository.findByEstadoAndActivoTrue(EstadoAprobacion.APROBADO, pageable)
                .map(establecimientoMapper::toResponse);
    }

    public Page<EstablecimientoResponse> listarPendientesAprobacion(Pageable pageable) {
        return establecimientoRepository.findByEstadoAndActivoTrue(EstadoAprobacion.PENDIENTE, pageable)
                .map(establecimientoMapper::toResponse);
    }

    public EstablecimientoResponse obtenerPorId(Long id) {
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Intento de acceder a un establecimiento inexistente. ID: {}", id);
                    return new ResourceNotFoundException("Establecimiento no encontrado");
                });
        return establecimientoMapper.toResponse(est);
    }

    public EstablecimientoResponse obtenerPorIdSeguro(Long id, Long currentUserId, boolean isAdmin) {
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));
                
        if (est.getEstado() != EstadoAprobacion.APROBADO) {
            boolean hasAccess = isAdmin || (currentUserId != null && est.getPropietario().getId().equals(currentUserId));
            if (!hasAccess) {
                log.warn("Acceso denegado a establecimiento ID: {} por usuario ID: {}", id, currentUserId);
                throw new ResourceNotFoundException("Establecimiento no encontrado");
            }
        }
        return establecimientoMapper.toResponse(est);
    }

    public List<EstablecimientoResponse> listarPorPropietario(Long propietarioId) {
        return establecimientoRepository.findByPropietarioId(propietarioId).stream()
                .map(establecimientoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "establecimientos_aprobados", allEntries = true)
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
                .reservasHabilitadas(request.getReservasHabilitadas() != null ? request.getReservasHabilitadas() : false)
                .estado(EstadoAprobacion.PENDIENTE)
                .aforoActual(0)
                .activo(true)
                .build();

        Establecimiento guardado = establecimientoRepository.save(establecimiento);
        log.info("Establecimiento creado exitosamente con ID: {}. Estado: PENDIENTE", guardado.getId());
        return establecimientoMapper.toResponse(guardado);
    }

    @Transactional
    @CacheEvict(value = "establecimientos_aprobados", allEntries = true)
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
        if (request.getReservasHabilitadas() != null) est.setReservasHabilitadas(request.getReservasHabilitadas());

        return establecimientoMapper.toResponse(establecimientoRepository.save(est));
    }

    @Transactional
    @CacheEvict(value = "establecimientos_aprobados", allEntries = true)
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

        boolean requiresApproval = false;

        // Check NIT change
        if (request.getNit() != null && !request.getNit().equals(est.getNit())) {
            est.setNit(request.getNit());
            requiresApproval = true;
        }

        // Check PDF change
        if (request.getRutPdfUrl() != null && !request.getRutPdfUrl().equals(est.getRutPdfUrl())) {
            est.setRutPdfUrl(request.getRutPdfUrl());
            requiresApproval = true;
        }

        // Check Address/Location change
        boolean direccionCambiada = request.getDireccion() != null && !request.getDireccion().equals(est.getDireccion());
        boolean coordenadasCambiadas = (request.getLatitud() != null && !request.getLatitud().equals(est.getLatitud())) || 
                                       (request.getLongitud() != null && !request.getLongitud().equals(est.getLongitud()));
        if (direccionCambiada || coordenadasCambiadas) {
            est.setDireccion(request.getDireccion());
            est.setLatitud(request.getLatitud());
            est.setLongitud(request.getLongitud());
            est.setUbicacion(puntoUbicacion);
            requiresApproval = true;
        }

        // Update the rest without requiring approval
        est.setNombre(request.getNombre());
        est.setDescripcion(request.getDescripcion());
        est.setCategoria(request.getCategoria());
        est.setImagenUrl(request.getImagenUrl());
        est.setAforoMaximo(request.getAforoMaximo());
        est.setTelefono(request.getTelefono());
        est.setHorarioAtencion(request.getHorarioAtencion());
        if (request.getReservasHabilitadas() != null) est.setReservasHabilitadas(request.getReservasHabilitadas());
        
        if (requiresApproval) {
            est.setEstado(EstadoAprobacion.PENDIENTE);
            est.setMotivoRechazo(null);
            est.setFechaAprobacion(null);
            log.info("Establecimiento ID: {} actualizado. Vuelve a estado PENDIENTE por cambios sensibles.", id);
        } else {
            log.info("Establecimiento ID: {} actualizado. Mantiene su estado actual.", id);
        }

        return establecimientoMapper.toResponse(establecimientoRepository.save(est));
    }

    @Transactional
    @CacheEvict(value = "establecimientos_aprobados", allEntries = true)
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
    @CacheEvict(value = "establecimientos_aprobados", allEntries = true)
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
    @CacheEvict(value = "establecimientos_aprobados", allEntries = true)
    public void actualizarAforo(Long id, Integer nuevoAforo) {
        log.debug("Actualizando aforo del establecimiento ID: {} a {}", id, nuevoAforo);
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));
        est.setAforoActual(nuevoAforo);
        establecimientoRepository.save(est);
    }

    public Page<EstablecimientoResponse> listarFiltradoAdmin(String texto, EstadoAprobacion estado, String nit, Pageable pageable) {
        return establecimientoRepository.findFiltradoAdmin(texto, estado, nit, pageable)
                .map(establecimientoMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = "establecimientos_aprobados", allEntries = true)
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
}