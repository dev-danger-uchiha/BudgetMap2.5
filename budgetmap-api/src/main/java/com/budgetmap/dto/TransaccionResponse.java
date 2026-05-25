package com.budgetmap.dto;

import com.budgetmap.model.enums.EstadoTransaccion;
import com.budgetmap.model.enums.TipoTransaccion;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransaccionResponse {
    private Long id;
    private TipoTransaccion tipo;
    private BigDecimal monto;
    private String metodoPago;
    private String referenciaPago;
    private EstadoTransaccion estado;
    private LocalDateTime fechaTransaccion;
}