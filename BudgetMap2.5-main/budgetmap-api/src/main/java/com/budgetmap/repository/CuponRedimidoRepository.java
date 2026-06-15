package com.budgetmap.repository;

import com.budgetmap.model.CuponRedimido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuponRedimidoRepository extends JpaRepository<CuponRedimido, Long> {
    List<CuponRedimido> findByUsuarioIdOrderByFechaRedencionDesc(Long usuarioId);
    Optional<CuponRedimido> findByCodigoUnico(String codigoUnico);
}