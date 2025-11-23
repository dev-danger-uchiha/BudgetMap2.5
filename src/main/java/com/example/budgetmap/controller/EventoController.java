package com.example.budgetmap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.budgetmap.model.Evento;
import com.example.budgetmap.service.EventoService;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("eventos", eventoService.findAll());
        return "evento/list";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("evento", new Evento());
        return "form/evento_form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Evento evento) {
        eventoService.save(evento);
        return "redirect:/eventos";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        eventoService.findById(id).ifPresent(e -> model.addAttribute("evento", e));
        return "evento/view";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        eventoService.deleteById(id);
        return "redirect:/eventos";
    }
}
