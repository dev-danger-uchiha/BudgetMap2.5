package com.example.budgetmap.model;

import com.example.budgetmap.model.enums.TipoPqrs;
import com.example.budgetmap.model.enums.EstadoPqrs;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "pqrs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Pqrs extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private TipoPqrs tipo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoPqrs estado = EstadoPqrs.ABIERTA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_a")
    @JsonIgnore
    private Usuario asignadoA;

    @Column(columnDefinition = "TEXT")
    private String respuesta;
}
