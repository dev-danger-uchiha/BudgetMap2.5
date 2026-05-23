package com.budgetmap.dto;

import com.budgetmap.model.enums.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String descripcion;

    @NotNull(message = "El tipo de evento es obligatorio")
    private TipoEvento tipoEvento;
    private Long lugarId;
    private Long establecimientoId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer aforoMaximo;
    private BigDecimal precio;
    private String imagenUrl;
}