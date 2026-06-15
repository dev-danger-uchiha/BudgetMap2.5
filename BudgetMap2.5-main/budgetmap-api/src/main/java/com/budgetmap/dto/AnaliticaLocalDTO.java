package com.budgetmap.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class AnaliticaLocalDTO {
    private LocalDate fecha;
    private Integer clicsPerfil;
    private Integer vistasMapa;
    private Integer cuponesVistos;
    private Integer exploradoresCercanosPromedio;
}