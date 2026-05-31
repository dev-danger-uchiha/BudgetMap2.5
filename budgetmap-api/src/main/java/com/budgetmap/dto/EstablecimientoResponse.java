package com.budgetmap.dto;

import com.budgetmap.model.enums.CategoriaEstablecimiento;
import com.budgetmap.model.enums.EstadoAprobacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstablecimientoResponse {

    private Long id;
    private String nombre;
    private String nit;
    private String descripcion;
    private CategoriaEstablecimiento categoria;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private String imagenUrl;
    private String rutPdfUrl;
    private Integer aforoMaximo;
    private Integer aforoActual;
    private String telefono;
    private String horarioAtencion;
    private EstadoAprobacion estado;
    private String motivoRechazo;
    private Boolean activo;
    private LocalDateTime createdAt;
    private Long propietarioId;
    private String propietarioNombre;
}