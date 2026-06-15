package com.budgetmap.mapper;

import com.budgetmap.dto.UsuarioDTO;
import com.budgetmap.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDTO toDto(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .rol(usuario.getRol())
                .puntosAcumulados(usuario.getPuntosAcumulados())
                .activo(usuario.getActivo())
                .ultimoAcceso(usuario.getUltimoAcceso())
                .createdAt(usuario.getCreatedAt())
                .avatarUrl(usuario.getAvatarUrl())
                .build();
    }
}
