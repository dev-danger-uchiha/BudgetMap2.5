package com.budgetmap.repository;

import com.budgetmap.model.Lugar;
import com.budgetmap.model.enums.CategoriaLugar;
import com.budgetmap.model.enums.EstadoAprobacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LugarRepository extends JpaRepository<Lugar, Long> {

        List<Lugar> findByEstado(EstadoAprobacion estado);

        Page<Lugar> findByEstadoAndActivoTrue(EstadoAprobacion estado, Pageable pageable);

        List<Lugar> findByCategoriaAndEstado(CategoriaLugar categoria, EstadoAprobacion estado);

        @Query(value = "SELECT * FROM lugares l " +
                        "WHERE l.estado = 'APROBADO' AND l.activo = true " +
                        "AND ST_Distance_Sphere(l.ubicacion, ST_GeomFromText(:point, 4326)) <= :radioMetros", nativeQuery = true)
        List<Lugar> findLugaresCercanos(
                        @Param("point") String pointWKT,
                        @Param("radioMetros") Double radioMetros);

        @Query("SELECT l FROM Lugar l WHERE l.estado = 'PENDIENTE' AND l.activo = true")
        List<Lugar> findPendientesAprobacion();

        @Query("SELECT l FROM Lugar l WHERE l.destacado = true AND l.estado = 'APROBADO' AND l.activo = true")
        List<Lugar> findDestacados();

        @Query("SELECT COUNT(l) FROM Lugar l WHERE l.estado = :estado")
        Long countByEstado(@Param("estado") EstadoAprobacion estado);
}
