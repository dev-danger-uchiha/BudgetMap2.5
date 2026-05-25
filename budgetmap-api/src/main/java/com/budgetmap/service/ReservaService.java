package com.budgetmap.service;

import com.budgetmap.dto.ReservaRequest;
import com.budgetmap.dto.ReservaResponse;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Establecimiento;
import com.budgetmap.model.Reserva;
import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.EstadoReserva;
import com.budgetmap.repository.EstablecimientoRepository;
import com.budgetmap.repository.ReservaRepository;
import com.budgetmap.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PuntosService puntosService; // Motor de puntos inyectado correctamente

    public List<ReservaResponse> listarTodas() {
        return reservaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservaResponse crear(ReservaRequest request, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Establecimiento est = establecimientoRepository.findById(request.getEstablecimientoId())
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado"));

        Integer aforoOcupado = reservaRepository.sumAforoByEstablecimientoAndFecha(
                est.getId(), request.getFechaReserva());
        if (aforoOcupado == null)
            aforoOcupado = 0;

        Integer aforoMaximo = est.getAforoMaximo() != null ? est.getAforoMaximo() : Integer.MAX_VALUE;

        if (aforoOcupado + request.getNumeroPersonas() > aforoMaximo) {
            throw new RuntimeException("No hay cupo disponible para esa fecha");
        }

        Reserva reserva = Reserva.builder()
                .codigoReserva(generarCodigoReserva())
                .usuario(usuario)
                .establecimiento(est)
                .fechaReserva(request.getFechaReserva())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .numeroPersonas(request.getNumeroPersonas())
                .estado(EstadoReserva.CONFIRMADA)
                .puntosOtorgados(calcularPuntos(request.getNumeroPersonas()))
                .notas(request.getNotas())
                .build();

        return convertirAResponse(reservaRepository.save(reserva));
    }

    @Transactional
    public void cancelar(Long id, Long usuarioId, String motivo) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tiene permisos para cancelar esta reserva");
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA ||
                reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new RuntimeException("La reserva ya no puede ser cancelada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reserva.setMotivoCancelacion(motivo);
        reservaRepository.save(reserva);
    }

    @Transactional
    public ReservaResponse confirmarAsistencia(String codigoReserva, Long propietarioId) {
        Reserva reserva = reservaRepository.findByCodigoReserva(codigoReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (!reserva.getEstablecimiento().getPropietario().getId().equals(propietarioId)) {
            throw new RuntimeException("No tiene permisos para gestionar esta reserva");
        }

        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new RuntimeException("Esta reserva ya fue completada");
        }

        reserva.setEstado(EstadoReserva.COMPLETADA);
        reserva.setFechaValidacion(LocalDateTime.now());

        // Otorgar puntos dinámicos al completar la asistencia
        puntosService.sumarPuntos(reserva.getUsuario().getId(), reserva.getPuntosOtorgados());

        return convertirAResponse(reservaRepository.save(reserva));
    }

    public List<ReservaResponse> listarPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public Page<ReservaResponse> listarPorUsuarioPaginado(Long usuarioId, Pageable pageable) {
        return reservaRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId, pageable)
                .map(this::convertirAResponse);
    }

    public List<ReservaResponse> listarPorEstablecimiento(Long estId) {
        return reservaRepository.findByEstablecimientoId(estId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public ReservaResponse obtenerPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        return convertirAResponse(reserva);
    }

    public ReservaResponse obtenerPorCodigo(String codigo) {
        Reserva reserva = reservaRepository.findByCodigoReserva(codigo)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        return convertirAResponse(reserva);
    }

    private String generarCodigoReserva() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Integer calcularPuntos(Integer numeroPersonas) {
        return numeroPersonas * 10;
    }

    private ReservaResponse convertirAResponse(Reserva reserva) {
        return ReservaResponse.builder()
                .id(reserva.getId())
                .codigoReserva(reserva.getCodigoReserva())
                .nombreEstablecimiento(reserva.getEstablecimiento().getNombre())
                .fechaReserva(reserva.getFechaReserva())
                .horaInicio(reserva.getHoraInicio())
                .horaFin(reserva.getHoraFin())
                .numeroPersonas(reserva.getNumeroPersonas())
                .estado(reserva.getEstado())
                .puntosOtorgados(reserva.getPuntosOtorgados())
                .createdAt(reserva.getCreatedAt())
                .build();
    }
}