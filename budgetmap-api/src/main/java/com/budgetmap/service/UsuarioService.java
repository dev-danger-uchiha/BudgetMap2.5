package com.budgetmap.service;

import com.budgetmap.dto.UsuarioDTO;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.RolUsuario;
import com.budgetmap.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        String adminEmail = "admin@budgetmap.com";

        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = Usuario.builder()
                    .nombre("Admin")
                    .apellido("BudgetMap")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin1234"))
                    .rol(RolUsuario.ADMINISTRADOR)
                    .puntosAcumulados(0)
                    .activo(true)
                    .emailVerificado(true)
                    .build();

            usuarioRepository.save(admin);

            System.out.println("\n[SEEDER] Usuario Administrador creado con éxito.");
            System.out.println("[SEEDER] Email: " + adminEmail);
            System.out.println("[SEEDER] Password: admin1234\n");
        }
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public Page<UsuarioDTO> listarActivos(Pageable pageable) {
        return usuarioRepository.findByActivoTrue(pageable)
                .map(this::convertirADTO);
    }

    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirADTO(usuario);
    }

    public List<UsuarioDTO> listarPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> buscarPorNombreOEmail(String criterio) {
        List<Usuario> usuarios = usuarioRepository.findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(criterio,
                criterio);
        return usuarios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void activarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarRol(Long id, RolUsuario nuevoRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void sumarPuntos(Long id, Integer puntos) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() + puntos);
        usuarioRepository.save(usuario);
    }

    public Long contarPorRol(RolUsuario rol) {
        return usuarioRepository.countByRol(rol);
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
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
                .build();
    }
}