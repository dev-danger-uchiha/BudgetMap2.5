package com.budgetmap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromocionResponse implements Serializable {
    private Long id;
    private String titulo;
    private String descripcion;
    private Integer descuentoPorcentaje;
    private BigDecimal descuentoValor;
    private BigDecimal precioEspecial;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String codigoCupon;
    private Integer usosMaximos;
    private Integer usosActuales;
    private String imagenUrl;
    private Boolean activo;
    private LocalDateTime createdAt;
    private Long establecimientoId;
    private String establecimientoNombre;
    private Long eventoId;
    private String eventoNombre;
}