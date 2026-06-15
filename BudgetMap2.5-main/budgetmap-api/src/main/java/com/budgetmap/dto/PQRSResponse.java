package com.budgetmap.dto;

import com.budgetmap.model.enums.EstadoPQRS;
import com.budgetmap.model.enums.TipoPQRS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PQRSResponse {

    private Long id;
    private String codigoTicket;
    private TipoPQRS tipo;
    private String asunto;
    private String descripcion;
    private EstadoPQRS estado;
    private String prioridad;
    private String respuesta;
    private String adjuntos;
    private LocalDateTime fechaRespuesta;
    private LocalDateTime createdAt;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private Long moderadorAsignadoId;
}