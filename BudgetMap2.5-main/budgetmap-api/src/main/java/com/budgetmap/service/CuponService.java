package com.budgetmap.service;

import lombok.RequiredArgsConstructor;

import com.budgetmap.dto.CanjearCuponRequest;
import com.budgetmap.dto.CuponRedimidoDTO;
import com.budgetmap.exception.CuponException;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.CuponRedimido;
import com.budgetmap.model.Establecimiento;
import com.budgetmap.model.Usuario;
import com.budgetmap.repository.CuponRedimidoRepository;
import com.budgetmap.repository.EstablecimientoRepository;
import com.budgetmap.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuponService {

    private final PuntosService puntosService;
    private final CuponRedimidoRepository cuponRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstablecimientoRepository establecimientoRepository;

    @Transactional
    public CuponRedimidoDTO canjearCupon(Long usuarioId, CanjearCuponRequest request) {
        log.info("Iniciando canje de cupón para el usuario ID: {} en el establecimiento ID: {}", usuarioId, request.getEstablecimientoId());
        
        puntosService.restarPuntos(usuarioId, request.getCostoPuntos());

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado al intentar canjear cupón: ID {}", usuarioId);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });
        Establecimiento local = establecimientoRepository.findById(request.getEstablecimientoId())
                .orElseThrow(() -> {
                    log.error("Establecimiento no encontrado al intentar canjear cupón: ID {}", request.getEstablecimientoId());
                    return new ResourceNotFoundException("Establecimiento no encontrado");
                });

        String codigo = "BMAP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        CuponRedimido nuevoCupon = CuponRedimido.builder()
                .usuario(usuario)
                .establecimiento(local)
                .tituloDescuento(request.getTituloDescuento())
                .codigoUnico(codigo)
                .puntosGastados(request.getCostoPuntos())
                .usado(false)
                .fechaRedencion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusDays(7))
                .build();

        nuevoCupon = cuponRepository.save(nuevoCupon);
        log.info("Cupón canjeado exitosamente. Código generado: {}", codigo);

        return mapearACuponDTO(nuevoCupon);
    }

    public List<CuponRedimidoDTO> obtenerMisCupones(Long usuarioId) {
        return cuponRepository.findByUsuarioIdOrderByFechaRedencionDesc(usuarioId)
                .stream()
                .map(this::mapearACuponDTO)
                .collect(Collectors.toList());
    }

    private CuponRedimidoDTO mapearACuponDTO(CuponRedimido cupon) {
        return CuponRedimidoDTO.builder()
                .id(cupon.getId())
                .nombreEstablecimiento(cupon.getEstablecimiento().getNombre())
                .tituloDescuento(cupon.getTituloDescuento())
                .codigoUnico(cupon.getCodigoUnico())
                .puntosGastados(cupon.getPuntosGastados())
                .usado(cupon.getUsado())
                .fechaExpiracion(cupon.getFechaExpiracion())
                .build();
    }

    @Transactional
    public CuponRedimidoDTO validarYQuemarCupon(String codigo, Long propietarioId) {
        log.info("Validando cupón con código: {} por el propietario ID: {}", codigo, propietarioId);
        
        CuponRedimido cupon = cuponRepository.findByCodigoUnico(codigo)
                .orElseThrow(() -> {
                    log.warn("Intento de validar un cupón inexistente: {}", codigo);
                    return new ResourceNotFoundException("Cupón no encontrado o código inválido");
                });

        if (!cupon.getEstablecimiento().getPropietario().getId().equals(propietarioId)) {
            log.warn("Alerta de seguridad: El usuario {} intentó validar un cupón del establecimiento {}", propietarioId, cupon.getEstablecimiento().getId());
            throw new CuponException("No tienes permisos para validar cupones de otro establecimiento");
        }

        if (cupon.getUsado()) {
            log.warn("El cupón {} ya fue redimido anteriormente", codigo);
            throw new CuponException("Este cupón ya fue redimido anteriormente");
        }

        if (cupon.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            log.warn("Intento de validación de un cupón expirado: {}", codigo);
            throw new CuponException("Este cupón ya ha expirado");
        }

        cupon.setUsado(true);
        cupon = cuponRepository.save(cupon);
        log.info("Cupón {} quemado con éxito", codigo);

        return mapearACuponDTO(cupon);
    }
}