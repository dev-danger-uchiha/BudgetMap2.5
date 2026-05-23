package com.budgetmap.dto;

import com.budgetmap.model.enums.CategoriaEstablecimiento;
import lombok.Data;

@Data
public class EstablecimientoUpdateRequest {
    private String nombre;
    private String descripcion;
    private String direccion;
    private String telefono;
    private CategoriaEstablecimiento categoria;
    private String horarioAtencion;
    private Integer aforoMaximo;
    private String imagenUrl;
}