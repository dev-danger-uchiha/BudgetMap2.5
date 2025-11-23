package com.example.budgetmap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.budgetmap.model.Establecimiento;
import com.example.budgetmap.service.EstablecimientoService;

@Controller
@RequestMapping("/establecimientos")
public class EstablecimientoController {

    private final EstablecimientoService establecimientoService;

    public EstablecimientoController(EstablecimientoService establecimientoService) {
        this.establecimientoService = establecimientoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("establecimientos", establecimientoService.findAll());
        return "establecimiento/list";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("establecimiento", new Establecimiento());
        return "form/establecimiento";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Establecimiento establecimiento) {
        establecimientoService.save(establecimiento);
        return "redirect:/establecimientos";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        establecimientoService.findById(id).ifPresent(e -> model.addAttribute("establecimiento", e));
        return "establecimiento/view";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        establecimientoService.deleteById(id);
        return "redirect:/establecimientos";
    }
}
