package com.budgetmap.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "analiticas_locales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnaliticaLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "establecimiento_id", nullable = false)
    private Establecimiento establecimiento;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "clics_perfil")
    private Integer clicsPerfil;

    @Column(name = "vistas_mapa")
    private Integer vistasMapa;

    @Column(name = "cupones_vistos")
    private Integer cuponesVistos;

    @Column(name = "exploradores_cercanos_promedio")
    private Integer exploradoresCercanosPromedio;
}