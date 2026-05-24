package com.budgetmap.dto;

import com.budgetmap.model.Lugar;
import com.budgetmap.model.enums.CategoriaLugar;

/**
 * DTO para exponer lugares destacados al público.
 *
 * ¿Por qué existe este DTO?
 * La entidad Lugar tiene:
 *   - moderador  (@ManyToOne LAZY → Usuario)  → ya tiene @JsonIgnore, pero
 *                                               aun así puede causar problemas
 *   - eventos    (@OneToMany LAZY → List<Evento>) → también tiene @JsonIgnore,
 *                                               pero exponer la entidad completa
 *                                               es una mala práctica de seguridad.
 *
 * Además, campos como motivoRechazo, fechaAprobacion o estado son internos
 * y no deben llegar al frontend público.
 *
 * Este DTO expone solo lo que la vista del mapa o la card de lugar necesitan.
 */
public record LugarDestacadoDTO(

        Long id,
        String nombre,
        String descripcion,
        CategoriaLugar categoria,
        String direccion,
        Double latitud,
        Double longitud,
        String imagenUrl,
        Integer aforoMaximo

) {
    /**
     * Factory method estático: convierte una entidad Lugar a este DTO.
     *
     * Uso: LugarDestacadoDTO.from(lugar)
     */
    public static LugarDestacadoDTO from(Lugar l) {
        return new LugarDestacadoDTO(
                l.getId(),
                l.getNombre(),
                l.getDescripcion(),
                l.getCategoria(),
                l.getDireccion(),
                l.getLatitud(),
                l.getLongitud(),
                l.getImagenUrl(),
                l.getAforoMaximo()
        );
    }
}
