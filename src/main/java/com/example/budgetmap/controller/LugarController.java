package com.example.budgetmap.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.budgetmap.model.Lugar;
import com.example.budgetmap.service.LugarService;

@Controller
@RequestMapping("/lugares")
public class LugarController {

    private final LugarService lugarService;

    public LugarController(LugarService lugarService) {
        this.lugarService = lugarService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lugares", lugarService.findAll());
        return "lugar/list";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("lugar", new Lugar());
        return "form/lugar_form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Lugar lugar) {
        lugarService.save(lugar);
        return "redirect:/lugares";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        lugarService.findById(id).ifPresent(l -> model.addAttribute("lugar", l));
        return "lugar/view";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        lugarService.deleteById(id);
        return "redirect:/lugares";
    }
}
