package com.budgetmap.service;

import com.budgetmap.mapper.ReservaMapper;
import lombok.RequiredArgsConstructor;

import com.budgetmap.dto.ReservaRequest;
import com.budgetmap.dto.ReservaResponse;
import com.budgetmap.exception.ReservaException;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Establecimiento;
import com.budgetmap.model.Evento;
import com.budgetmap.model.Reserva;
import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.EstadoReserva;
import com.budgetmap.repository.EstablecimientoRepository;
import com.budgetmap.repository.EventoRepository;
import com.budgetmap.repository.ReservaRepository;
import com.budgetmap.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

    // Comisión por reserva confirmada con promoción ($500 COP)
    private static final BigDecimal COMISION_RESERVA = new BigDecimal("500.00");

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstablecimientoRepository establecimientoRepository;
    private final EventoRepository eventoRepository;
    private final UsuarioService usuarioService;
    private final PuntosService puntosService;
    private final ReservaMapper reservaMapper;

    public Page<ReservaResponse> listarTodas(Pageable pageable) {
        return reservaRepository.findAll(pageable)
                .map(reservaMapper::toResponse);
    }

    // =========================================================================
    // CREAR RESERVA: Soporta evento (si tiene costo) O establecimiento (si habilitó reservas)
    // =========================================================================
    @Transactional
    public ReservaResponse crear(ReservaRequest request, Long usuarioId) {
        logger.info("Iniciando creación de reserva para el usuario ID: {}", usuarioId);

        validarRequest(request);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado: ID {}", usuarioId);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });

        Reserva.ReservaBuilder reservaBuilder = Reserva.builder()
                .codigoReserva(generarCodigoReserva())
                .usuario(usuario)
                .fechaReserva(request.getFechaReserva())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .numeroPersonas(request.getNumeroPersonas())
                .estado(EstadoReserva.PENDIENTE)
                .puntosOtorgados(calcularPuntos(request.getNumeroPersonas()))
                .notas(request.getNotas());

        if (request.getEventoId() != null) {
            procesarReservaEvento(request, reservaBuilder);
        }

        if (request.getEstablecimientoId() != null) {
            procesarReservaEstablecimiento(request, reservaBuilder);
        }

        Reserva reserva = reservaBuilder.build();
        Reserva guardada = reservaRepository.save(reserva);
        logger.info("Reserva creada exitosamente: Código {} | Estado: PENDIENTE | Puntos: {}",
                guardada.getCodigoReserva(), guardada.getPuntosOtorgados());
        return reservaMapper.toResponse(guardada);
    }

    private void validarRequest(ReservaRequest request) {
        if (request.getEventoId() == null && request.getEstablecimientoId() == null) {
            throw new ReservaException("Debe especificar un evento o un establecimiento para reservar");
        }
        if (request.getEventoId() != null && request.getEstablecimientoId() != null) {
            throw new ReservaException("No puede reservar en un evento y un establecimiento a la vez");
        }
    }

    private void procesarReservaEvento(ReservaRequest request, Reserva.ReservaBuilder builder) {
        Evento evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));

        if (evento.getPrecio() == null || evento.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReservaException("Este evento es gratuito y no requiere reserva");
        }

        if (evento.getAforoMaximo() != null) {
            Integer aforoOcupado = reservaRepository.sumAforoByEventoId(evento.getId());
            if (aforoOcupado == null) aforoOcupado = 0;

            if (aforoOcupado + request.getNumeroPersonas() > evento.getAforoMaximo()) {
                throw new ReservaException("No hay cupo disponible para este evento");
            }
        }

        evento.setAforoActual(evento.getAforoActual() + request.getNumeroPersonas());
        eventoRepository.save(evento);

        builder.evento(evento);
        if (evento.getLugar() != null) {
            builder.lugar(evento.getLugar());
        }
    }

    private void procesarReservaEstablecimiento(ReservaRequest request, Reserva.ReservaBuilder builder) {
        Establecimiento est = establecimientoRepository.findById(request.getEstablecimientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));

        if (!Boolean.TRUE.equals(est.getReservasHabilitadas())) {
            throw new ReservaException("Este establecimiento no tiene habilitadas las reservas");
        }

        Integer aforoOcupado = reservaRepository.sumAforoByEstablecimientoAndFecha(est.getId(), request.getFechaReserva());
        if (aforoOcupado == null) aforoOcupado = 0;

        Integer aforoMaximo = est.getAforoMaximo() != null ? est.getAforoMaximo() : Integer.MAX_VALUE;

        if (aforoOcupado + request.getNumeroPersonas() > aforoMaximo) {
            throw new ReservaException("No hay cupo disponible para esa fecha");
        }

        builder.establecimiento(est);
    }

    // =========================================================================
    // CANCELAR RESERVA
    // =========================================================================
    @Transactional
    public void cancelar(Long id, Long usuarioId, String motivo) {
        logger.info("Solicitud de cancelación de reserva ID: {} por el usuario ID: {}", id, usuarioId);

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            logger.warn("Intento de cancelación no autorizada. Reserva ID: {}, Usuario ID: {}", id, usuarioId);
            throw new ReservaException("No tiene permisos para cancelar esta reserva");
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA || reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new ReservaException("La reserva ya no puede ser cancelada");
        }

        // Si fue reserva de evento, devolver aforo
        if (reserva.getEvento() != null) {
            Evento evento = reserva.getEvento();
            evento.setAforoActual(Math.max(0, evento.getAforoActual() - reserva.getNumeroPersonas()));
            eventoRepository.save(evento);
            logger.debug("Aforo del evento ID: {} restaurado tras cancelación", evento.getId());
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reserva.setMotivoCancelacion(motivo);
        reservaRepository.save(reserva);
        logger.info("Reserva ID: {} cancelada exitosamente.", id);
    }

    // =========================================================================
    // CONFIRMAR ASISTENCIA (Aliado/Anfitrión ingresa el código)
    // → Aquí se otorgan los puntos al explorador
    // =========================================================================
    @Transactional
    public ReservaResponse confirmarAsistencia(String codigoReserva, Long propietarioId) {
        logger.info("Confirmando asistencia para la reserva con código: {}", codigoReserva);

        Reserva reserva = reservaRepository.findByCodigoReserva(codigoReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        // Verificar permisos: el propietario del establecimiento o creador del evento
        boolean tienePermiso = false;
        if (reserva.getEstablecimiento() != null) {
            tienePermiso = reserva.getEstablecimiento().getPropietario().getId().equals(propietarioId);
        } else if (reserva.getEvento() != null) {
            tienePermiso = reserva.getEvento().getCreador().getId().equals(propietarioId);
        }

        if (!tienePermiso) {
            logger.warn("Intento de confirmación no autorizada. Código: {}, Usuario ID: {}", codigoReserva, propietarioId);
            throw new SecurityException("No tiene permisos para gestionar esta reserva");
        }

        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new IllegalStateException("Esta reserva ya fue completada");
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("No se puede confirmar una reserva cancelada");
        }

        reserva.setEstado(EstadoReserva.COMPLETADA);
        reserva.setFechaValidacion(LocalDateTime.now());

        try {
            // *** MOMENTO CLAVE: Otorgar puntos SOLO al confirmar asistencia ***
            puntosService.sumarPuntos(reserva.getUsuario().getId(), reserva.getPuntosOtorgados());
            logger.info("Puntos otorgados al explorador ID: {} → +{} puntos",
                    reserva.getUsuario().getId(), reserva.getPuntosOtorgados());
        } catch (Exception e) {
            logger.error("Error al sumar puntos en la reserva código: {}", codigoReserva, e);
            throw e;
        }

        // Aplicar comisión si la reserva tiene promoción asociada
        if (reserva.getPromocion() != null) {
            reserva.setComisionCobrada(COMISION_RESERVA);
            logger.debug("Comisión de {} COP aplicada a reserva con promoción", COMISION_RESERVA);
        }

        return reservaMapper.toResponse(reservaRepository.save(reserva));
    }

    // =========================================================================
    // CONSULTAS
    // =========================================================================
    public List<ReservaResponse> listarPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(reservaMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<ReservaResponse> listarPorUsuarioPaginado(Long usuarioId, Pageable pageable, String tipo, String nombre, java.time.LocalDate fecha) {
        List<Reserva> reservas = reservaRepository.findByUsuarioId(usuarioId);

        if (tipo != null && !tipo.isBlank()) {
            reservas = reservas.stream()
                    .filter(r -> {
                        if (tipo.equalsIgnoreCase("EVENTO")) return r.getEvento() != null;
                        if (tipo.equalsIgnoreCase("ESTABLECIMIENTO")) return r.getEstablecimiento() != null;
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        if (nombre != null && !nombre.isBlank()) {
            String q = nombre.toLowerCase();
            reservas = reservas.stream()
                    .filter(r ->
                            (r.getEvento() != null && r.getEvento().getNombre().toLowerCase().contains(q)) ||
                            (r.getEstablecimiento() != null && r.getEstablecimiento().getNombre().toLowerCase().contains(q))
                    )
                    .collect(Collectors.toList());
        }

        if (fecha != null) {
            reservas = reservas.stream()
                    .filter(r -> r.getFechaReserva() != null && r.getFechaReserva().isEqual(fecha))
                    .collect(Collectors.toList());
        }

        reservas.sort((r1, r2) -> {
            if (r2.getCreatedAt() != null && r1.getCreatedAt() != null) {
                return r2.getCreatedAt().compareTo(r1.getCreatedAt());
            }
            return 0;
        });

        int start = (int) pageable.getOffset();
        List<ReservaResponse> dtos;
        if (start >= reservas.size()) {
            dtos = new java.util.ArrayList<>();
        } else {
            int end = Math.min(start + pageable.getPageSize(), reservas.size());
            dtos = reservas.subList(start, end).stream()
                    .map(reservaMapper::toResponse)
                    .collect(Collectors.toList());
        }

        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, reservas.size());
    }

    public List<ReservaResponse> listarPorEstablecimiento(Long estId, Long currentUserId) {
        Establecimiento est = establecimientoRepository.findById(estId)
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));
        if (!est.getPropietario().getId().equals(currentUserId)) {
            throw new SecurityException("No tiene permisos para ver reservas de este establecimiento");
        }
        return reservaRepository.findByEstablecimientoId(estId).stream()
                .map(reservaMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ReservaResponse> listarPorEvento(Long eventoId, Long currentUserId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        if (!evento.getCreador().getId().equals(currentUserId)) {
            throw new SecurityException("No tiene permisos para ver reservas de este evento");
        }
        return reservaRepository.findByEventoId(eventoId).stream()
                .map(reservaMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ReservaResponse obtenerPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        return reservaMapper.toResponse(reserva);
    }

    public ReservaResponse obtenerPorIdSeguro(Long id, Long currentUserId, boolean isAdmin) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
                
        boolean hasAccess = isAdmin;
        if (!hasAccess) {
            hasAccess = reserva.getUsuario().getId().equals(currentUserId);
        }
        if (!hasAccess && reserva.getEstablecimiento() != null) {
            hasAccess = reserva.getEstablecimiento().getPropietario().getId().equals(currentUserId);
        }
        if (!hasAccess && reserva.getEvento() != null) {
            hasAccess = reserva.getEvento().getCreador().getId().equals(currentUserId);
        }
        
        if (!hasAccess) {
            logger.warn("Acceso denegado a reserva ID: {} por usuario ID: {}", id, currentUserId);
            throw new SecurityException("No tiene permisos para ver esta reserva");
        }
        return reservaMapper.toResponse(reserva);
    }

    public ReservaResponse obtenerPorCodigo(String codigo) {
        Reserva reserva = reservaRepository.findByCodigoReserva(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        return reservaMapper.toResponse(reserva);
    }

    // =========================================================================
    // UTILIDADES INTERNAS
    // =========================================================================
    private String generarCodigoReserva() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Integer calcularPuntos(Integer numeroPersonas) {
        return numeroPersonas * 10;
    }
}