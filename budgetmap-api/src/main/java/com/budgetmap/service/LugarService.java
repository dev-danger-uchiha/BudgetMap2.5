package com.budgetmap.service;

import com.budgetmap.dto.LugarRequest;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Lugar;
import com.budgetmap.model.enums.CategoriaLugar;
import com.budgetmap.model.enums.EstadoAprobacion;
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

@Slf4j
@Service
public class LugarService {

    @Autowired
    private LugarRepository lugarRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public List<Lugar> listarTodos() {
        return lugarRepository.findAll();
    }

    public List<Lugar> listarAprobados() {
        return lugarRepository.findByEstado(EstadoAprobacion.APROBADO);
    }

    public Page<Lugar> listarAprobadosPaginado(Pageable pageable) {
        return lugarRepository.findByEstadoAndActivoTrue(EstadoAprobacion.APROBADO, pageable);
    }

    public List<Lugar> listarPendientesAprobacion() {
        return lugarRepository.findPendientesAprobacion();
    }

    public Lugar obtenerPorId(Long id) {
        return lugarRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Lugar no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Lugar no encontrado");
                });
    }

    @Transactional
    public Lugar crear(LugarRequest request) {
        log.info("Creando nuevo lugar público: {}", request.getNombre());
        Point puntoDeUbicacion = geometryFactory.createPoint(
                new Coordinate(request.getLongitud(), request.getLatitud()));

        Lugar lugar = Lugar.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .direccion(request.getDireccion())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .ubicacion(puntoDeUbicacion)
                .imagenUrl(request.getImagenUrl())
                .aforoMaximo(request.getAforoMaximo())
                .estado(EstadoAprobacion.PENDIENTE)
                .activo(true)
                .build();

        Lugar guardado = lugarRepository.save(lugar);
        log.info("Lugar creado exitosamente con ID: {}", guardado.getId());
        return guardado;
    }

    @Transactional
    public Lugar actualizar(Long id, LugarRequest request) {
        log.info("Actualizando lugar ID: {}", id);
        Lugar lugar = obtenerPorId(id);

        if (!lugar.getLatitud().equals(request.getLatitud()) ||
                !lugar.getLongitud().equals(request.getLongitud())) {
            log.debug("Actualizando coordenadas geográficas del lugar ID: {}", id);
            Point nuevoPunto = geometryFactory.createPoint(
                    new Coordinate(request.getLongitud(), request.getLatitud()));
            lugar.setUbicacion(nuevoPunto);
            lugar.setLatitud(request.getLatitud());
            lugar.setLongitud(request.getLongitud());
        }

        lugar.setNombre(request.getNombre());
        lugar.setDescripcion(request.getDescripcion());
        lugar.setCategoria(request.getCategoria());
        lugar.setDireccion(request.getDireccion());
        lugar.setImagenUrl(request.getImagenUrl());
        lugar.setAforoMaximo(request.getAforoMaximo());

        return lugarRepository.save(lugar);
    }

    @Transactional
    public void aprobar(Long id, Long moderadorId) {
        log.info("Moderador ID: {} aprobando lugar ID: {}", moderadorId, id);
        Lugar lugar = obtenerPorId(id);
        lugar.setEstado(EstadoAprobacion.APROBADO);
        lugar.setModeradorId(moderadorId);
        lugar.setFechaAprobacion(LocalDateTime.now());
        lugar.setMotivoRechazo(null);
        lugarRepository.save(lugar);
    }

    @Transactional
    public void rechazar(Long id, Long moderadorId, String motivo) {
        log.warn("Moderador ID: {} rechazando lugar ID: {}. Motivo: {}", moderadorId, id, motivo);
        Lugar lugar = obtenerPorId(id);
        lugar.setEstado(EstadoAprobacion.RECHAZADO);
        lugar.setModeradorId(moderadorId);
        lugar.setMotivoRechazo(motivo);
        lugarRepository.save(lugar);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando lugar con ID: {}", id);
        Lugar lugar = obtenerPorId(id);
        lugar.setActivo(false);
        lugarRepository.save(lugar);
        log.debug("Lugar ID: {} marcado como inactivo", id);
    }

    public Page<Lugar> listarFiltradoAdmin(String texto, EstadoAprobacion estado, CategoriaLugar categoria, Pageable pageable) {
        return lugarRepository.findFiltradoAdmin(texto, estado, categoria, pageable);
    }

    public List<Lugar> buscarCercanos(Double latitud, Double longitud, Double radioKm) {
        String pointWKT = String.format(java.util.Locale.US, "POINT(%.8f %.8f)", longitud, latitud);
        Double radioMetros = radioKm * 1000;
        return lugarRepository.findLugaresCercanos(pointWKT, radioMetros);
    }

    public Long contarPorEstado(EstadoAprobacion estado) {
        return lugarRepository.countByEstado(estado);
    }
}