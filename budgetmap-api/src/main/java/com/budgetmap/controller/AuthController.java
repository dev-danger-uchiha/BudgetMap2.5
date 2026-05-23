package com.budgetmap.controller;

import com.budgetmap.dto.LoginRequest;
import com.budgetmap.dto.LoginResponse;
import com.budgetmap.dto.RegistroRequest;
import com.budgetmap.dto.UsuarioDTO;
import com.budgetmap.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.autenticar(request));
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registro(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/registro/explorador")
    public ResponseEntity<UsuarioDTO> registroExplorador(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.registrarExplorador(request));
    }
}