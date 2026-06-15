package com.budgetmap.model;

import com.budgetmap.model.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_reserva", unique = true, nullable = false, length = 20)
    private String codigoReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establecimiento_id")
    private Establecimiento establecimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lugar_id")
    private Lugar lugar;

    // --- RELACIÓN ASOCIADA AL MODELO DE NEGOCIO (COMISIONES POR CUPÓN) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promocion_id")
    private Promocion promocion;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDate fechaReserva;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Builder.Default
    @Column(name = "numero_personas", nullable = false)
    private Integer numeroPersonas = 1;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;

    @Builder.Default
    @Column(name = "puntos_otorgados")
    private Integer puntosOtorgados = 0;

    @Builder.Default
    @Column(name = "comision_cobrada", precision = 10, scale = 2)
    private BigDecimal comisionCobrada = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (estado == null)
            estado = EstadoReserva.PENDIENTE;
        if (numeroPersonas == null)
            numeroPersonas = 1;
        if (puntosOtorgados == null)
            puntosOtorgados = 0;
        if (comisionCobrada == null)
            comisionCobrada = BigDecimal.ZERO;
    }
}