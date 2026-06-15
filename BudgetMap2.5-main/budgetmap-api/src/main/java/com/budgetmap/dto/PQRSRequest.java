package com.budgetmap.dto;

import com.budgetmap.model.enums.TipoPQRS;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PQRSRequest {

    @NotNull(message = "El tipo de PQRS es obligatorio")
    private TipoPQRS tipo;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede superar los 200 caracteres")
    private String asunto;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    private String adjuntos;
}