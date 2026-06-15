package com.budgetmap.model;

import com.budgetmap.model.enums.EstadoPQRS;
import com.budgetmap.model.enums.TipoPQRS;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pqrs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PQRS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_ticket", unique = true, nullable = false, length = 20)
    private String codigoTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPQRS tipo;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPQRS estado = EstadoPQRS.ABIERTO;

    @Column(name = "moderador_asignado_id")
    private Long moderadorAsignadoId;

    @Column(name = "respuesta", columnDefinition = "TEXT")
    private String respuesta;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "adjuntos", length = 1000)
    private String adjuntos;

    @Builder.Default
    @Column(name = "prioridad", length = 10)
    private String prioridad = "MEDIA";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (estado == null)
            estado = EstadoPQRS.ABIERTO;
        if (prioridad == null)
            prioridad = "MEDIA";
    }
}