package com.budgetmap.mapper;

import com.budgetmap.dto.PromocionResponse;
import com.budgetmap.model.Promocion;
import org.springframework.stereotype.Component;

@Component
public class PromocionMapper {

    public PromocionResponse toResponse(Promocion promo) {
        return PromocionResponse.builder()
                .id(promo.getId())
                .titulo(promo.getTitulo())
                .descripcion(promo.getDescripcion())
                .descuentoPorcentaje(promo.getDescuentoPorcentaje())
                .descuentoValor(promo.getDescuentoValor())
                .precioEspecial(promo.getPrecioEspecial())
                .fechaInicio(promo.getFechaInicio())
                .fechaFin(promo.getFechaFin())
                .codigoCupon(promo.getCodigoCupon())
                .usosMaximos(promo.getUsosMaximos())
                .usosActuales(promo.getUsosActuales())
                .imagenUrl(promo.getImagenUrl())
                .activo(promo.getActivo())
                .createdAt(promo.getCreatedAt())
                .establecimientoId(promo.getEstablecimiento() != null ? promo.getEstablecimiento().getId() : null)
                .establecimientoNombre(
                        promo.getEstablecimiento() != null ? promo.getEstablecimiento().getNombre() : null)
                .eventoId(promo.getEvento() != null ? promo.getEvento().getId() : null)
                .eventoNombre(promo.getEvento() != null ? promo.getEvento().getNombre() : null)
                .build();
    }
}
