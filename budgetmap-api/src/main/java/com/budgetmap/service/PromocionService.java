package com.budgetmap.service;

import com.budgetmap.dto.PromocionRequest;
import com.budgetmap.dto.PromocionResponse;
import com.budgetmap.exception.PromocionException;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Establecimiento;
import com.budgetmap.model.Evento;
import com.budgetmap.model.Promocion;
import com.budgetmap.repository.EstablecimientoRepository;
import com.budgetmap.repository.EventoRepository;
import com.budgetmap.repository.PromocionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    public List<PromocionResponse> listarMisPromociones(Long propietarioId) {
        Establecimiento est = establecimientoRepository.findByPropietarioId(propietarioId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No tienes un establecimiento registrado"));

        return promocionRepository.findByEstablecimientoIdAndActivoTrue(est.getId())
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<PromocionResponse> listarTodas() {
        return promocionRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<PromocionResponse> listarPorEstablecimiento(Long establecimientoId) {
        return promocionRepository.findByEstablecimientoId(establecimientoId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<PromocionResponse> listarActivasPorEstablecimiento(Long establecimientoId) {
        return promocionRepository.findByEstablecimientoIdAndActivoTrue(establecimientoId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public Page<PromocionResponse> listarActivas(Pageable pageable) {
        return promocionRepository.findActivas(LocalDate.now(), pageable)
                .map(this::convertirAResponse);
    }

    public PromocionResponse obtenerPorId(Long id) {
        Promocion promo = promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada"));
        return convertirAResponse(promo);
    }

    @Transactional
    public PromocionResponse crear(PromocionRequest request, Long propietarioId) {
        log.info("Creando promoción para el propietario ID: {}", propietarioId);
        Establecimiento est = null;
        Evento evento = null;

        if (request.getEstablecimientoId() != null) {
            est = establecimientoRepository.findById(request.getEstablecimientoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));

            if (!est.getPropietario().getId().equals(propietarioId)) {
                throw new PromocionException("No tienes permisos sobre este establecimiento");
            }
        } else if (request.getEventoId() != null) {
            evento = eventoRepository.findById(request.getEventoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

            if (!evento.getCreador().getId().equals(propietarioId)) {
                throw new PromocionException("No tienes permisos sobre este evento");
            }
        } else {
            throw new PromocionException("La promoción debe estar asociada a un local o evento");
        }

        Promocion promo = Promocion.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .establecimiento(est)
                .evento(evento)
                .descuentoPorcentaje(request.getDescuentoPorcentaje())
                .descuentoValor(request.getDescuentoValor())
                .precioEspecial(request.getPrecioEspecial())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .codigoCupon(request.getCodigoCupon())
                .usosMaximos(request.getUsosMaximos())
                .imagenUrl(request.getImagenUrl())
                .usosActuales(0)
                .activo(true)
                .build();

        return convertirAResponse(promocionRepository.save(promo));
    }

    @Transactional
    public PromocionResponse actualizar(Long id, PromocionRequest request, Long propietarioId) {
        Promocion promo = promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada"));

        validarPropiedad(promo, propietarioId);

        promo.setTitulo(request.getTitulo());
        promo.setDescripcion(request.getDescripcion());
        promo.setDescuentoPorcentaje(request.getDescuentoPorcentaje());
        promo.setDescuentoValor(request.getDescuentoValor());
        promo.setPrecioEspecial(request.getPrecioEspecial());
        promo.setFechaInicio(request.getFechaInicio());
        promo.setFechaFin(request.getFechaFin());
        promo.setCodigoCupon(request.getCodigoCupon());
        promo.setUsosMaximos(request.getUsosMaximos());
        promo.setImagenUrl(request.getImagenUrl());

        return convertirAResponse(promocionRepository.save(promo));
    }

    @Transactional
    public void desactivar(Long id, Long propietarioId) {
        Promocion promo = promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada"));

        validarPropiedad(promo, propietarioId);
        promo.setActivo(false);
        promocionRepository.save(promo);
        log.info("Promoción ID: {} desactivada.", id);
    }

    @Transactional
    public void registrarUso(Long id) {
        Promocion promo = promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada"));

        if (promo.getUsosMaximos() != null && promo.getUsosActuales() >= promo.getUsosMaximos()) {
            throw new PromocionException("Límite de usos alcanzado");
        }

        promo.setUsosActuales(promo.getUsosActuales() + 1);
        promocionRepository.save(promo);
    }

    public Page<PromocionResponse> listarMisprocionesPaginado(Long propietarioId, Pageable pageable, String estado, String titulo) {
        Establecimiento est = establecimientoRepository.findByPropietarioId(propietarioId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No tienes un establecimiento registrado"));

        List<Promocion> promos = promocionRepository.findByEstablecimientoIdAndActivoTrue(est.getId());

        if (estado != null && !estado.isBlank()) {
            LocalDate hoy = LocalDate.now();
            promos = promos.stream()
                    .filter(p -> {
                        boolean estaActiva = p.getFechaInicio() != null && p.getFechaFin() != null &&
                                !hoy.isBefore(p.getFechaInicio()) &&
                                !hoy.isAfter(p.getFechaFin());
                        boolean estaVencida = p.getFechaFin() != null && hoy.isAfter(p.getFechaFin());
                        boolean estaAgotada = p.getUsosMaximos() != null && p.getUsosActuales() >= p.getUsosMaximos();

                        return switch (estado.toUpperCase()) {
                            case "ACTIVA" -> estaActiva && !estaAgotada;
                            case "VENCIDA" -> estaVencida;
                            case "AGOTADA" -> estaAgotada;
                            default -> true;
                        };
                    })
                    .collect(Collectors.toList());
        }

        if (titulo != null && !titulo.isBlank()) {
            promos = promos.stream()
                    .filter(p -> p.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                    .collect(Collectors.toList());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), promos.size());
        List<PromocionResponse> dtos = promos.subList(start, end).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, promos.size());
    }

    private void validarPropiedad(Promocion promo, Long propietarioId) {
        if (promo.getEstablecimiento() != null &&
                !promo.getEstablecimiento().getPropietario().getId().equals(propietarioId)) {
            throw new PromocionException("No tienes permisos para esta acción");
        }
        if (promo.getEvento() != null &&
                !promo.getEvento().getCreador().getId().equals(propietarioId)) {
            throw new PromocionException("No tienes permisos para esta acción");
        }
    }

    private PromocionResponse convertirAResponse(Promocion promo) {
        return PromocionResponse.builder()
                .id(promo.getId())
                .titulo(promo.getTitulo())
                .descripcion(promo.getDescripcion())
                .descuentoPorcentaje(promo.getDescuentoPorcentaje())
                .descuentoValor(promo.getDescuentoValor())
                .precioEspecial(promo.getPrecioEspecial())
                .fechaInicio(promo.getFechaInicio())
                .fechaFin(promo.getFechaFin())
                .codigoCupon(promo.getCodigoCupon())
                .usosMaximos(promo.getUsosMaximos())
                .usosActuales(promo.getUsosActuales())
                .imagenUrl(promo.getImagenUrl())
                .activo(promo.getActivo())
                .createdAt(promo.getCreatedAt())
                .establecimientoId(promo.getEstablecimiento() != null ? promo.getEstablecimiento().getId() : null)
                .establecimientoNombre(
                        promo.getEstablecimiento() != null ? promo.getEstablecimiento().getNombre() : null)
                .eventoId(promo.getEvento() != null ? promo.getEvento().getId() : null)
                .eventoNombre(promo.getEvento() != null ? promo.getEvento().getNombre() : null)
                .build();
    }
}