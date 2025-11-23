package com.example.budgetmap.repository;

import com.example.budgetmap.model.Establecimiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstablecimientoRepository extends JpaRepository<Establecimiento, Long> {
    List<Establecimiento> findByCiudadIgnoreCase(String ciudad);

    List<Establecimiento> findByEstado(String estado);

    List<Establecimiento> findByCreadoPorId(Long usuarioId);
}
