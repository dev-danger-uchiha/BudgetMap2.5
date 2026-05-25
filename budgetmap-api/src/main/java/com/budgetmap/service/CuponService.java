package com.budgetmap.service;

import com.budgetmap.dto.CanjearCuponRequest;
import com.budgetmap.dto.CuponRedimidoDTO;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.CuponRedimido;
import com.budgetmap.model.Establecimiento;
import com.budgetmap.model.Usuario;
import com.budgetmap.repository.CuponRedimidoRepository;
import com.budgetmap.repository.EstablecimientoRepository;
import com.budgetmap.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CuponService {

    @Autowired
    private PuntosService puntosService;

    @Autowired
    private CuponRedimidoRepository cuponRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Transactional
    public CuponRedimidoDTO canjearCupon(Long usuarioId, CanjearCuponRequest request) {
        
        // 1. Descontar los puntos
        puntosService.restarPuntos(usuarioId, request.getCostoPuntos());

        // 2. Traer las entidades
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Establecimiento local = establecimientoRepository.findById(request.getEstablecimientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado"));

        // 3. Generar código único
        String codigo = "BMAP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // 4. Crear y guardar la entidad
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

        // 5. Retornar el DTO limpio
        return mapearACuponDTO(nuevoCupon);
    }

    // Obtener los cupones activos de un explorador
    public List<CuponRedimidoDTO> obtenerMisCupones(Long usuarioId) {
        return cuponRepository.findByUsuarioIdOrderByFechaRedencionDesc(usuarioId)
                .stream()
                .map(this::mapearACuponDTO)
                .collect(Collectors.toList());
    }

    // Mapper interno
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
        // 1. Buscar el cupón en la base de datos
        CuponRedimido cupon = cuponRepository.findByCodigoUnico(codigo)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado o código inválido"));

        // 2. Verificar que el Aliado que escanea sea el dueño del local
        if (!cupon.getEstablecimiento().getPropietario().getId().equals(propietarioId)) {
            throw new RuntimeException("No tienes permisos para validar cupones de otro establecimiento");
        }

        // 3. Verificar que no haya sido usado antes
        if (cupon.getUsado()) {
            throw new RuntimeException("Este cupón ya fue redimido anteriormente");
        }

        // 4. Verificar la fecha de vencimiento
        if (cupon.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Este cupón ya ha expirado");
        }

        // 5. ¡Quemar el cupón!
        cupon.setUsado(true);
        cupon = cuponRepository.save(cupon);

        return mapearACuponDTO(cupon);
    }
}