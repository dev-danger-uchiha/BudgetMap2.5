package com.example.budgetmap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.budgetmap.model.Usuario;
import com.example.budgetmap.service.UsuarioService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "form/cliente";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute("usuario") Usuario usuario, BindingResult br, Model model) {
        if (br.hasErrors()) {
            return "form/cliente";
        }
        if (usuarioService.existsByUserName(usuario.getUserName())) {
            model.addAttribute("error", "Nombre de usuario no disponible");
            return "form/cliente";
        }
        usuarioService.createCliente(usuario);
        return "redirect:/login?registered";
    }
}
