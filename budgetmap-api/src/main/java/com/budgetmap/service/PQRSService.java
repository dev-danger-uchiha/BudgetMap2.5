package com.budgetmap.service;

import com.budgetmap.dto.PQRSRequest;
import com.budgetmap.dto.PQRSResponse;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.PQRS;
import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.EstadoPQRS;
import com.budgetmap.repository.PQRSRepository;
import com.budgetmap.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PQRSService {

    @Autowired
    private PQRSRepository pqrsRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<PQRSResponse> listarTodos() {
        return pqrsRepository.findAll().stream().map(this::convertirAResponse).collect(Collectors.toList());
    }

    public List<PQRSResponse> listarPorUsuario(Long usuarioId) {
        return pqrsRepository.findByUsuarioId(usuarioId).stream().map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public Page<PQRSResponse> listarPorUsuarioPaginado(Long usuarioId, Pageable pageable) {
        return pqrsRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId, pageable).map(this::convertirAResponse);
    }

    public List<PQRSResponse> listarPendientesRespuesta() {
        return pqrsRepository.findPendientesRespuesta().stream().map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<PQRSResponse> listarAsignadosAModerador(Long moderadorId) {
        return pqrsRepository.findAsignadosAModerador(moderadorId).stream().map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    private PQRS obtenerEntityPorId(Long id) {
        return pqrsRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("PQRS con ID {} no encontrado", id);
                    return new ResourceNotFoundException("PQRS no encontrado");
                });
    }

    public PQRSResponse obtenerPorId(Long id) {
        return convertirAResponse(obtenerEntityPorId(id));
    }

    @Transactional
    public PQRSResponse crear(PQRSRequest request, Long usuarioId) {
        log.info("Creando nuevo ticket PQRS para usuario ID: {}", usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        PQRS pqrs = PQRS.builder()
                .codigoTicket(generarCodigoTicket())
                .usuario(usuario)
                .tipo(request.getTipo())
                .asunto(request.getAsunto())
                .descripcion(request.getDescripcion())
                .adjuntos(request.getAdjuntos())
                .estado(EstadoPQRS.ABIERTO)
                .prioridad("MEDIA")
                .build();

        PQRS guardado = pqrsRepository.save(pqrs);
        log.info("Ticket PQRS creado con éxito. Código: {}", guardado.getCodigoTicket());
        return convertirAResponse(guardado);
    }

    @Transactional
    public PQRSResponse responder(Long id, String respuesta, Long moderadorId) {
        log.info("Moderador ID: {} respondiendo al ticket ID: {}", moderadorId, id);
        PQRS pqrs = obtenerEntityPorId(id);

        pqrs.setRespuesta(respuesta);
        pqrs.setEstado(EstadoPQRS.RESPONDIDO);
        pqrs.setModeradorAsignadoId(moderadorId);
        pqrs.setFechaRespuesta(LocalDateTime.now());

        return convertirAResponse(pqrsRepository.save(pqrs));
    }

    @Transactional
    public void asignarAModerador(Long id, Long moderadorId) {
        log.info("Asignando ticket ID: {} al moderador ID: {}", id, moderadorId);
        PQRS pqrs = obtenerEntityPorId(id);
        pqrs.setModeradorAsignadoId(moderadorId);
        pqrs.setEstado(EstadoPQRS.EN_PROCESO);
        pqrsRepository.save(pqrs);
    }

    @Transactional
    public void cerrar(Long id) {
        log.info("Cerrando ticket PQRS ID: {}", id);
        PQRS pqrs = obtenerEntityPorId(id);
        pqrs.setEstado(EstadoPQRS.CERRADO);
        pqrsRepository.save(pqrs);
    }

    public Page<PQRSResponse> listarPaginadosConFiltros(Pageable pageable, String estado, String prioridad, String tipo) {
        List<PQRS> pqrsList = pqrsRepository.findAll();

        if (estado != null && !estado.isBlank()) {
            try {
                EstadoPQRS estadoEnum = EstadoPQRS.valueOf(estado);
                pqrsList = pqrsList.stream()
                        .filter(p -> p.getEstado() == estadoEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                log.warn("Estado inválido: {}", estado);
            }
        }

        if (prioridad != null && !prioridad.isBlank()) {
            pqrsList = pqrsList.stream()
                    .filter(p -> p.getPrioridad() != null && p.getPrioridad().equalsIgnoreCase(prioridad))
                    .collect(Collectors.toList());
        }

        if (tipo != null && !tipo.isBlank()) {
            pqrsList = pqrsList.stream()
                    .filter(p -> p.getTipo() != null && p.getTipo().name().equalsIgnoreCase(tipo))
                    .collect(Collectors.toList());
        }

        pqrsList.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), pqrsList.size());
        List<PQRSResponse> dtos = pqrsList.subList(start, end).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, pqrsList.size());
    }

    public Long contarPorEstado(EstadoPQRS estado) {
        return pqrsRepository.countByEstado(estado);
    }

    private String generarCodigoTicket() {
        return "PQRS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PQRSResponse convertirAResponse(PQRS pqrs) {
        return PQRSResponse.builder()
                .id(pqrs.getId())
                .codigoTicket(pqrs.getCodigoTicket())
                .tipo(pqrs.getTipo())
                .asunto(pqrs.getAsunto())
                .descripcion(pqrs.getDescripcion())
                .estado(pqrs.getEstado())
                .prioridad(pqrs.getPrioridad())
                .respuesta(pqrs.getRespuesta())
                .fechaRespuesta(pqrs.getFechaRespuesta())
                .adjuntos(pqrs.getAdjuntos())
                .createdAt(pqrs.getCreatedAt())
                .usuarioId(pqrs.getUsuario().getId())
                .usuarioNombre(pqrs.getUsuario().getNombre())
                .usuarioEmail(pqrs.getUsuario().getEmail())
                .moderadorAsignadoId(pqrs.getModeradorAsignadoId())
                .build();
    }
}