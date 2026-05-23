package com.budgetmap.service;

import com.budgetmap.dto.NotificacionResponse;
import com.budgetmap.model.Notificacion;
import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.TipoNotificacion;
import com.budgetmap.repository.NotificacionRepository;
import com.budgetmap.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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

        return notificacionRepository.save(notificacion);
    }

    @Transactional
    public void marcarComoLeida(Long id, Long usuarioId) {
        notificacionRepository.marcarComoLeida(id, usuarioId);
    }

    @Transactional
    public void marcarTodasComoLeidas(Long usuarioId) {
        notificacionRepository.marcarTodasComoLeidas(usuarioId);
    }

    @Transactional
    public void eliminar(Long id) {
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
}