package com.budgetmap.controller;

import com.budgetmap.dto.DashboardStatsResponse;
import com.budgetmap.service.EstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/metrics")
@CrossOrigin(origins = "*")
public class EstadisticasController {

    @Autowired
    private EstadisticasService estadisticasService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<DashboardStatsResponse> getSummary() {
        return ResponseEntity.ok(estadisticasService.obtenerResumenGeneral());
    }
}