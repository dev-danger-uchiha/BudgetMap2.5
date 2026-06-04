package com.budgetmap.repository;

import com.budgetmap.model.Reserva;
import com.budgetmap.model.enums.EstadoReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @EntityGraph(attributePaths = {"establecimiento", "evento"})
    Page<Reserva> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"establecimiento", "evento"})
    Optional<Reserva> findByCodigoReserva(String codigoReserva);

    @EntityGraph(attributePaths = {"establecimiento", "evento"})
    List<Reserva> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {"establecimiento", "evento"})
    Page<Reserva> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId, Pageable pageable);

    @EntityGraph(attributePaths = {"establecimiento", "evento"})
    List<Reserva> findByEstablecimientoId(Long establecimientoId);

    @Query("SELECT r FROM Reserva r WHERE r.establecimiento.id = :estId AND r.fechaReserva = :fecha AND r.estado IN ('CONFIRMADA', 'PENDIENTE')")
    List<Reserva> findReservasActivasPorFecha(@Param("estId") Long establecimientoId, @Param("fecha") LocalDate fecha);

    @Query("SELECT COALESCE(SUM(r.numeroPersonas), 0) FROM Reserva r WHERE r.establecimiento.id = :estId AND r.fechaReserva = :fecha AND r.estado = 'CONFIRMADA'")
    Integer sumAforoByEstablecimientoAndFecha(@Param("estId") Long estId, @Param("fecha") LocalDate fecha);

    @Query("SELECT r FROM Reserva r WHERE r.usuario.id = :usuarioId AND r.estado = :estado")
    List<Reserva> findByUsuarioAndEstado(@Param("usuarioId") Long usuarioId, @Param("estado") EstadoReserva estado);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.establecimiento.id = :estId AND r.estado = :estado")
    Long countByEstablecimientoAndEstado(@Param("estId") Long estId, @Param("estado") EstadoReserva estado);

    @Query("SELECT r FROM Reserva r WHERE r.fechaReserva = :fecha AND r.estado = 'CONFIRMADA'")
    List<Reserva> findReservasDelDia(@Param("fecha") LocalDate fecha);

    @EntityGraph(attributePaths = {"establecimiento", "evento"})
    List<Reserva> findByEventoId(Long eventoId);

    @Query("SELECT COALESCE(SUM(r.numeroPersonas), 0) FROM Reserva r WHERE r.evento.id = :eventoId AND r.estado IN ('PENDIENTE', 'CONFIRMADA')")
    Integer sumAforoByEventoId(@Param("eventoId") Long eventoId);
}
