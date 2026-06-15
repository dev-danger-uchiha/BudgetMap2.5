package com.budgetmap.dto;

import com.budgetmap.model.enums.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponse {
    private Long id;
    private TipoNotificacion tipo;
    private String titulo;
    private String mensaje;
    private Long referenciaId;
    private String referenciaTipo;
    private Boolean leida;
    private LocalDateTime fechaLectura;
    private String accionUrl;
    private String imagenUrl;
    private String origen;
    private LocalDateTime createdAt;
}