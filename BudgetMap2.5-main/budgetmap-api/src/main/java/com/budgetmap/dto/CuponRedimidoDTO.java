package com.budgetmap.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CuponRedimidoDTO {
    private Long id;
    private String nombreEstablecimiento; // Solo el nombre, no toda la entidad
    private String tituloDescuento;
    private String codigoUnico;
    private Integer puntosGastados;
    private Boolean usado;
    private LocalDateTime fechaExpiracion;
}