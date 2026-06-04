package com.budgetmap.dto;

import com.budgetmap.model.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO implements Serializable {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private RolUsuario rol;
    private Integer puntosAcumulados;
    private Boolean activo;
    private LocalDateTime ultimoAcceso;
    private LocalDateTime createdAt;
    private String avatarUrl;
}
