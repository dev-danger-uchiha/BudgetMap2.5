package com.budgetmap.service;

import com.budgetmap.dto.DashboardStatsResponse;
import com.budgetmap.model.enums.CategoriaEstablecimiento;
import com.budgetmap.repository.EstablecimientoRepository;
import com.budgetmap.repository.LugarRepository;
import com.budgetmap.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class EstadisticasService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private LugarRepository lugarRepository;

    public DashboardStatsResponse obtenerResumenGeneral() {
        // 1. Conteos básicos (Los números grandes del dashboard)
        long usuarios = usuarioRepository.count();
        long est = establecimientoRepository.count();
        long lug = lugarRepository.count();

        // 2. Registros Mensuales de Usuarios (Para las barras Ene-Abr)
        Map<String, Long> registrosMensuales = new HashMap<>();
        String[] nombresMeses = { "ENE", "FEB", "MAR", "ABR", "MAY", "JUN" };
        int anioActual = LocalDateTime.now().getYear();

        for (int i = 1; i <= 4; i++) { // Solo los primeros 4 meses para tu gráfica actual
            LocalDateTime inicio = LocalDateTime.of(anioActual, i, 1, 0, 0);
            LocalDateTime fin = inicio.plusMonths(1).minusNanos(1);

            // IMPORTANTE: Asegúrate de tener countByCreatedAtBetween en tu
            // UsuarioRepository
            long totalMes = usuarioRepository.countByCreatedAtBetween(inicio, fin);
            registrosMensuales.put(nombresMeses[i - 1], totalMes);
        }

        // 3. Distribución por Categoría (Para la barra de colores)
        Map<String, Long> porCategoria = new HashMap<>();
        // Esto asume que tienes countByCategoria en tu EstablecimientoRepository
        porCategoria.put("RESTAURANTE",
                establecimientoRepository.countByCategoria(CategoriaEstablecimiento.RESTAURANTE));
        porCategoria.put("BAR", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.BAR));
        porCategoria.put("PANADERIA", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.PANADERIA));
        porCategoria.put("TIENDA", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.TIENDA));
        porCategoria.put("SUPERMERCADO",
                establecimientoRepository.countByCategoria(CategoriaEstablecimiento.SUPERMERCADO));
        porCategoria.put("FARMACIA", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.FARMACIA));
        porCategoria.put("HOTEL", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.HOTEL));
        porCategoria.put("GIMNASIO", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.GIMNASIO));
        porCategoria.put("OTRO", establecimientoRepository.countByCategoria(CategoriaEstablecimiento.OTRO));

        return DashboardStatsResponse.builder()
                .totalUsuarios(usuarios)
                .totalEstablecimientos(est)
                .totalLugares(lug)
                .establecimientosPorCategoria(porCategoria)
                .registrosMensuales(registrosMensuales)
                .build();
    }
}