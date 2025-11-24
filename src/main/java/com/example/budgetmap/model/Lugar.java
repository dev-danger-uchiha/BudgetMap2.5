package com.example.budgetmap.model;

import com.example.budgetmap.model.enums.TipoLugar;
import com.example.budgetmap.model.enums.EstadoLugar;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "lugar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Lugar extends BaseEntity {

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoLugar tipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String ciudad;

    private String direccion;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoLugar estado = EstadoLugar.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;

    @OneToMany(mappedBy = "lugar", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Evento> eventos;
}
