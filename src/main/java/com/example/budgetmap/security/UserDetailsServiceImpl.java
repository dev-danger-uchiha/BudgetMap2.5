package com.example.budgetmap.security;

import com.example.budgetmap.model.Usuario;
import com.example.budgetmap.model.enums.EstadoUsuario;
import com.example.budgetmap.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.DisabledException;

import java.util.Collections;

/**
 * Implementación de UserDetailsService que carga Usuario desde la BD
 * y lo convierte a UserDetails usado por Spring Security.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

        private final UsuarioRepository usuarioRepository;

        public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
                this.usuarioRepository = usuarioRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Usuario usuario = usuarioRepository.findByUserName(username)
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

                // Si tu lógica requiere bloquear usuarios no ACTIVO, valida aquí
                if (usuario.getEstado() != null && usuario.getEstado() != EstadoUsuario.ACTIVO) {
                        throw new DisabledException("Usuario no activo: " + username);
                }

                GrantedAuthority authority = new SimpleGrantedAuthority(
                                usuario.getRol() != null ? usuario.getRol().name() : "ROL_CLIENTE");

                return User.builder()
                                .username(usuario.getUserName())
                                .password(usuario.getPassword())
                                .authorities(Collections.singleton(authority))
                                .accountExpired(false)
                                .accountLocked(false)
                                .credentialsExpired(false)
                                .disabled(false)
                                .build();
        }
}
