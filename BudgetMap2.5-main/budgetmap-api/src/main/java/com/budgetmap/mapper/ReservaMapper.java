package com.budgetmap.mapper;

import com.budgetmap.dto.ReservaResponse;
import com.budgetmap.model.Reserva;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapper {

    public ReservaResponse toResponse(Reserva reserva) {
        String nombreEstablecimiento = null;
        String nombreEvento = null;
        String tipoReserva;

        if (reserva.getEvento() != null) {
            nombreEvento = reserva.getEvento().getNombre();
            tipoReserva = "EVENTO";
            if (reserva.getEstablecimiento() != null) {
                nombreEstablecimiento = reserva.getEstablecimiento().getNombre();
            }
        } else if (reserva.getEstablecimiento() != null) {
            nombreEstablecimiento = reserva.getEstablecimiento().getNombre();
            tipoReserva = "ESTABLECIMIENTO";
        } else {
            tipoReserva = "DESCONOCIDO";
        }

        return ReservaResponse.builder()
                .id(reserva.getId())
                .codigoReserva(reserva.getCodigoReserva())
                .nombreEstablecimiento(nombreEstablecimiento)
                .nombreEvento(nombreEvento)
                .tipoReserva(tipoReserva)
                .fechaReserva(reserva.getFechaReserva())
                .horaInicio(reserva.getHoraInicio())
                .horaFin(reserva.getHoraFin())
                .numeroPersonas(reserva.getNumeroPersonas())
                .estado(reserva.getEstado())
                .puntosOtorgados(reserva.getPuntosOtorgados())
                .comisionCobrada(reserva.getComisionCobrada())
                .createdAt(reserva.getCreatedAt())
                .build();
    }
}
