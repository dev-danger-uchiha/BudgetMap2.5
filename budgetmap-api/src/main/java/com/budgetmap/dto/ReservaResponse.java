package com.budgetmap.dto;

import com.budgetmap.model.enums.EstadoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponse {
    private Long id;
    private String codigoReserva;
    private String nombreEstablecimiento;
    private String nombreEvento;
    private String tipoReserva; // "ESTABLECIMIENTO" o "EVENTO"
    private LocalDate fechaReserva;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer numeroPersonas;
    private EstadoReserva estado;
    private Integer puntosOtorgados;
    private BigDecimal comisionCobrada;
    private LocalDateTime createdAt;
}
