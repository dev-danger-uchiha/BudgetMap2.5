package com.budgetmap.repository;

import com.budgetmap.model.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByLugarId(Long lugarId);

    @Query("SELECT e FROM Evento e WHERE e.estado = :estado")
    List<Evento> findByEstado(@Param("estado") com.budgetmap.model.enums.EstadoAprobacion estado);

    List<Evento> findByCreadorId(Long creadorId);
    List<Evento> findByCreadorIdAndActivoTrue(Long creadorId);

    Page<Evento> findByActivoTrueAndFechaInicioGreaterThanEqualOrderByFechaInicioAsc(LocalDate fecha,
            Pageable pageable);

    @Query("SELECT e FROM Evento e WHERE e.activo = true AND e.destacado = true AND e.fechaInicio >= :fecha ORDER BY e.fechaInicio ASC")
    List<Evento> findDestacados(@Param("fecha") LocalDate fecha);

    @Query("SELECT e FROM Evento e WHERE e.lugar.id = :lugarId AND e.activo = true AND e.fechaInicio >= :fecha")
    List<Evento> findProximosByLugar(@Param("lugarId") Long lugarId, @Param("fecha") LocalDate fecha);

    @Query("SELECT e FROM Evento e WHERE e.activo = true AND e.fechaInicio BETWEEN :inicio AND :fin")
    List<Evento> findByRangoFechas(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COUNT(e) FROM Evento e WHERE e.creador.id = :creadorId AND e.activo = true")
    Long countActivosByCreador(@Param("creadorId") Long creadorId);
    
    List<Evento> findAllByActivoTrue();
    
    @Query("SELECT e FROM Evento e WHERE e.destacado = true AND e.activo = true")
    List<Evento> findAllDestacados();

    @Query("SELECT e FROM Evento e WHERE " +
           "(:texto IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))) AND " +
           "(:estado IS NULL OR e.estado = :estado) AND " +
           "(:fechaInicio IS NULL OR e.fechaInicio >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR e.fechaInicio <= :fechaFin)")
    Page<Evento> findFiltradoAdmin(@Param("texto") String texto, @Param("estado") com.budgetmap.model.enums.EstadoAprobacion estado, @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin, Pageable pageable);
}
