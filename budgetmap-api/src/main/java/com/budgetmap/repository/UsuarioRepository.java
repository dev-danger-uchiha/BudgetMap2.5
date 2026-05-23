package com.budgetmap.repository;

import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.RolUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    long countByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fin);

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByRol(RolUsuario rol);

    Page<Usuario> findByActivoTrue(Pageable pageable);

    List<Usuario> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(String nombre, String email);

    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.activo = true")
    List<Usuario> findActivosByRol(@Param("rol") RolUsuario rol);

    @Query("SELECT u FROM Usuario u WHERE u.puntosAcumulados >= :minPuntos ORDER BY u.puntosAcumulados DESC")
    List<Usuario> findTopByPuntos(@Param("minPuntos") Integer minPuntos, Pageable pageable);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol = :rol")
    Long countByRol(@Param("rol") RolUsuario rol);
}
