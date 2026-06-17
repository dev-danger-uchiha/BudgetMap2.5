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

        String link = frontendUrl + "/reset-password.html?token=" + token;
        String subject = "BudgetMap - Solicitud de Cambio de Contraseña";
        String logoUrl = frontendUrl + "/images/pwa-icon.png";
        String grassBg = "https://images.unsplash.com/photo-1533460004989-cef01064af7e?w=800&q=80"; // Imagen de pasto claro
        
        String htmlContent = "<div style=\"font-family: 'Helvetica Neue', Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #dcfce7; background-image: url('" + grassBg + "'); background-size: cover; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.1); border: 1px solid #bbf7d0;\">" +
                "<div style=\"background: rgba(255, 255, 255, 0.85); padding: 30px; text-align: center; border-bottom: 2px solid #fdba74;\">" +
                "<img src=\"" + logoUrl + "\" alt=\"BudgetMap Icon\" width=\"70\" style=\"display: block; margin: 0 auto 10px auto; drop-shadow: 0 4px 6px rgba(0,0,0,0.1);\">" +
                "<h1 style=\"margin: 0; color: #166534; font-size: 28px; font-weight: 900; letter-spacing: 1px;\">Budget<span style=\"color: #f97316;\">Map</span></h1>" +
                "</div>" +
                "<div style=\"padding: 40px 30px; background: rgba(255, 255, 255, 0.95);\">" +
                "<h2 style=\"margin-top: 0; color: #14532d; font-size: 24px; font-weight: bold;\">¡Hola " + usuario.getNombre() + "!</h2>" +
                "<p style=\"color: #3f3f46; font-size: 16px; line-height: 1.6; margin-bottom: 30px;\">Hemos recibido una solicitud desde nuestros radares para restablecer la contraseña de tu cuenta. Si fuiste tú, haz clic en el botón de abajo para configurarla:</p>" +
                "<div style=\"text-align: center; margin: 40px 0;\">" +
                "<a href=\"" + link + "\" style=\"background: linear-gradient(90deg, #fdba74, #fb923c); color: #ffffff; text-decoration: none; padding: 16px 36px; border-radius: 50px; font-weight: bold; font-size: 16px; display: inline-block; box-shadow: 0 4px 15px rgba(251, 146, 60, 0.4); text-transform: uppercase; letter-spacing: 1px; border: 1px solid #f97316;\">Crear Nueva Contraseña</a>" +
                "</div>" +
                "<p style=\"color: #71717a; font-size: 14px; text-align: center; margin-top: 30px;\">Por tu seguridad, este enlace se autodestruirá en <strong>30 minutos</strong>.</p>" +
                "<p style=\"color: #a1a1aa; font-size: 13px; text-align: center; margin-top: 10px;\">Si tú no solicitaste este cambio, simplemente ignora este correo. Tu cuenta está segura.</p>" +
                "</div>" +
                "<div style=\"background-color: rgba(220, 252, 231, 0.95); padding: 20px; text-align: center; border-top: 1px solid #bbf7d0;\">" +
                "<p style=\"margin: 0; color: #166534; font-size: 12px; font-weight: bold;\">&copy; 2026 BudgetMap. El mapa de tus finanzas.</p>" +
                "</div>" +
                "</div>";

        emailService.enviarCorreo(usuario.getEmail(), subject, htmlContent);
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
