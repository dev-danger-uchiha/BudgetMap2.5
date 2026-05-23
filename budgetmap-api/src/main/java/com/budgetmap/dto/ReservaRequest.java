package com.budgetmap.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservaRequest {

    @NotNull(message = "El establecimiento es obligatorio")
    private Long establecimientoId;

    @NotNull(message = "La fecha de reserva es obligatoria")
    @Future(message = "La fecha debe ser futura")
    private LocalDate fechaReserva;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;
    private LocalTime horaFin;

    @NotNull(message = "El número de personas es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 persona")
    private Integer numeroPersonas;
    private String notas;
}
