package com.budgetmap.repository;

import com.budgetmap.model.Reserva;
import com.budgetmap.model.enums.EstadoReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Optional<Reserva> findByCodigoReserva(String codigoReserva);

    List<Reserva> findByUsuarioId(Long usuarioId);

    Page<Reserva> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId, Pageable pageable);

    List<Reserva> findByEstablecimientoId(Long establecimientoId);

    @Query("SELECT r FROM Reserva r WHERE r.establecimiento.id = :estId AND r.fechaReserva = :fecha AND r.estado IN ('CONFIRMADA', 'PENDIENTE')")
    List<Reserva> findReservasActivasPorFecha(@Param("estId") Long establecimientoId, @Param("fecha") LocalDate fecha);

    @Query("SELECT SUM(r.numeroPersonas) FROM Reserva r WHERE r.establecimiento.id = :estId AND r.fechaReserva = :fecha AND r.estado IN ('CONFIRMADA', 'PENDIENTE')")
    Integer sumAforoByEstablecimientoAndFecha(@Param("estId") Long establecimientoId, @Param("fecha") LocalDate fecha);

    @Query("SELECT r FROM Reserva r WHERE r.usuario.id = :usuarioId AND r.estado = :estado")
    List<Reserva> findByUsuarioAndEstado(@Param("usuarioId") Long usuarioId, @Param("estado") EstadoReserva estado);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.establecimiento.id = :estId AND r.estado = :estado")
    Long countByEstablecimientoAndEstado(@Param("estId") Long estId, @Param("estado") EstadoReserva estado);

    @Query("SELECT r FROM Reserva r WHERE r.fechaReserva = :fecha AND r.estado = 'CONFIRMADA'")
    List<Reserva> findReservasDelDia(@Param("fecha") LocalDate fecha);
}
