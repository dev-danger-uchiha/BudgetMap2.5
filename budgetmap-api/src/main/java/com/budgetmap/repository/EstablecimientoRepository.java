package com.budgetmap.repository;

import com.budgetmap.model.Establecimiento;
import com.budgetmap.model.enums.CategoriaEstablecimiento;
import com.budgetmap.model.enums.EstadoAprobacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EstablecimientoRepository extends JpaRepository<Establecimiento, Long> {

        long countByCategoria(CategoriaEstablecimiento categoria);

        List<Establecimiento> findByPropietarioId(Long propietarioId);

        List<Establecimiento> findByEstado(EstadoAprobacion estado);

        Page<Establecimiento> findByEstadoAndActivoTrue(EstadoAprobacion estado, Pageable pageable);

        List<Establecimiento> findByCategoriaAndEstado(CategoriaEstablecimiento categoria, EstadoAprobacion estado);

        @Query(value = "SELECT * FROM establecimientos e " +
                        "WHERE e.estado = 'APROBADO' AND e.activo = true " +
                        "AND ST_Distance_Sphere(e.ubicacion, ST_GeomFromText(:point, 4326)) <= :radioMetros", nativeQuery = true)
        List<Establecimiento> findEstablecimientosCercanos(
                        @Param("point") String pointWKT,
                        @Param("radioMetros") Double radioMetros);

        @Query("SELECT e FROM Establecimiento e WHERE e.estado = 'PENDIENTE' AND e.activo = true")
        List<Establecimiento> findPendientesAprobacion();

        @Query("SELECT e FROM Establecimiento e WHERE e.propietario.id = :propietarioId AND e.activo = true")
        List<Establecimiento> findActivosByPropietario(@Param("propietarioId") Long propietarioId);

        @Query("SELECT COUNT(e) FROM Establecimiento e WHERE e.estado = :estado")
        Long countByEstado(@Param("estado") EstadoAprobacion estado);

        boolean existsByNit(String nit);
}
