package com.budgetmap.dto;

import com.budgetmap.model.enums.TipoPublico;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PlanSuscripcionDTO {
    private Integer id;
    private String nombre;
    private TipoPublico tipoPublico;
    private BigDecimal precioMensual;
    private Boolean permitePromosIlimitadas;
    private Boolean permiteEstadisticasAvanzadas;
    private Boolean accesoAnticipadoOfertas;
    private Boolean sinAnuncios;
}