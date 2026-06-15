package com.budgetmap.mapper;

import com.budgetmap.dto.EstablecimientoResponse;
import com.budgetmap.model.Establecimiento;
import org.springframework.stereotype.Component;

@Component
public class EstablecimientoMapper {

    public EstablecimientoResponse toResponse(Establecimiento est) {
        return EstablecimientoResponse.builder()
                .id(est.getId())
                .nombre(est.getNombre())
                .nit(est.getNit())
                .descripcion(est.getDescripcion())
                .categoria(est.getCategoria())
                .direccion(est.getDireccion())
                .latitud(est.getLatitud())
                .longitud(est.getLongitud())
                .imagenUrl(est.getImagenUrl())
                .rutPdfUrl(est.getRutPdfUrl())
                .aforoMaximo(est.getAforoMaximo())
                .aforoActual(est.getAforoActual())
                .telefono(est.getTelefono())
                .horarioAtencion(est.getHorarioAtencion())
                .estado(est.getEstado())
                .motivoRechazo(est.getMotivoRechazo())
                .activo(est.getActivo())
                .destacado(est.getDestacado())
                .verificado(est.getVerificado())
                .reservasHabilitadas(est.getReservasHabilitadas())
                .createdAt(est.getCreatedAt())
                .propietarioId(est.getPropietario().getId())
                .propietarioNombre(est.getPropietario().getNombre())
                .build();
    }
}
