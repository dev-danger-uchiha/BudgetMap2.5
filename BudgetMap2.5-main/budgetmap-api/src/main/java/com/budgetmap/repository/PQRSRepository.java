package com.budgetmap.repository;

import com.budgetmap.model.PQRS;
import com.budgetmap.model.enums.EstadoPQRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PQRSRepository extends JpaRepository<PQRS, Long> {
    List<PQRS> findByUsuarioId(Long usuarioId);

    Page<PQRS> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId, Pageable pageable);

    List<PQRS> findByEstado(EstadoPQRS estado);

    Optional<PQRS> findByCodigoTicket(String codigoTicket);

    @Query("SELECT p FROM PQRS p WHERE p.estado = 'ABIERTO' OR p.estado = 'EN_PROCESO' ORDER BY p.createdAt ASC")
    List<PQRS> findPendientesRespuesta();

    @Query("SELECT p FROM PQRS p WHERE p.moderadorAsignadoId = :modId AND p.estado = 'EN_PROCESO'")
    List<PQRS> findAsignadosAModerador(@Param("modId") Long moderadorId);

    @Query("SELECT COUNT(p) FROM PQRS p WHERE p.estado = :estado")
    Long countByEstado(@Param("estado") EstadoPQRS estado);
}
