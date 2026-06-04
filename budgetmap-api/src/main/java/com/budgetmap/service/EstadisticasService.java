package com.budgetmap.service;

import lombok.RequiredArgsConstructor;

import com.budgetmap.dto.DashboardStatsResponse;
import com.budgetmap.model.enums.CategoriaEstablecimiento;
import com.budgetmap.model.enums.EstadoAprobacion;
import com.budgetmap.model.enums.EstadoPQRS;
import com.budgetmap.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final UsuarioRepository usuarioRepository;
    private final EstablecimientoRepository establecimientoRepository;
    private final LugarRepository lugarRepository;
    private final ReservaRepository reservaRepository;
    private final PQRSRepository pqrsRepository;

    public DashboardStatsResponse obtenerResumenGeneral() {
        log.info("Calculando estadísticas generales para el dashboard de administración...");

        // 1. Conteos básicos
        long usuarios = usuarioRepository.count();
        long est = establecimientoRepository.count();
        long estAprobados = establecimientoRepository.countByEstado(EstadoAprobacion.APROBADO);
        long lug = lugarRepository.count();
        long reservas = reservaRepository.count();
        long ticketsPendientes = pqrsRepository.countByEstado(EstadoPQRS.ABIERTO);

        // 2. Registros Mensuales de Usuarios
        Map<String, Long> registrosMensuales = new HashMap<>();
        String[] nombresMeses = { "ENE", "FEB", "MAR", "ABR", "MAY", "JUN" };
        int anioActual = LocalDateTime.now().getYear();

        for (int i = 1; i <= 4; i++) {
            LocalDateTime inicio = LocalDateTime.of(anioActual, i, 1, 0, 0);
            LocalDateTime fin = inicio.plusMonths(1).minusNanos(1);

            long totalMes = usuarioRepository.countByCreatedAtBetween(inicio, fin);
            registrosMensuales.put(nombresMeses[i - 1], totalMes);
        }

        // 3. Distribución por Categoría
        Map<String, Long> porCategoria = new HashMap<>();
        porCategoria.put("RESTAURANTE", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.RESTAURANTE));
        porCategoria.put("BAR", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.BAR));
        porCategoria.put("PANADERIA", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.PANADERIA));
        porCategoria.put("TIENDA", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.TIENDA));
        porCategoria.put("SUPERMERCADO", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.SUPERMERCADO));
        porCategoria.put("FARMACIA", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.FARMACIA));
        porCategoria.put("HOTEL", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.HOTEL));
        porCategoria.put("GIMNASIO", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.GIMNASIO));
        porCategoria.put("OTRO", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.OTRO));

        log.debug("Estadísticas calculadas: {} usuarios, {} establecimientos ({} aprobados), {} reservas, {} tickets pendientes.",
                usuarios, est, estAprobados, reservas, ticketsPendientes);

        return DashboardStatsResponse.builder()
                .totalUsuarios(usuarios)
                .totalEstablecimientos(est)
                .totalEstablecimientosAprobados(estAprobados)
                .totalLugares(lug)
                .totalReservas(reservas)
                .ticketsPendientes(ticketsPendientes)
                .establecimientosPorCategoria(porCategoria)
                .registrosMensuales(registrosMensuales)
                .build();
    }
}