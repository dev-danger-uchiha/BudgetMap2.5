package com.example.budgetmap.model;

import com.example.budgetmap.model.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Reserva extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establecimiento_id", nullable = false)
    @JsonIgnore
    private Establecimiento establecimiento;

    private LocalDateTime fechaReserva;

    private Integer cantidad = 1;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoReserva estado = EstadoReserva.PENDIENTE;
}
