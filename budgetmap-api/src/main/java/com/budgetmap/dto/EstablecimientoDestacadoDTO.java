package com.budgetmap.dto;

import com.budgetmap.model.Establecimiento;
import com.budgetmap.model.enums.CategoriaEstablecimiento;

/**
 * DTO para exponer establecimientos destacados al público.
 *
 * ¿Por qué existe este DTO?
 * La entidad Establecimiento tiene relaciones LAZY (@ManyToOne) con Usuario
 * (propietario y moderador). Si Jackson intenta serializar esas relaciones
 * fuera de la sesión de Hibernate, lanza LazyInitializationException.
 * Además expone campos internos (nit, motivoRechazo, fechaAprobacion, etc.)
 * que el frontend público nunca necesita ver.
 *
 * Este DTO contiene SOLO los campos seguros y útiles para la vista pública.
 */
public record EstablecimientoDestacadoDTO(

        Long id,
        String nombre,
        String descripcion,
        CategoriaEstablecimiento categoria,
        String direccion,
        Double latitud,
        Double longitud,
        String imagenUrl,
        Integer aforoMaximo,
        Integer aforoActual,
        String telefono,
        String horarioAtencion,

        // Campos de publicidad del modelo de negocio
        Boolean pinDestacado,
        String colorPin

) {
    /**
     * Factory method estático: convierte una entidad Establecimiento a este DTO.
     * Se llama desde el controller para hacer la conversión de forma limpia.
     *
     * Uso: EstablecimientoDestacadoDTO.from(establecimiento)
     */
    public static EstablecimientoDestacadoDTO from(Establecimiento e) {
        return new EstablecimientoDestacadoDTO(
                e.getId(),
                e.getNombre(),
                e.getDescripcion(),
                e.getCategoria(),
                e.getDireccion(),
                e.getLatitud(),
                e.getLongitud(),
                e.getImagenUrl(),
                e.getAforoMaximo(),
                e.getAforoActual(),
                e.getTelefono(),
                e.getHorarioAtencion(),
                e.getPinDestacado(),
                e.getColorPin()
        );
    }
}
