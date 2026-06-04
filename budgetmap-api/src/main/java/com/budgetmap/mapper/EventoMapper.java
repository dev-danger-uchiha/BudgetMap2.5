package com.budgetmap.mapper;

import com.budgetmap.dto.EventoResponse;
import com.budgetmap.model.Evento;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EventoMapper {

    public EventoResponse toResponse(Evento evento) {
        return EventoResponse.builder()
                .id(evento.getId())
                .nombre(evento.getNombre())
                .descripcion(evento.getDescripcion())
                .tipoEvento(evento.getTipoEvento())
                .fechaInicio(evento.getFechaInicio())
                .fechaFin(evento.getFechaFin())
                .horaInicio(evento.getHoraInicio())
                .horaFin(evento.getHoraFin())
                .aforoMaximo(evento.getAforoMaximo())
                .aforoActual(evento.getAforoActual())
                .precio(evento.getPrecio())
                .imagenUrl(evento.getImagenUrl())
                .activo(evento.getActivo())
                .destacado(evento.getDestacado())
                .verificado(evento.getVerificado())
                .estado(evento.getEstado())
                .motivoRechazo(evento.getMotivoRechazo())
                .createdAt(evento.getCreatedAt())
                .creadorId(evento.getCreador() != null ? evento.getCreador().getId() : null)
                .creadorNombre(evento.getCreador() != null ? evento.getCreador().getNombre() : null)
                .lugarId(evento.getLugar() != null ? evento.getLugar().getId() : null)
                .lugarNombre(evento.getLugar() != null ? evento.getLugar().getNombre() : null)
                .establecimientoId(evento.getEstablecimiento() != null ? evento.getEstablecimiento().getId() : null)
                .establecimientoNombre(
                        evento.getEstablecimiento() != null ? evento.getEstablecimiento().getNombre() : null)
                .requiereReserva(evento.getPrecio() != null && evento.getPrecio().compareTo(BigDecimal.ZERO) > 0)
                .build();
    }
}
