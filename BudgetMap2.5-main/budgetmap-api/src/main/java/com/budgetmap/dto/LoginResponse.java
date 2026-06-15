package com.budgetmap.dto;

import com.budgetmap.model.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    @Builder.Default
    private String tipo = "Bearer";
    private Long id;
    private String nombre;
    private String email;
    private RolUsuario rol;
    private Integer puntosAcumulados;
}
