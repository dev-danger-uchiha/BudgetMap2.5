package com.budgetmap.service;

import com.budgetmap.dto.LoginRequest;
import com.budgetmap.dto.LoginResponse;
import com.budgetmap.dto.RegistroRequest;
import com.budgetmap.dto.UsuarioDTO;
import com.budgetmap.exception.*;
import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.RolUsuario;
import com.budgetmap.repository.UsuarioRepository;
import com.budgetmap.security.JwtUtils;
import com.budgetmap.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public LoginResponse autenticar(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            log.info("Usuario autenticado exitosamente: {}", userDetails.getUsername());

            return LoginResponse.builder()
                    .token(jwt)
                    .tipo("Bearer")
                    .id(userDetails.getId())
                    .nombre(userDetails.getNombre())
                    .email(userDetails.getUsername())
                    .rol(userDetails.getRol())
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Intento de autenticacion fallido para: {}", loginRequest.getEmail());
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }
    }

    @Transactional
    public UsuarioDTO registrar(RegistroRequest request) {
        String emailNormalizado = normalizarEmail(request.getEmail());
        String nombreNormalizado = normalizarNombre(request.getNombre());

        validarPassword(request.getPassword());

        RolUsuario rolFinal = request.getRol() != null ? request.getRol() : RolUsuario.EXPLORADOR;

        Usuario usuario = Usuario.builder()
                .nombre(nombreNormalizado)
                .apellido(normalizarNombre(request.getApellido()))
                .email(emailNormalizado)
                .password(encoder.encode(request.getPassword()))
                .telefono(normalizarTelefono(request.getTelefono()))
                .rol(rolFinal)
                .puntosAcumulados(0)
                .activo(true)
                .emailVerificado(false)
                .build();

        try {
            Usuario guardado = usuarioRepository.save(usuario);
            usuarioRepository.flush();

            log.info("Usuario registrado exitosamente: {}, rol: {}",
                    guardado.getEmail(), guardado.getRol());

            return convertirADTO(guardado);

        } catch (DataIntegrityViolationException e) {
            log.warn("Intento de registro con email duplicado: {}", emailNormalizado);
            throw new EmailYaRegistradoException("El email ya esta registrado en el sistema");

        } catch (Exception e) {
            log.error("Error inesperado al registrar usuario: {}", e.getMessage(), e);
            throw new RegistroException("Error al procesar el registro. Intente nuevamente.");
        }
    }

    @Transactional
    public UsuarioDTO registrarExplorador(RegistroRequest request) {
        RegistroRequest requestCopia = RegistroRequest.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(request.getPassword())
                .telefono(request.getTelefono())
                .rol(RolUsuario.EXPLORADOR)
                .build();

        return registrar(requestCopia);
    }

    private String normalizarEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        return email.trim().toLowerCase();
    }

    private String normalizarNombre(String nombre) {
        if (nombre == null) {
            return null;
        }
        return nombre.trim();
    }

    private String normalizarTelefono(String telefono) {
        if (telefono == null) {
            return null;
        }
        return telefono.trim().replaceAll("[^0-9+]", "");
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new PasswordInvalidoException("La contraseña debe tener al menos 8 caracteres");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new PasswordInvalidoException("La contraseña debe contener al menos una mayuscula");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new PasswordInvalidoException("La contraseña debe contener al menos una minuscula");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new PasswordInvalidoException("La contraseña debe contener al menos un numero");
        }
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .rol(usuario.getRol())
                .puntosAcumulados(usuario.getPuntosAcumulados() != null ? usuario.getPuntosAcumulados() : 0)
                .activo(usuario.getActivo() != null ? usuario.getActivo() : true)
                .ultimoAcceso(usuario.getUltimoAcceso())
                .createdAt(usuario.getCreatedAt())
                .build();
    }
}