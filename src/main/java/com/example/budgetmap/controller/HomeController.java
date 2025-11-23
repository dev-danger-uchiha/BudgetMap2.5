package com.example.budgetmap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.budgetmap.service.EstablecimientoService;
import com.example.budgetmap.service.EventoService;
import com.example.budgetmap.service.LugarService;

@Controller
public class HomeController {

    private final EstablecimientoService establecimientoService;
    private final EventoService eventoService;
    private final LugarService lugarService;

    public HomeController(EstablecimientoService establecimientoService,
            EventoService eventoService,
            LugarService lugarService) {
        this.establecimientoService = establecimientoService;
        this.eventoService = eventoService;
        this.lugarService = lugarService;
    }

    @GetMapping({ "/", "/home" })
    public String home(Model model) {
        model.addAttribute("establecimientos", establecimientoService.findTop10());
        model.addAttribute("eventosProximos", eventoService.findNextEvents(10));
        model.addAttribute("lugaresDestacados", lugarService.findFeatured(10));
        return "home";
    }
}
