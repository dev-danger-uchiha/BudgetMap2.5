package com.example.budgetmap.service;

import com.example.budgetmap.model.Usuario;
import com.example.budgetmap.model.enums.Role;
import com.example.budgetmap.model.enums.EstadoUsuario;
import com.example.budgetmap.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> findByUserName(String userName) {
        return usuarioRepository.findByUserName(userName);
    }

    public boolean existsByUserName(String userName) {
        return usuarioRepository.existsByUserName(userName);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario save(Usuario usuario) {
        // si viene password en claro, encriptar; si ya está encriptada, asume caller
        if (usuario.getPassword() != null && !usuario.getPassword().startsWith("{bcrypt}")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        if (usuario.getRol() == null) {
            usuario.setRol(Role.ROL_CLIENTE);
        }
        if (usuario.getEstado() == null) {
            usuario.setEstado(EstadoUsuario.PENDIENTE);
        }
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario createCliente(Usuario usuario) {
        usuario.setRol(Role.ROL_CLIENTE);
        usuario.setEstado(EstadoUsuario.PENDIENTE);
        return save(usuario);
    }
}
