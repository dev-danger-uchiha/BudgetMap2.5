package com.budgetmap.dto;

import com.budgetmap.model.enums.CategoriaLugar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LugarRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    private CategoriaLugar categoria;
    private String direccion;

    @NotNull(message = "La latitud es obligatoria")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;
    private String imagenUrl;
    private Integer aforoMaximo;
}
