package com.budgetmap.repository;

import com.budgetmap.model.ConfigAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConfigAlertaRepository extends JpaRepository<ConfigAlerta, Long> {

    Optional<ConfigAlerta> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);

    @Query("SELECT c.radioMetros FROM ConfigAlerta c WHERE c.usuario.id = :usuarioId AND c.activo = true")
    Integer findRadioByUsuarioId(@Param("usuarioId") Long usuarioId);
}