package com.budgetmap.model;

import com.budgetmap.model.enums.TipoPublico;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "planes_suscripcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanSuscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_publico", nullable = false)
    private TipoPublico tipoPublico;

    @Column(name = "precio_mensual", nullable = false)
    private BigDecimal precioMensual;

    @Column(name = "permite_promos_ilimitadas", columnDefinition = "TINYINT(1)")
    private Boolean permitePromosIlimitadas;

    @Column(name = "permite_estadisticas_avanzadas", columnDefinition = "TINYINT(1)")
    private Boolean permiteEstadisticasAvanzadas;

    @Column(name = "acceso_anticipado_ofertas", columnDefinition = "TINYINT(1)")
    private Boolean accesoAnticipadoOfertas;

    @Column(name = "sin_anuncios", columnDefinition = "TINYINT(1)")
    private Boolean sinAnuncios;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean activo;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}