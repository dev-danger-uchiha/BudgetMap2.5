package com.budgetmap.model;

import com.budgetmap.model.enums.TipoNotificacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoNotificacion tipo;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo;

    @Builder.Default
    @Column(name = "leida", nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    @Column(name = "accion_url", length = 500)
    private String accionUrl;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Builder.Default
    @Column(name = "origen", length = 20)
    private String origen = "SPRING";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (leida == null)
            leida = false;
        if (origen == null)
            origen = "SPRING";
    }
}