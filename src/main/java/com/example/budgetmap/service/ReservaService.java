package com.example.budgetmap.service;

import com.example.budgetmap.model.Reserva;
import com.example.budgetmap.model.enums.EstadoReserva;
import com.example.budgetmap.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva save(Reserva reserva) {
        if (reserva.getEstado() == null)
            reserva.setEstado(EstadoReserva.PENDIENTE);
        return reservaRepository.save(reserva);
    }

    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }

    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    public List<Reserva> findByEstablecimientoId(Long establecimientoId) {
        return reservaRepository.findByEstablecimientoId(establecimientoId);
    }

    public List<Reserva> findByFechaRange(LocalDateTime start, LocalDateTime end) {
        return reservaRepository.findByFechaReservaBetween(start, end);
    }

    public void cancelById(Long id) {
        reservaRepository.findById(id).ifPresent(r -> {
            r.setEstado(EstadoReserva.CANCELADA);
            reservaRepository.save(r);
        });
    }
}
