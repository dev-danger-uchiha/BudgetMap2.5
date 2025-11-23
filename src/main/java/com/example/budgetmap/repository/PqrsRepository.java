package com.example.budgetmap.repository;

import com.example.budgetmap.model.Pqrs;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PqrsRepository extends JpaRepository<Pqrs, Long> {
    List<Pqrs> findByUsuarioId(Long usuarioId);

    List<Pqrs> findByEstado(String estado);

    List<Pqrs> findByAsignadoAId(Long usuarioId);
}