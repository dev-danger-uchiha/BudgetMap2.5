package com.budgetmap.dto;

import java.time.LocalDateTime;

import com.budgetmap.model.enums.CategoriaEstablecimiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstablecimientoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String nit;
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    private CategoriaEstablecimiento categoria;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;
    private String imagenUrl;
    private String rutPdfUrl;
    private Integer aforoMaximo;
    private String telefono;
    private String horarioAtencion;

}
