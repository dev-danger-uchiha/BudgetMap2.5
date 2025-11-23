package com.example.budgetmap.model;

import com.example.budgetmap.model.enums.TipoLugar;
import com.example.budgetmap.model.enums.EstadoLugar;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "lugar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoLugar tipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String ciudad;

    private String direccion;

    @Enumerated(EnumType.STRING)
    private EstadoLugar estado = EstadoLugar.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "lugar", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Evento> eventos;
}
