package com.example.budgetmap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.budgetmap.model.Reserva;
import com.example.budgetmap.service.ReservaService;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reservas", reservaService.findAll());
        return "reserva/list";
    }

    @GetMapping("/crear")
    public String crearForm(Model model) {
        model.addAttribute("reserva", new Reserva());
        return "form/reserva_form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Reserva reserva) {
        reservaService.save(reserva);
        return "redirect:/reservas";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id) {
        reservaService.cancelById(id);
        return "redirect:/reservas";
    }
}
