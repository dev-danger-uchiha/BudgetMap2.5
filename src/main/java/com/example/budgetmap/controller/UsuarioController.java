package com.example.budgetmap.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.budgetmap.model.Usuario;
import com.example.budgetmap.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Lista usuarios (administración)
    @GetMapping
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String list(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuario";
    }

    @GetMapping("/crear")
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String crearForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "form/admin";
    }

    @PostMapping("/guardar")
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String guardar(@ModelAttribute Usuario usuario) {
        usuarioService.save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String editarForm(@PathVariable Long id, Model model) {
        Usuario u = usuarioService.findById(id).orElse(new Usuario());
        model.addAttribute("usuario", u);
        return "form/admin";
    }

    @PostMapping("/eliminar/{id}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String eliminar(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return "redirect:/usuarios";
    }

    // Endpoints REST opcionales
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Usuario> getUsuario(@PathVariable Long id) {
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
