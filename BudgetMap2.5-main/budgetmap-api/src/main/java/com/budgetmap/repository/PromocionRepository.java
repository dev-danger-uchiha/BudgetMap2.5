package com.budgetmap.repository;

import com.budgetmap.model.Promocion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

       List<Promocion> findByEstablecimientoIdAndActivoTrue(Long establecimientoId);

       List<Promocion> findByEstablecimientoId(Long establecimientoId);

       List<Promocion> findByEventoIdAndActivoTrue(Long eventoId);

       @Query("SELECT p FROM Promocion p WHERE p.establecimiento.id = :estId AND p.activo = true " +
                     "AND p.fechaInicio <= :hoy AND p.fechaFin >= :hoy")
       List<Promocion> findActivasByEstablecimiento(@Param("estId") Long estId, @Param("hoy") LocalDate hoy);

       @Query("SELECT p FROM Promocion p WHERE p.activo = true AND p.fechaInicio <= :hoy AND p.fechaFin >= :hoy")
       Page<Promocion> findActivas(@Param("hoy") LocalDate hoy, Pageable pageable);

       @Query("SELECT p FROM Promocion p WHERE p.codigoCupon = :codigo AND p.activo = true " +
                     "AND p.fechaInicio <= :hoy AND p.fechaFin >= :hoy")
       List<Promocion> findByCodigoCuponActivo(@Param("codigo") String codigo, @Param("hoy") LocalDate hoy);
}
