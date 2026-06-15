package com.budgetmap.mapper;

import com.budgetmap.dto.PQRSResponse;
import com.budgetmap.model.PQRS;
import org.springframework.stereotype.Component;

@Component
public class PQRSMapper {

    public PQRSResponse toResponse(PQRS pqrs) {
        return PQRSResponse.builder()
                .id(pqrs.getId())
                .codigoTicket(pqrs.getCodigoTicket())
                .tipo(pqrs.getTipo())
                .asunto(pqrs.getAsunto())
                .descripcion(pqrs.getDescripcion())
                .estado(pqrs.getEstado())
                .prioridad(pqrs.getPrioridad())
                .respuesta(pqrs.getRespuesta())
                .fechaRespuesta(pqrs.getFechaRespuesta())
                .adjuntos(pqrs.getAdjuntos())
                .createdAt(pqrs.getCreatedAt())
                .usuarioId(pqrs.getUsuario().getId())
                .usuarioNombre(pqrs.getUsuario().getNombre())
                .usuarioEmail(pqrs.getUsuario().getEmail())
                .moderadorAsignadoId(pqrs.getModeradorAsignadoId())
                .build();
    }
}
