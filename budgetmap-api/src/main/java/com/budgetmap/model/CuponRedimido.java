package com.budgetmap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cupones_redimidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuponRedimido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El explorador que gastó sus puntos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // A qué establecimiento pertenece este descuento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "establecimiento_id", nullable = false)
    private Establecimiento establecimiento;

    @Column(nullable = false)
    private String tituloDescuento; // Ej: "15% de descuento en Hamburguesas"

    @Column(nullable = false, unique = true, length = 20)
    private String codigoUnico; // Ej: "BMAP-X7Y9Z"

    @Column(name = "puntos_gastados", nullable = false)
    private Integer puntosGastados;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean usado = false; // Cambiará a true cuando el restaurante lo valide

    @Column(name = "fecha_redencion")
    private LocalDateTime fechaRedencion;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;
}