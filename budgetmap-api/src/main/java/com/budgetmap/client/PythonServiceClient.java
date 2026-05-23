package com.budgetmap.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(
    name = "budgetmap-python",
    url = "${python.service.url}", 
    configuration = PythonServiceClientConfig.class
)
public interface PythonServiceClient {

    @GetMapping("/api/geo/todo-cercano")
    @CircuitBreaker(name = "pythonService", fallbackMethod = "fallbackBuscarTodoCercano")
    @Retry(name = "pythonService")
    Map<String, Object> buscarTodoCercano(
            @RequestParam("lat") Double lat,
            @RequestParam("lon") Double lon,
            @RequestParam("radio_km") Double radio);

    @GetMapping("/api/reportes/dashboard")
    @CircuitBreaker(name = "pythonService", fallbackMethod = "fallbackDashboard")
    @Retry(name = "pythonService")
    Map<String, Object> obtenerEstadisticasPython();

    @PostMapping("/api/filtros/telemetria")
    @CircuitBreaker(name = "pythonService", fallbackMethod = "fallbackTelemetria")
    @Retry(name = "pythonService")
    Map<String, Object> enviarTelemetria(@RequestBody Map<String, Object> datos);

    default Map<String, Object> fallbackBuscarTodoCercano(Double lat, Double lon, Double radio, Exception ex) {

        System.err.println("Fallback activado para buscarTodoCercano: " + ex.getMessage());

        return Map.of(
            "success", false,
            "error", "Servicio de geolocalizacion no disponible",
            "lugares", java.util.Collections.emptyList(),
            "establecimientos", java.util.Collections.emptyList(),
            "centro_busqueda", Map.of("lat", lat, "lon", lon),
            "radio_km", radio
        );
    }
    
    default Map<String, Object> fallbackDashboard(Exception ex) {
        System.err.println("Fallback activado para dashboard: " + ex.getMessage());
        
        return Map.of(
            "success", false,
            "error", "Servicio de reportes no disponible",
            "tipo", "dashboard_admin",
            "fecha_generacion", java.time.Instant.now().toString(),
            "usuarios", Map.of("total", 0, "por_rol", java.util.Collections.emptyMap()),
            "lugares", Map.of("total", 0, "por_estado", java.util.Collections.emptyMap()),
            "establecimientos", Map.of("total", 0, "por_estado", java.util.Collections.emptyMap()),
            "reservas_mes", Map.of("total", 0, "confirmadas", 0, "canceladas", 0)
        );
    }
    
    default Map<String, Object> fallbackTelemetria(Map<String, Object> datos, Exception ex) {
        System.err.println("Fallback activado para telemetria: " + ex.getMessage());
        
        return Map.of(
            "success", false,
            "error", "Servicio de alertas no disponible",
            "alertas_generadas", 0,
            "alertas", java.util.Collections.emptyList()
        );
    }
}