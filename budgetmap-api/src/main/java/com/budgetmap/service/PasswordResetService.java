package com.budgetmap.service;

import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.PasswordResetToken;
import com.budgetmap.model.Usuario;
import com.budgetmap.repository.PasswordResetTokenRepository;
import com.budgetmap.repository.UsuarioRepository;
import com.budgetmap.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${budgetmap.frontend.url}")
    private String frontendUrl;

    @Transactional
    public void solicitarRecuperacionPassword(String email) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);
        
        if (optionalUsuario.isEmpty()) {
            log.warn("Solicitud de recuperación para email no registrado: {}", email);
            return; // No revelar si el email existe o no por seguridad
        }

        Usuario usuario = optionalUsuario.get();

        // Eliminar token anterior si existe y hacer flush para evitar error de constraint (Insert before Delete)
        tokenRepository.deleteByUsuario(usuario);
        tokenRepository.flush();

        // Crear nuevo token (válido por 30 minutos)
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .usuario(usuario)
                .fechaExpiracion(LocalDateTime.now().plusMinutes(30))
                .build();

        tokenRepository.save(resetToken);

        // Enviar correo
        String link = frontendUrl + "/reset-password.html?token=" + token;
        String subject = "BudgetMap - Recuperación de Contraseña";
        String text = "Hola " + usuario.getNombre() + ",\n\n" +
                "Has solicitado restablecer tu contraseña. Haz clic en el siguiente enlace para crear una nueva:\n\n" +
                link + "\n\n" +
                "Este enlace expirará en 30 minutos.\n" +
                "Si no has sido tú, ignora este mensaje.\n\n" +
                "El equipo de BudgetMap.";

        emailService.enviarCorreo(usuario.getEmail(), subject, text);
        log.info("Correo de recuperación enviado a {}", email);
    }

    @Transactional
    public void resetPassword(String tokenStr, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new ResourceNotFoundException("El token es inválido o no existe"));

        if (resetToken.isExpirado()) {
            tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("El token ha expirado");
        }

        PasswordValidator.validar(newPassword);

        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        tokenRepository.delete(resetToken);
        log.info("Contraseña restablecida correctamente para el usuario {}", usuario.getEmail());
    }
}
