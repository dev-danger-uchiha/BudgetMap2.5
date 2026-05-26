package com.budgetmap.service;

import com.budgetmap.dto.CompraPlanRequest;
import com.budgetmap.dto.PlanSuscripcionDTO;
import com.budgetmap.dto.TransaccionResponse;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.PlanSuscripcion;
import com.budgetmap.model.Transaccion;
import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.EstadoTransaccion;
import com.budgetmap.model.enums.TipoTransaccion;
import com.budgetmap.repository.PlanSuscripcionRepository;
import com.budgetmap.repository.TransaccionRepository;
import com.budgetmap.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SuscripcionService {

    @Autowired
    private PlanSuscripcionRepository planRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    public List<PlanSuscripcionDTO> obtenerPlanesActivos() {
        log.debug("Consultando catálogo de planes de suscripción activos");
        return planRepository.findAll().stream()
                .filter(PlanSuscripcion::getActivo)
                .map(this::mapearAPlanDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransaccionResponse comprarPlan(Long usuarioId, CompraPlanRequest request) {
        log.info("Iniciando proceso de suscripción. Usuario ID: {} -> Plan ID: {}", usuarioId, request.getPlanId());
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    log.error("Usuario ID {} no encontrado al intentar comprar plan", usuarioId);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });
                
        PlanSuscripcion plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> {
                    log.error("Plan ID {} no encontrado", request.getPlanId());
                    return new ResourceNotFoundException("Plan no encontrado");
                });

        // 1. Guardar Transacción
        Transaccion transaccion = Transaccion.builder()
                .usuario(usuario)
                .tipo(TipoTransaccion.COMPRA_PLAN)
                .monto(plan.getPrecioMensual())
                .metodoPago(request.getMetodoPago())
                .referenciaPago(request.getReferenciaPago())
                .estado(EstadoTransaccion.EXITOSO)
                .build();
        transaccion = transaccionRepository.save(transaccion);
        log.debug("Transacción de compra registrada con ID: {}", transaccion.getId());

        // 2. Actualizar Usuario (Sube a PRO por 30 días)
        usuario.setPlan(plan);
        usuario.setFechaFinSuscripcion(LocalDateTime.now().plusDays(30));
        usuarioRepository.save(usuario);
        
        log.info("Suscripción exitosa. Usuario ID: {} es ahora PRO hasta {}", usuarioId, usuario.getFechaFinSuscripcion());

        return mapearATransaccionResponse(transaccion);
    }

    // --- MAPPERS INTERNOS ---
    private PlanSuscripcionDTO mapearAPlanDTO(PlanSuscripcion plan) {
        return PlanSuscripcionDTO.builder()
                .id(plan.getId())
                .nombre(plan.getNombre())
                .tipoPublico(plan.getTipoPublico())
                .precioMensual(plan.getPrecioMensual())
                .permitePromosIlimitadas(plan.getPermitePromosIlimitadas())
                .permiteEstadisticasAvanzadas(plan.getPermiteEstadisticasAvanzadas())
                .accesoAnticipadoOfertas(plan.getAccesoAnticipadoOfertas())
                .sinAnuncios(plan.getSinAnuncios())
                .build();
    }

    private TransaccionResponse mapearATransaccionResponse(Transaccion tx) {
        return TransaccionResponse.builder()
                .id(tx.getId())
                .tipo(tx.getTipo())
                .monto(tx.getMonto())
                .metodoPago(tx.getMetodoPago())
                .referenciaPago(tx.getReferenciaPago())
                .estado(tx.getEstado())
                .fechaTransaccion(tx.getFechaTransaccion())
                .build();
    }
}