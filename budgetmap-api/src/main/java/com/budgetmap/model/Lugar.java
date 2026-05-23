package com.budgetmap.model;

import com.budgetmap.model.enums.CategoriaLugar;
import com.budgetmap.model.enums.EstadoAprobacion;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lugares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaLugar categoria;

    @Column(name = "direccion", length = 300)
    private String direccion;

    @Column(name = "latitud", nullable = false)
    private Double latitud;

    @Column(name = "longitud", nullable = false)
    private Double longitud;

    @JsonIgnore
    @Column(columnDefinition = "geometry SRID 4326")
    private Point ubicacion;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "aforo_maximo")
    private Integer aforoMaximo;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAprobacion estado = EstadoAprobacion.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderador_id")
    @JsonIgnore
    private Usuario moderador;

    public void setModeradorId(Long moderadorId) {
        if (moderadorId == null) {
            this.moderador = null;
        } else {
            Usuario usuario = new Usuario();
            usuario.setId(moderadorId);
            this.moderador = usuario;
        }
    }

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "lugar", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evento> eventos = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void actualizarUbicacion() {
        if (estado == null)
            estado = EstadoAprobacion.PENDIENTE;
        if (activo == null)
            activo = true;

        if (this.latitud != null && this.longitud != null) {
            this.ubicacion = com.budgetmap.util.GeometryUtils.crearPunto(this.latitud, this.longitud);
        }
    }
}
