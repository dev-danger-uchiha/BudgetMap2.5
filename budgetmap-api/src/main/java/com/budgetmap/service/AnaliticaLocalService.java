package com.budgetmap.service;

import lombok.RequiredArgsConstructor;

import com.budgetmap.dto.AnaliticaLocalDTO;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.AnaliticaLocal;
import com.budgetmap.model.Establecimiento;
import com.budgetmap.repository.AnaliticaLocalRepository;
import com.budgetmap.repository.EstablecimientoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnaliticaLocalService {

    private final AnaliticaLocalRepository analiticaRepository;
    private final EstablecimientoRepository establecimientoRepository;

    // --- MÉTODOS DE RASTREO (Tracking) ---

    @Transactional
    public void registrarVistaMapa(Long establecimientoId) {
        log.debug("Registrando vista en mapa para el establecimiento ID: {}", establecimientoId);
        AnaliticaLocal stats = obtenerOCrearAnaliticaHoy(establecimientoId);
        stats.setVistasMapa(stats.getVistasMapa() + 1);
        analiticaRepository.save(stats);
    }

    @Transactional
    public void registrarClicPerfil(Long establecimientoId) {
        log.debug("Registrando clic en perfil para el establecimiento ID: {}", establecimientoId);
        AnaliticaLocal stats = obtenerOCrearAnaliticaHoy(establecimientoId);
        stats.setClicsPerfil(stats.getClicsPerfil() + 1);
        analiticaRepository.save(stats);
    }

    @Transactional
    public void registrarVistaCupon(Long establecimientoId) {
        log.debug("Registrando vista de cupón para el establecimiento ID: {}", establecimientoId);
        AnaliticaLocal stats = obtenerOCrearAnaliticaHoy(establecimientoId);
        stats.setCuponesVistos(stats.getCuponesVistos() + 1);
        analiticaRepository.save(stats);
    }

    @Transactional
    public void actualizarExploradoresCercanos(Long establecimientoId, int cantidadDetectada) {
        log.debug("Actualizando exploradores cercanos para el establecimiento ID: {}. Detectados: {}", establecimientoId, cantidadDetectada);
        AnaliticaLocal stats = obtenerOCrearAnaliticaHoy(establecimientoId);
        
        int promedioActual = stats.getExploradoresCercanosPromedio();
        if (promedioActual == 0) {
            stats.setExploradoresCercanosPromedio(cantidadDetectada);
        } else {
            // Promediamos el valor histórico del día con el nuevo escaneo
            stats.setExploradoresCercanosPromedio((promedioActual + cantidadDetectada) / 2);
        }
        
        analiticaRepository.save(stats);
    }

    // --- MÉTODOS DE CONSULTA (Para el Dashboard del Aliado) ---

    public List<AnaliticaLocalDTO> obtenerHistorialEstablecimiento(Long establecimientoId) {
        log.info("Consultando historial de analíticas para el establecimiento ID: {}", establecimientoId);
        List<AnaliticaLocal> historial = analiticaRepository.findByEstablecimientoIdOrderByFechaDesc(establecimientoId);
        
        return historial.stream().map(a -> AnaliticaLocalDTO.builder()
                .fecha(a.getFecha())
                .clicsPerfil(a.getClicsPerfil())
                .vistasMapa(a.getVistasMapa())
                .cuponesVistos(a.getCuponesVistos())
                .exploradoresCercanosPromedio(a.getExploradoresCercanosPromedio())
                .build()
        ).collect(Collectors.toList());
    }

    // --- MÉTODO AUXILIAR ---
    private AnaliticaLocal obtenerOCrearAnaliticaHoy(Long establecimientoId) {
        LocalDate hoy = LocalDate.now();
        
        return analiticaRepository.findByEstablecimientoIdAndFecha(establecimientoId, hoy)
                .orElseGet(() -> {
                    log.info("Creando nuevo registro de analítica para hoy en el establecimiento ID: {}", establecimientoId);
                    Establecimiento establecimiento = establecimientoRepository.findById(establecimientoId)
                            .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));
                            
                    return AnaliticaLocal.builder()
                            .establecimiento(establecimiento)
                            .fecha(hoy)
                            .clicsPerfil(0)
                            .vistasMapa(0)
                            .cuponesVistos(0)
                            .exploradoresCercanosPromedio(0)
                            .build();
                });
    }
}