package com.budgetmap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigAlertaResponse {
    private Long id;
    private Integer radioMetros;
    private Boolean notificarPromociones;
    private Boolean notificarEventos;
    private Boolean activo;
}