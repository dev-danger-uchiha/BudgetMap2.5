package com.budgetmap.model;

import com.budgetmap.model.enums.CategoriaEstablecimiento;
import com.budgetmap.model.enums.EstadoAprobacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "establecimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Establecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(name = "nit", unique = true, length = 20)
    private String nit;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaEstablecimiento categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;

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

    @Column(name = "rut_pdf_url", length = 500)
    private String rutPdfUrl;

    @Column(name = "aforo_maximo")
    private Integer aforoMaximo;

    @Builder.Default
    @Column(name = "aforo_actual", nullable = false)
    private Integer aforoActual = 0;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "horario_atencion", length = 200)
    private String horarioAtencion;

    @Builder.Default
    @Column(name = "reservas_habilitadas", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean reservasHabilitadas = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAprobacion estado = EstadoAprobacion.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderador_id")
    private Usuario moderador;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    // --- NUEVOS CAMPOS: PUBLICIDAD Y ADS ---
    @Column(name = "pin_destacado", columnDefinition = "TINYINT(1)")
    private Boolean pinDestacado;

    @Column(name = "verificado", columnDefinition = "TINYINT(1)")
    private Boolean verificado = false;

    @Column(name = "color_pin", length = 20)
    private String colorPin;

    @Column(name = "fin_publicidad")
    private LocalDateTime finPublicidad;

    @Builder.Default
    @Column(name = "destacado", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean destacado = false;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void actualizarUbicacion() {
        if (estado == null)
            estado = EstadoAprobacion.PENDIENTE;
        if (activo == null)
            activo = true;
        if (aforoActual == null)
            aforoActual = 0;
        if (destacado == null)
            destacado = false;
        if (pinDestacado == null)
            pinDestacado = false;
        if (this.latitud != null && this.longitud != null) {
            this.ubicacion = com.budgetmap.util.GeometryUtils.crearPunto(this.latitud, this.longitud);
        }
    }
}