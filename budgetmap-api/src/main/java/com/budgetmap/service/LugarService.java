package com.budgetmap.service;

import com.budgetmap.dto.LugarRequest;
import com.budgetmap.model.Lugar;
import com.budgetmap.model.enums.EstadoAprobacion;
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
                .orElseThrow(() -> new RuntimeException("Lugar no encontrado"));
    }

    @Transactional
    public Lugar crear(LugarRequest request) {
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

        return lugarRepository.save(lugar);
    }

    @Transactional
    public Lugar actualizar(Long id, LugarRequest request) {
        Lugar lugar = obtenerPorId(id);

        if (!lugar.getLatitud().equals(request.getLatitud()) ||
                !lugar.getLongitud().equals(request.getLongitud())) {
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
        Lugar lugar = obtenerPorId(id);
        lugar.setEstado(EstadoAprobacion.APROBADO);
        lugar.setModeradorId(moderadorId);
        lugar.setFechaAprobacion(LocalDateTime.now());
        lugar.setMotivoRechazo(null);
        lugarRepository.save(lugar);
    }

    @Transactional
    public void rechazar(Long id, Long moderadorId, String motivo) {
        Lugar lugar = obtenerPorId(id);
        lugar.setEstado(EstadoAprobacion.RECHAZADO);
        lugar.setModeradorId(moderadorId);
        lugar.setMotivoRechazo(motivo);
        lugarRepository.save(lugar);
    }

    @Transactional
    public void eliminar(Long id) {
        Lugar lugar = obtenerPorId(id);
        lugar.setActivo(false);
        lugarRepository.save(lugar);
    }

    public List<Lugar> buscarCercanos(Double latitud, Double longitud, Double radioKm) {
        String pointWKT = String.format("POINT(%.8f %.8f)", longitud, latitud);
        Double radioMetros = radioKm * 1000;
        return lugarRepository.findLugaresCercanos(pointWKT, radioMetros);
    }

    public Long contarPorEstado(EstadoAprobacion estado) {
        return lugarRepository.countByEstado(estado);
    }
}
