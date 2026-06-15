package com.budgetmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PromocionRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;
    private String descripcion;
    private Long establecimientoId;
    private Long eventoId;
    private Integer descuentoPorcentaje;
    private BigDecimal descuentoValor;
    private BigDecimal precioEspecial;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;
    private String codigoCupon;
    private Integer usosMaximos;
    private String imagenUrl;
}