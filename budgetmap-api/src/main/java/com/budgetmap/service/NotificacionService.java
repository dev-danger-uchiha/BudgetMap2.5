package com.budgetmap.service;

import lombok.RequiredArgsConstructor;

import com.budgetmap.dto.NotificacionResponse;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Notificacion;
import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.TipoNotificacion;
import com.budgetmap.repository.NotificacionRepository;
import com.budgetmap.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final com.budgetmap.repository.ConfigAlertaRepository configAlertaRepository;

    public List<NotificacionResponse> listarPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId, Pageable.unpaged())
                .getContent().stream().map(this::convertirAResponse).collect(Collectors.toList());
    }

    public Page<NotificacionResponse> listarPorUsuarioPaginado(Long usuarioId, Pageable pageable) {
        return notificacionRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId, pageable)
                .map(this::convertirAResponse);
    }

    public List<NotificacionResponse> listarNoLeidas(Long usuarioId) {
        return notificacionRepository.findNoLeidasByUsuario(usuarioId).stream()
                .map(this::convertirAResponse).collect(Collectors.toList());
    }

    public Long contarNoLeidas(Long usuarioId) {
        return notificacionRepository.countNoLeidasByUsuario(usuarioId);
    }

    @Transactional
    public Notificacion crear(TipoNotificacion tipo, String titulo, String mensaje,
            Long usuarioId, Long referenciaId, String referenciaTipo) {
        log.info("Creando notificación tipo {} para el usuario ID: {}", tipo, usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    log.error("Fallo al crear notificación. Usuario ID {} no encontrado.", usuarioId);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });

        com.budgetmap.model.ConfigAlerta config = configAlertaRepository.findByUsuarioId(usuarioId).orElse(null);
        if (config != null) {
            if (tipo == TipoNotificacion.PROMOCION_NUEVA && Boolean.FALSE.equals(config.getNotificarPromociones())) {
                log.info("Notificación abortada: El usuario ID {} tiene deshabilitadas las promociones.", usuarioId);
                return null;
            }
            if (tipo == TipoNotificacion.EVENTO_RECORDATORIO && Boolean.FALSE.equals(config.getNotificarEventos())) {
                log.info("Notificación abortada: El usuario ID {} tiene deshabilitados los eventos.", usuarioId);
                return null;
            }
        }

        Notificacion notificacion = Notificacion.builder()
                .usuario(usuario)
                .tipo(tipo)
                .titulo(titulo)
                .mensaje(mensaje)
                .referenciaId(referenciaId)
                .referenciaTipo(referenciaTipo)
                .leida(false)
                .origen("SPRING")
                .build();

        Notificacion guardada = notificacionRepository.save(notificacion);
        log.debug("Notificación ID: {} guardada correctamente", guardada.getId());
        return guardada;
    }

    @Transactional
    public void marcarComoLeida(Long id, Long usuarioId) {
        log.debug("Marcando notificación ID: {} como leída para el usuario ID: {}", id, usuarioId);
        notificacionRepository.marcarComoLeida(id, usuarioId);
    }

    @Transactional
    public void marcarTodasComoLeidas(Long usuarioId) {
        log.info("Marcando todas las notificaciones como leídas para el usuario ID: {}", usuarioId);
        notificacionRepository.marcarTodasComoLeidas(usuarioId);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando notificación ID: {}", id);
        notificacionRepository.deleteById(id);
    }

    private NotificacionResponse convertirAResponse(Notificacion notificacion) {
        return NotificacionResponse.builder()
                .id(notificacion.getId())
                .tipo(notificacion.getTipo())
                .titulo(notificacion.getTitulo())
                .mensaje(notificacion.getMensaje())
                .referenciaId(notificacion.getReferenciaId())
                .referenciaTipo(notificacion.getReferenciaTipo())
                .leida(notificacion.getLeida())
                .fechaLectura(notificacion.getFechaLectura())
                .accionUrl(notificacion.getAccionUrl())
                .imagenUrl(notificacion.getImagenUrl())
                .origen(notificacion.getOrigen())
                .createdAt(notificacion.getCreatedAt())
                .build();
    }

    @Scheduled(cron = "0 0 2 * * ?") // Todos los días a las 2 AM
    @Transactional
    public void limpiarNotificacionesAntiguas() {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        log.info("Ejecutando limpieza de notificaciones leídas anteriores a {}", hace30Dias);
        notificacionRepository.deleteByLeidaTrueAndCreatedAtBefore(hace30Dias);
    }
}