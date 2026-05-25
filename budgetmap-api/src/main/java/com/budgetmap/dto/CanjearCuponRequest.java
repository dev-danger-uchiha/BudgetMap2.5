package com.budgetmap.dto;

import lombok.Data;

@Data
public class CanjearCuponRequest {
    private Long establecimientoId;
    private String tituloDescuento;
    private Integer costoPuntos;
}