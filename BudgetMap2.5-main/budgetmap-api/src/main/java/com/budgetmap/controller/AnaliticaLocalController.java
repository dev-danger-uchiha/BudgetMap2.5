package com.budgetmap.controller;

import com.budgetmap.dto.AnaliticaLocalDTO;
import com.budgetmap.service.AnaliticaLocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analiticas")
public class AnaliticaLocalController {

    @Autowired
    private AnaliticaLocalService analiticaService;

    // Endpoint para que el Frontend avise cuando alguien abre el mapa y ve el pin
    @PostMapping("/{establecimientoId}/vista-mapa")
    public ResponseEntity<Void> registrarVistaMapa(@PathVariable Long establecimientoId) {
        analiticaService.registrarVistaMapa(establecimientoId);
        return ResponseEntity.ok().build();
    }

    // Endpoint para que el Frontend avise cuando alguien entra al perfil del local
    @PostMapping("/{establecimientoId}/clic-perfil")
    public ResponseEntity<Void> registrarClicPerfil(@PathVariable Long establecimientoId) {
        analiticaService.registrarClicPerfil(establecimientoId);
        return ResponseEntity.ok().build();
    }

    // Endpoint para que el Frontend avise cuando alguien ve un cupón de este local
    @PostMapping("/{establecimientoId}/vista-cupon")
    public ResponseEntity<Void> registrarVistaCupon(@PathVariable Long establecimientoId) {
        analiticaService.registrarVistaCupon(establecimientoId);
        return ResponseEntity.ok().build();
    }

    // Endpoint interno para actualizar el promedio del radar
    @PostMapping("/{establecimientoId}/radar-exploradores")
    public ResponseEntity<Void> actualizarExploradoresCercanos(
            @PathVariable Long establecimientoId, 
            @RequestParam int cantidad) {
            
        analiticaService.actualizarExploradoresCercanos(establecimientoId, cantidad);
        return ResponseEntity.ok().build();
    }
    
    // Endpoint para que el Aliado vea sus estadísticas (Panel de Control)
    @GetMapping("/{establecimientoId}/historial")
    public ResponseEntity<List<AnaliticaLocalDTO>> obtenerHistorial(@PathVariable Long establecimientoId) {
        List<AnaliticaLocalDTO> estadisticas = analiticaService.obtenerHistorialEstablecimiento(establecimientoId);
        return ResponseEntity.ok(estadisticas);
    }
}