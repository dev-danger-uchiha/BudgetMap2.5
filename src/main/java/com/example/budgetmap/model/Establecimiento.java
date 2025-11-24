package com.example.budgetmap.model;

import com.example.budgetmap.model.enums.TipoEstablecimiento;
import com.example.budgetmap.model.enums.EstadoEstablecimiento;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "establecimiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Establecimiento extends BaseEntity {

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
    @Builder.Default
    private EstadoEstablecimiento estado = EstadoEstablecimiento.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;

    @OneToMany(mappedBy = "establecimiento", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reserva> reservas;
}
