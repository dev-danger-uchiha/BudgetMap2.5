package com.budgetmap.repository;

import com.budgetmap.model.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    Page<Notificacion> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId, Pageable pageable);

    @Query("SELECT n FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.leida = false ORDER BY n.createdAt DESC")
    List<Notificacion> findNoLeidasByUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.leida = false")
    Long countNoLeidasByUsuario(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true, n.fechaLectura = CURRENT_TIMESTAMP WHERE n.id = :id AND n.usuario.id = :usuarioId")
    int marcarComoLeida(@Param("id") Long id, @Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true, n.fechaLectura = CURRENT_TIMESTAMP WHERE n.usuario.id = :usuarioId AND n.leida = false")
    int marcarTodasComoLeidas(@Param("usuarioId") Long usuarioId);

    @Modifying
    void deleteByLeidaTrueAndCreatedAtBefore(LocalDateTime fecha);
}
