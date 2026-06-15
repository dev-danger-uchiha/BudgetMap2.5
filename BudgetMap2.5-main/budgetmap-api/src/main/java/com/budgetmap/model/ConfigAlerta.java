package com.budgetmap.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "config_alertas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Builder.Default
    @Column(name = "radio_metros")
    private Integer radioMetros = 500;

    @Builder.Default
    @Column(name = "notificar_promociones", columnDefinition = "TINYINT(1)")
    private Boolean notificarPromociones = true;

    @Builder.Default
    @Column(name = "notificar_eventos", columnDefinition = "TINYINT(1)")
    private Boolean notificarEventos = true;

    @Builder.Default
    @Column(name = "activo", columnDefinition = "TINYINT(1)")
    private Boolean activo = true;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}