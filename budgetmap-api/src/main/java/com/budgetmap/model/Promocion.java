package com.budgetmap.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "promociones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establecimiento_id")
    private Establecimiento establecimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @Builder.Default
    @Column(name = "descuento_porcentaje")
    private Integer descuentoPorcentaje = 0;

    @Builder.Default
    @Column(name = "descuento_valor", precision = 10, scale = 2)
    private BigDecimal descuentoValor = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "precio_especial", precision = 10, scale = 2)
    private BigDecimal precioEspecial = BigDecimal.ZERO;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "codigo_cupon", length = 50)
    private String codigoCupon;

    @Column(name = "usos_maximos")
    private Integer usosMaximos;

    @Builder.Default
    @Column(name = "usos_actuales", nullable = false)
    private Integer usosActuales = 0;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (usosActuales == null)
            usosActuales = 0;
        if (activo == null)
            activo = true;
        if (descuentoPorcentaje == null)
            descuentoPorcentaje = 0;
        if (descuentoValor == null)
            descuentoValor = BigDecimal.ZERO;
        if (precioEspecial == null)
            precioEspecial = BigDecimal.ZERO;
    }
}