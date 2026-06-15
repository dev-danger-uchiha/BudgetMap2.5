package com.budgetmap.dto;

import com.budgetmap.model.enums.TipoEvento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoResponse implements Serializable {
    private Long id;
    private String nombre;
    private String descripcion;
    private TipoEvento tipoEvento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer aforoMaximo;
    private Integer aforoActual;
    private BigDecimal precio;
    private String imagenUrl;
    private Boolean activo;
    private Boolean destacado;
    private Boolean verificado;
    private com.budgetmap.model.enums.EstadoAprobacion estado;
    private String motivoRechazo;
    private LocalDateTime createdAt;
    private Long creadorId;
    private String creadorNombre;
    private Long lugarId;
    private String lugarNombre;
    private Long establecimientoId;
    private String establecimientoNombre;
    private Boolean requiereReserva; // true si precio > 0
}