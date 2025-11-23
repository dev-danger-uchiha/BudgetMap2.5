package com.example.budgetmap.repository;

import com.example.budgetmap.model.Evento;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByLugarId(Long lugarId);

    List<Evento> findByFechaInicioAfter(LocalDateTime from);

    List<Evento> findByFechaInicioBetween(LocalDateTime start, LocalDateTime end);
}
