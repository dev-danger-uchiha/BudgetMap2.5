package com.example.budgetmap.model;

import com.example.budgetmap.model.enums.TipoEstablecimiento;
import com.example.budgetmap.model.enums.EstadoEstablecimiento;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "establecimiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Establecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoEstablecimiento tipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String direccion;

    private String ciudad;

    private String contacto;

    private String horarios;

    @Enumerated(EnumType.STRING)
    private EstadoEstablecimiento estado = EstadoEstablecimiento.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "establecimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reserva> reservas;
}
