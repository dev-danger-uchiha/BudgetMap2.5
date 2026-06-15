package com.budgetmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PQRSRespuestaRequest {

    @NotBlank(message = "La respuesta no puede estar vacía")
    @Size(min = 10, max = 3000, message = "La respuesta debe tener entre 10 y 3000 caracteres")
    private String respuesta;
}