package com.budgetmap.dto;

import com.budgetmap.model.Evento;
import com.budgetmap.model.enums.TipoEvento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para exponer eventos destacados al público.
 *
 * ¿Por qué existe este DTO?
 * La entidad Evento tiene 3 relaciones LAZY:
 *   - lugar      (@ManyToOne LAZY)
 *   - establecimiento (@ManyToOne LAZY)
 *   - creador    (@ManyToOne LAZY → Usuario)
 *
 * Si Jackson toca esas relaciones fuera de sesión → LazyInitializationException.
 * Si las carga en sesión y hay ciclos (Evento→Lugar→Eventos→...) → bucle infinito.
 *
 * Este DTO extrae solo los IDs y nombres necesarios de las relaciones,
 * accediendo a ellos DENTRO de la sesión (en el mismo método from()),
 * sin exponer objetos anidados completos.
 */
public record EventoDestacadoDTO(

        Long id,
        String nombre,
        String descripcion,
        TipoEvento tipoEvento,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalTime horaInicio,
        LocalTime horaFin,
        Integer aforoMaximo,
        Integer aforoActual,
        BigDecimal precio,
        String imagenUrl,

        // Referencia plana al lugar (sin anidar el objeto completo)
        Long lugarId,
        String lugarNombre,

        // Referencia plana al establecimiento
        Long establecimientoId,
        String establecimientoNombre

) {
    /**
     * Factory method estático: convierte una entidad Evento a este DTO.
     * Las relaciones LAZY (lugar, establecimiento, creador) se leen aquí,
     * mientras Hibernate todavía tiene la sesión abierta.
     *
     * Uso: EventoDestacadoDTO.from(evento)
     */
    public static EventoDestacadoDTO from(Evento e) {
        return new EventoDestacadoDTO(
                e.getId(),
                e.getNombre(),
                e.getDescripcion(),
                e.getTipoEvento(),
                e.getFechaInicio(),
                e.getFechaFin(),
                e.getHoraInicio(),
                e.getHoraFin(),
                e.getAforoMaximo(),
                e.getAforoActual(),
                e.getPrecio(),
                e.getImagenUrl(),

                // Acceder al lugar de forma segura: si es null, devolver null
                e.getLugar() != null ? e.getLugar().getId() : null,
                e.getLugar() != null ? e.getLugar().getNombre() : null,

                // Igual con establecimiento
                e.getEstablecimiento() != null ? e.getEstablecimiento().getId() : null,
                e.getEstablecimiento() != null ? e.getEstablecimiento().getNombre() : null
        );
    }
}
