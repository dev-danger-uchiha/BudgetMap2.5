package com.budgetmap.repository;

import com.budgetmap.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByUsuarioIdOrderByFechaTransaccionDesc(Long usuarioId);
    Optional<Transaccion> findByReferenciaPago(String referenciaPago);
}