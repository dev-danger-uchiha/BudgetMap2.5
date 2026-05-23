package com.budgetmap.service;

import com.budgetmap.dto.EstablecimientoRequest;
import com.budgetmap.dto.EstablecimientoResponse;
import com.budgetmap.dto.EstablecimientoUpdateRequest;
import com.budgetmap.model.Establecimiento;
import com.budgetmap.model.Lugar;
import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.EstadoAprobacion;
import com.budgetmap.repository.EstablecimientoRepository;
import com.budgetmap.repository.UsuarioRepository;
import com.budgetmap.repository.LugarRepository;
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

        return establecimientoRepository.findByPropietarioId(propietarioId).stream()
                .findFirst()
                .map(this::convertirAResponse)
                .orElseThrow(() -> new RuntimeException("El usuario no tiene un establecimiento registrado"));
    }

    // --- MÉTODOS DE CONSULTA ---
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
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));
        return convertirAResponse(est);
    }

    public List<EstablecimientoResponse> listarPorPropietario(Long propietarioId) {
        return establecimientoRepository.findByPropietarioId(propietarioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // --- MÉTODOS DE ESCRITURA (TRANSACCIONALES) ---
    @Transactional
    public EstablecimientoResponse crear(EstablecimientoRequest request, Long propietarioId) {
        Usuario propietario = usuarioRepository.findById(propietarioId)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        if (request.getNit() != null && establecimientoRepository.existsByNit(request.getNit())) {
            throw new RuntimeException("Ya existe un establecimiento con ese NIT");
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
                .aforoMaximo(request.getAforoMaximo())
                .telefono(request.getTelefono())
                .horarioAtencion(request.getHorarioAtencion())
                .estado(EstadoAprobacion.PENDIENTE)
                .aforoActual(0)
                .activo(true)
                .build();

        return convertirAResponse(establecimientoRepository.save(establecimiento));
    }

    @Transactional
    public EstablecimientoResponse actualizarDesdePerfil(Long propietarioId, EstablecimientoUpdateRequest request) {
        // Buscamos el establecimiento que pertenece a este usuario
        Establecimiento est = establecimientoRepository.findByPropietarioId(propietarioId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró un establecimiento para este usuario"));

        // Actualizamos solo los campos permitidos desde el perfil
        if (request.getNombre() != null)
            est.setNombre(request.getNombre());
        if (request.getDescripcion() != null)
            est.setDescripcion(request.getDescripcion());
        if (request.getDireccion() != null)
            est.setDireccion(request.getDireccion());
        if (request.getTelefono() != null)
            est.setTelefono(request.getTelefono());
        if (request.getCategoria() != null)
            est.setCategoria(request.getCategoria());
        if (request.getHorarioAtencion() != null)
            est.setHorarioAtencion(request.getHorarioAtencion());
        if (request.getAforoMaximo() != null)
            est.setAforoMaximo(request.getAforoMaximo());
        if (request.getImagenUrl() != null)
            est.setImagenUrl(request.getImagenUrl());

        // Al guardar, devolvemos la respuesta convertida
        return convertirAResponse(establecimientoRepository.save(est));
    }

    @Transactional

    public EstablecimientoResponse actualizar(Long id, EstablecimientoRequest request, Long propietarioId) {
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));

        if (!est.getPropietario().getId().equals(propietarioId)) {
            throw new RuntimeException("No tiene permisos para editar este establecimiento");
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
        est.setAforoMaximo(request.getAforoMaximo());
        est.setTelefono(request.getTelefono());
        est.setHorarioAtencion(request.getHorarioAtencion());
        est.setEstado(EstadoAprobacion.PENDIENTE);
        est.setMotivoRechazo(null);
        est.setFechaAprobacion(null);

        return convertirAResponse(establecimientoRepository.save(est));
    }

    @Transactional
    public void aprobar(Long id, Long moderadorId) {
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));

        Usuario moderador = usuarioRepository.findById(moderadorId)
                .orElseThrow(() -> new RuntimeException("Moderador no encontrado"));

        est.setEstado(EstadoAprobacion.APROBADO);
        est.setModerador(moderador);
        est.setFechaAprobacion(LocalDateTime.now());
        est.setMotivoRechazo(null);
        establecimientoRepository.save(est);
    }

    @Transactional
    public void rechazar(Long id, Long moderadorId, String motivo) {
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));

        Usuario moderador = usuarioRepository.findById(moderadorId)
                .orElseThrow(() -> new RuntimeException("Moderador no encontrado"));

        est.setEstado(EstadoAprobacion.RECHAZADO);
        est.setModerador(moderador);
        est.setMotivoRechazo(motivo);
        establecimientoRepository.save(est);
    }

    @Transactional
    public void actualizarAforo(Long id, Integer nuevoAforo) {
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));
        est.setAforoActual(nuevoAforo);
        establecimientoRepository.save(est);
    }

    @Transactional
    public void eliminar(Long id, Long propietarioId) {
        Establecimiento est = establecimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));

        if (!est.getPropietario().getId().equals(propietarioId)) {
            throw new RuntimeException("No tiene permisos para eliminar este establecimiento");
        }

        est.setActivo(false);
        establecimientoRepository.save(est);
    }

    // --- UTILIDADES Y BÚSQUEDA GEOGRÁFICA ---
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