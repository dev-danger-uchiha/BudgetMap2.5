package com.budgetmap.service;

import com.budgetmap.dto.UsuarioDTO;
import com.budgetmap.exception.ResourceNotFoundException;
import com.budgetmap.model.Usuario;
import com.budgetmap.model.enums.RolUsuario;
import com.budgetmap.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

            // Reemplazamos los System.out por logs profesionales
            log.info("[SEEDER] Usuario Administrador creado con éxito.");
            log.info("[SEEDER] Email: {} | Password: admin1234", adminEmail);
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

    public List<UsuarioDTO> obtenerLeaderboard(int limite) {
        return usuarioRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .sorted((u1, u2) -> Integer.compare(
                        u2.getPuntosAcumulados() != null ? u2.getPuntosAcumulados() : 0,
                        u1.getPuntosAcumulados() != null ? u1.getPuntosAcumulados() : 0))
                .limit(limite)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Fallo al consultar usuario: ID {} no encontrado", id);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });
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
        log.warn("Desactivando usuario ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario ID: {} desactivado exitosamente", id);
    }

    @Transactional
    public void activarUsuario(Long id) {
        log.info("Activando usuario ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        log.info("Usuario ID: {} activado exitosamente", id);
    }

    @Transactional
    public void cambiarRol(Long id, RolUsuario nuevoRol) {
        log.info("Cambiando rol del usuario ID: {} a {}", id, nuevoRol);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void sumarPuntos(Long id, Integer puntos) {
        log.debug("Añadiendo {} puntos extra al usuario ID: {}", puntos, id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() + puntos);
        usuarioRepository.save(usuario);
    }

    public Page<UsuarioDTO> listarPaginadoConFiltros(Pageable pageable, String criterio, String rol, Boolean activo) {
        List<Usuario> usuarios = usuarioRepository.findAll();

        if (criterio != null && !criterio.isBlank()) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getNombre().toLowerCase().contains(criterio.toLowerCase()) ||
                                u.getApellido().toLowerCase().contains(criterio.toLowerCase()) ||
                                u.getEmail().toLowerCase().contains(criterio.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (rol != null && !rol.isBlank()) {
            try {
                RolUsuario rolEnum = RolUsuario.valueOf(rol);
                usuarios = usuarios.stream()
                        .filter(u -> u.getRol() == rolEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                log.warn("Rol inválido: {}", rol);
            }
        }

        if (activo != null) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getActivo() == activo)
                    .collect(Collectors.toList());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), usuarios.size());
        List<UsuarioDTO> dtos = usuarios.subList(start, end).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, usuarios.size());
    }

    public Long contarPorRol(RolUsuario rol) {
        return usuarioRepository.countByRol(rol);
    }

    @Transactional
    public void cambiarEstadoConValidacion(Long id, boolean activo, Long adminActualId) {
        if (id.equals(adminActualId)) {
            log.warn("Intento de cambiar estado del usuario actual. Admin ID: {}", adminActualId);
            throw new RuntimeException("No puedes cambiar tu propio estado. Contacta a otro administrador.");
        }

        if (activo) {
            activarUsuario(id);
        } else {
            desactivarUsuario(id);
        }
    }

    @Transactional
    public UsuarioDTO crearUsuario(String nombre, String apellido, String email, String password, String rol) {
        log.info("Creando nuevo usuario: {}", email);

        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        try {
            RolUsuario rolEnum = RolUsuario.valueOf(rol.toUpperCase());

            Usuario usuario = Usuario.builder()
                    .nombre(nombre)
                    .apellido(apellido)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .rol(rolEnum)
                    .puntosAcumulados(0)
                    .activo(true)
                    .emailVerificado(true)
                    .build();

            Usuario guardado = usuarioRepository.save(usuario);
            log.info("Usuario creado exitosamente con ID: {}", guardado.getId());
            return convertirADTO(guardado);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol inválido: " + rol);
        }
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
                .avatarUrl(usuario.getAvatarUrl())
                .build();
    }

    @Transactional
    public void actualizarAvatar(Long id, String avatarUrl) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setAvatarUrl(avatarUrl);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void actualizarPerfil(Long id, String nombre, String apellido, String telefono, String password) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        
        if (nombre != null && !nombre.isBlank()) {
            usuario.setNombre(nombre);
        }
        if (apellido != null && !apellido.isBlank()) {
            usuario.setApellido(apellido);
        }
        if (telefono != null && !telefono.isBlank()) {
            usuario.setTelefono(telefono);
        }
        if (password != null && !password.isBlank()) {
            usuario.setPassword(passwordEncoder.encode(password));
        }
        
        usuarioRepository.save(usuario);
    }
}