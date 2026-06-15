package com.budgetmap.repository;

import com.budgetmap.model.AnaliticaLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnaliticaLocalRepository extends JpaRepository<AnaliticaLocal, Long> {
    List<AnaliticaLocal> findByEstablecimientoIdOrderByFechaDesc(Long establecimientoId);
    Optional<AnaliticaLocal> findByEstablecimientoIdAndFecha(Long establecimientoId, LocalDate fecha);
}