package com.budgetmap.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class DashboardStatsResponse {

    private long totalUsuarios;
    private long totalEstablecimientos;
    private long totalEstablecimientosAprobados;
    private long totalLugares;
    private long totalReservas;
    private long ticketsPendientes;
    private Map<String, Long> usuariosPorRol;
    private Map<String, Long> establecimientosPorCategoria;
    private Map<String, Long> lugaresPorEstado;
    private Map<String, Long> registrosMensuales;
}