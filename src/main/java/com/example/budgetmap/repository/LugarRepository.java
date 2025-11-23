package com.example.budgetmap.repository;

import com.example.budgetmap.model.Lugar;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LugarRepository extends JpaRepository<Lugar, Long> {
    List<Lugar> findByCiudadIgnoreCase(String ciudad);

    List<Lugar> findByEstado(String estado); // si usas enums, Spring aceptará el String nombre; puedes sobrecargar con
                                             // Enum en firmas más específicas
}
