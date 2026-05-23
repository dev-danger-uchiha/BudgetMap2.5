package com.budgetmap.security;

import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.RolUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
@Builder
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private String username;

    @JsonIgnore
    private String password;

    private RolUsuario rol;
    private Collection<? extends GrantedAuthority> authorities;
    private Boolean activo;

    public static UserDetailsImpl build(Usuario usuario) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name());

        return new UserDetailsImpl(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getRol(),
                Collections.singletonList(authority),
                usuario.getActivo());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo != null ? activo : true;
    }
}