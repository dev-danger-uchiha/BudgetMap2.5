package com.budgetmap.repository;

import com.budgetmap.model.PlanSuscripcion;
import com.budgetmap.model.enums.TipoPublico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanSuscripcionRepository extends JpaRepository<PlanSuscripcion, Integer> {
    Optional<PlanSuscripcion> findByNombre(String nombre);
    List<PlanSuscripcion> findByTipoPublicoAndActivoTrue(TipoPublico tipoPublico);
}