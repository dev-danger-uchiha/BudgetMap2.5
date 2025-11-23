package com.example.budgetmap.repository;

import com.example.budgetmap.model.Notificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    List<Notificacion> findByLeidoFalseAndUsuarioId(Long usuarioId);
}