package com.example.budgetmap.model;

import com.example.budgetmap.model.enums.TipoPqrs;
import com.example.budgetmap.model.enums.EstadoPqrs;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pqrs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pqrs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private TipoPqrs tipo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    private EstadoPqrs estado = EstadoPqrs.ABIERTA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_a")
    @JsonIgnore
    private Usuario asignadoA;

    @Column(columnDefinition = "TEXT")
    private String respuesta;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null)
            createdAt = Instant.now();
    }
}
