package com.example.budgetmap.repository;

import com.example.budgetmap.model.Reserva;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findByEstablecimientoId(Long establecimientoId);

    List<Reserva> findByFechaReservaBetween(LocalDateTime start, LocalDateTime end);
}
