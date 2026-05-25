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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuscripcionService {

    @Autowired
    private PlanSuscripcionRepository planRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    // Devuelve DTOs para que el Frontend dibuje la tabla de precios
    public List<PlanSuscripcionDTO> obtenerPlanesActivos() {
        return planRepository.findAll().stream()
                .filter(PlanSuscripcion::getActivo)
                .map(this::mapearAPlanDTO)
                .collect(Collectors.toList());
    }

    // Procesa la compra usando DTOs
    @Transactional
    public TransaccionResponse comprarPlan(Long usuarioId, CompraPlanRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
                
        PlanSuscripcion plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

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

        // 2. Actualizar Usuario (Sube a PRO por 30 días)
        usuario.setPlan(plan);
        usuario.setFechaFinSuscripcion(LocalDateTime.now().plusDays(30));
        usuarioRepository.save(usuario);

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