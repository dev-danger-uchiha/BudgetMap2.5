package com.budgetmap.controller;

import com.budgetmap.model.*;
import com.budgetmap.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/destacados")
public class DestacadosController {

    @Autowired private EstablecimientoRepository estRepo;
    @Autowired private EventoRepository evtRepo;
    @Autowired private LugarRepository lugRepo;

    @GetMapping("/establecimientos")
    public List<Establecimiento> getEstablecimientos() {
        return estRepo.findDestacados(); // Debes agregar este método al Repo
    }

    @GetMapping("/eventos")
    public List<Evento> getEventos() {
        return evtRepo.findDestacados(LocalDate.now());
    }

    @GetMapping("/lugares")
    public List<Lugar> getLugares() {
        return lugRepo.findDestacados(); // Debes agregar este método al Repo
    }
}