package com.budgetmap.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AprobacionRequest {

    @NotNull(message = "La decisión de aprobación es obligatoria")
    private Boolean aprobar;
    private String motivoRechazo;
}
