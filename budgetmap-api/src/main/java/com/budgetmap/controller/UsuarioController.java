package com.budgetmap.controller;

import com.budgetmap.dto.UsuarioDTO;
import com.budgetmap.model.enums.RolUsuario;
import com.budgetmap.security.UserDetailsImpl;
import com.budgetmap.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/usuarios/paginado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Page<UsuarioDTO>> listarPaginado(
            Pageable pageable,
            @RequestParam(required = false) String criterio,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) Boolean activo) {
        return ResponseEntity.ok(usuarioService.listarPaginadoConFiltros(pageable, criterio, rol, activo));
    }

    @GetMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping("/usuarios/rol/{rol}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioDTO>> listarPorRol(@PathVariable RolUsuario rol) {
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> obtenerPerfil(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(userDetails.getId()));
    }

    @PutMapping("/perfil/avatar")
    public ResponseEntity<Void> actualizarAvatar(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, String> body) {
        String avatarUrl = body.get("avatarUrl");
        usuarioService.actualizarAvatar(userDetails.getId(), avatarUrl);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/perfil/actualizar")
    public ResponseEntity<Void> actualizarPerfil(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, String> body) {
        usuarioService.actualizarPerfil(
            userDetails.getId(),
            body.get("nombre"),
            body.get("apellido"),
            body.get("telefono"),
            body.get("password")
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/usuarios/buscar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuarios(@RequestParam String criterio) {
        return ResponseEntity.ok(usuarioService.buscarPorNombreOEmail(criterio));
    }

    @GetMapping("/usuarios/leaderboard")
    public ResponseEntity<List<UsuarioDTO>> obtenerLeaderboard(@RequestParam(defaultValue = "5") int top) {
        return ResponseEntity.ok(usuarioService.obtenerLeaderboard(top));
    }

    @PostMapping("/usuarios/crear")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioDTO> crearUsuario(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetailsImpl adminActual) {
        return ResponseEntity.ok(usuarioService.crearUsuario(
                body.get("nombre"),
                body.get("apellido"),
                body.get("email"),
                body.get("password"),
                body.get("rol")
        ));
    }

    @PutMapping("/usuarios/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo,
            @AuthenticationPrincipal UserDetailsImpl adminActual) {
        usuarioService.cambiarEstadoConValidacion(id, activo, adminActual.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/usuarios/{id}/rol")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> cambiarRol(@PathVariable Long id, @RequestBody Map<String, String> body) {
        RolUsuario nuevoRol = RolUsuario.valueOf(body.get("rol"));
        usuarioService.cambiarRol(id, nuevoRol);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/estadisticas/usuarios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Long>> estadisticas() {
        return ResponseEntity.ok(Map.of(
                "total", usuarioService.contarPorRol(RolUsuario.ADMINISTRADOR) +
                        usuarioService.contarPorRol(RolUsuario.MODERADOR) +
                        usuarioService.contarPorRol(RolUsuario.LOCAL_ALIADO) +
                        usuarioService.contarPorRol(RolUsuario.ANFITRION) +
                        usuarioService.contarPorRol(RolUsuario.EXPLORADOR) +
                        usuarioService.contarPorRol(RolUsuario.EXPLORADOR_PRO),
                "administradores", usuarioService.contarPorRol(RolUsuario.ADMINISTRADOR),
                "moderadores", usuarioService.contarPorRol(RolUsuario.MODERADOR),
                "localesAliados", usuarioService.contarPorRol(RolUsuario.LOCAL_ALIADO),
                "anfitriones", usuarioService.contarPorRol(RolUsuario.ANFITRION),
                "exploradores", usuarioService.contarPorRol(RolUsuario.EXPLORADOR) + usuarioService.contarPorRol(RolUsuario.EXPLORADOR_PRO)));
    }
}
