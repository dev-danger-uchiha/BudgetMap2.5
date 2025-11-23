package com.example.budgetmap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.budgetmap.model.Pqrs;
import com.example.budgetmap.service.PqrsService;

@Controller
@RequestMapping("/pqrs")
public class PqrsController {

    private final PqrsService pqrsService;

    public PqrsController(PqrsService pqrsService) {
        this.pqrsService = pqrsService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pqrsList", pqrsService.findAll());
        return "pqrs/list";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("pqrs", new Pqrs());
        return "form/pqrs_form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Pqrs pqrs) {
        pqrsService.save(pqrs);
        return "redirect:/pqrs";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        pqrsService.findById(id).ifPresent(p -> model.addAttribute("pqrs", p));
        return "pqrs/view";
    }

    @PostMapping("/asignar/{id}")
    // @PreAuthorize("hasAuthority('ROL_MODERADOR') or hasAuthority('ROL_ADMIN')")
    public String asignar(@PathVariable Long id, @RequestParam Long usuarioId) {
        pqrsService.assignToUser(id, usuarioId);
        return "redirect:/pqrs";
    }
}
