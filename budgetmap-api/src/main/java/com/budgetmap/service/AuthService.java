package com.budgetmap.service;

import com.budgetmap.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;

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
import com.budgetmap.util.PasswordValidator;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final com.budgetmap.repository.TokenRevocadoRepository tokenRevocadoRepository;
    private final UsuarioMapper usuarioMapper;

    public LoginResponse autenticar(LoginRequest loginRequest) {
        log.info("Iniciando proceso de autenticación para el email: {}", loginRequest.getEmail());
        
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (usuario != null && Boolean.TRUE.equals(usuario.getCuentaBloqueada())) {
            if (usuario.getFechaDesbloqueo() != null && java.time.LocalDateTime.now().isBefore(usuario.getFechaDesbloqueo())) {
                long minutosRestantes = java.time.Duration.between(java.time.LocalDateTime.now(), usuario.getFechaDesbloqueo()).toMinutes();
                throw new CuentaBloqueadaException("La cuenta está bloqueada por demasiados intentos fallidos.", Math.max(1, minutosRestantes));
            } else {
                usuario.setCuentaBloqueada(false);
                usuario.setIntentosFallidos(0);
                usuario.setFechaDesbloqueo(null);
                usuarioRepository.save(usuario);
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            if (usuario != null) {
                usuario.setIntentosFallidos(0);
                usuario.setCuentaBloqueada(false);
                usuario.setFechaDesbloqueo(null);
                usuarioRepository.save(usuario);
            }

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
            log.warn("Intento de autenticación fallido para: {}", loginRequest.getEmail());
            
            if (usuario != null) {
                int intentos = (usuario.getIntentosFallidos() == null ? 0 : usuario.getIntentosFallidos()) + 1;
                usuario.setIntentosFallidos(intentos);
                
                if (intentos >= 5) {
                    usuario.setCuentaBloqueada(true);
                    usuario.setFechaDesbloqueo(java.time.LocalDateTime.now().plusMinutes(15));
                    usuarioRepository.save(usuario);
                    log.warn("Cuenta bloqueada por superar intentos fallidos: {}", usuario.getEmail());
                    throw new CuentaBloqueadaException("Cuenta bloqueada por superar el máximo de intentos fallidos.", 15);
                } else {
                    usuarioRepository.save(usuario);
                }
            }
            
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }
    }

    @Transactional
    public UsuarioDTO registrar(RegistroRequest request) {
        String emailNormalizado = normalizarEmail(request.getEmail());
        String nombreNormalizado = normalizarNombre(request.getNombre());

        log.info("Iniciando registro de nuevo usuario con email: {}", emailNormalizado);

        PasswordValidator.validar(request.getPassword());

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

            log.info("Usuario registrado exitosamente: {}, rol: {}", guardado.getEmail(), guardado.getRol());

            return usuarioMapper.toDto(guardado);

        } catch (DataIntegrityViolationException e) {
            log.warn("Intento de registro con email duplicado: {}", emailNormalizado);
            throw new EmailYaRegistradoException("El email ya está registrado en el sistema");

        } catch (Exception e) {
            log.error("Error inesperado al registrar usuario: {}", e.getMessage(), e);
            throw new RegistroException("Error al procesar el registro. Intente nuevamente.");
        }
    }

    @Transactional
    public UsuarioDTO registrarExplorador(RegistroRequest request) {
        log.debug("Redirigiendo registro a flujo de EXPLORADOR por defecto para: {}", request.getEmail());
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
            throw new RegistroException("El email es obligatorio");
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


    @Transactional
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token != null && jwtUtils.validateJwtToken(token)) {
            java.util.Date expiration = jwtUtils.getExpirationDateFromToken(token);
            java.time.LocalDateTime expirationLdt = java.time.LocalDateTime.ofInstant(
                    expiration.toInstant(), java.time.ZoneId.systemDefault());
                    
            com.budgetmap.model.TokenRevocado tokenRevocado = com.budgetmap.model.TokenRevocado.builder()
                    .token(token)
                    .fechaExpiracion(expirationLdt)
                    .build();
                    
            tokenRevocadoRepository.save(tokenRevocado);
            log.info("Token añadido a la blacklist (logout exitoso).");
        }
    }

    @Scheduled(cron = "0 0 3 * * ?") // Todos los días a las 3 AM
    @Transactional
    public void limpiarTokensRevocadosExpirados() {
        log.info("Ejecutando limpieza de tokens revocados expirados en la base de datos.");
        tokenRevocadoRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}