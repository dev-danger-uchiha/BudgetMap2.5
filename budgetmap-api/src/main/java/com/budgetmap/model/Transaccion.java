package com.budgetmap.model;

import com.budgetmap.model.enums.EstadoTransaccion;
import com.budgetmap.model.enums.TipoTransaccion;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "referencia_pago", unique = true)
    private String referenciaPago;

    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado;

    @Column(name = "fecha_transaccion", insertable = false, updatable = false)
    private LocalDateTime fechaTransaccion;
}
