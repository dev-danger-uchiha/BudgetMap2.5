package com.example.budgetmap.config;

import com.example.budgetmap.model.Establecimiento;
import com.example.budgetmap.model.Evento;
import com.example.budgetmap.model.Lugar;
import com.example.budgetmap.model.Notificacion;
import com.example.budgetmap.model.Pqrs;
import com.example.budgetmap.model.Reserva;
import com.example.budgetmap.model.Usuario;
import com.example.budgetmap.model.enums.EstadoEstablecimiento;
import com.example.budgetmap.model.enums.EstadoPqrs;
import com.example.budgetmap.model.enums.EstadoReserva;
import com.example.budgetmap.model.enums.EstadoUsuario;
import com.example.budgetmap.model.enums.Role;
import com.example.budgetmap.model.enums.TipoEvento;
import com.example.budgetmap.model.enums.TipoEstablecimiento;
import com.example.budgetmap.model.enums.TipoLugar;
import com.example.budgetmap.repository.EstablecimientoRepository;
import com.example.budgetmap.repository.EventoRepository;
import com.example.budgetmap.repository.LugarRepository;
import com.example.budgetmap.repository.NotificacionRepository;
import com.example.budgetmap.repository.PqrsRepository;
import com.example.budgetmap.repository.ReservaRepository;
import com.example.budgetmap.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Profile({ "dev", "default" }) // ejecuta en perfil dev o por defecto; quita o ajusta según necesites
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EstablecimientoRepository establecimientoRepository;
    private final LugarRepository lugarRepository;
    private final EventoRepository eventoRepository;
    private final ReservaRepository reservaRepository;
    private final PqrsRepository pqrsRepository;
    private final NotificacionRepository notificacionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
            EstablecimientoRepository establecimientoRepository,
            LugarRepository lugarRepository,
            EventoRepository eventoRepository,
            ReservaRepository reservaRepository,
            PqrsRepository pqrsRepository,
            NotificacionRepository notificacionRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.establecimientoRepository = establecimientoRepository;
        this.lugarRepository = lugarRepository;
        this.eventoRepository = eventoRepository;
        this.reservaRepository = reservaRepository;
        this.pqrsRepository = pqrsRepository;
        this.notificacionRepository = notificacionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedUsers();
        seedLugarEventoEstablecimiento();
        seedReservaPqrsNotifications();
    }

    private void seedUsers() {
        createUserIfNotExists("admin", "admin@local", "admin123", Role.ROL_ADMIN, EstadoUsuario.ACTIVO,
                "Administrador");
        createUserIfNotExists("moderador", "moderador@local", "mod12345", Role.ROL_MODERADOR, EstadoUsuario.ACTIVO,
                "Moderador");
        createUserIfNotExists("establecimiento", "establecimiento@local", "est12345", Role.ROL_ESTABLECIMIENTO,
                EstadoUsuario.ACTIVO, "Propietario Establecimiento");
        createUserIfNotExists("cliente", "cliente@local", "cli12345", Role.ROL_CLIENTE, EstadoUsuario.ACTIVO,
                "Cliente Demo");
    }

    private void seedLugarEventoEstablecimiento() {
        // crear un lugar si no existe
        Optional<Lugar> maybeLugar = lugarRepository.findAll().stream().findFirst();
        Lugar lugar;
        if (maybeLugar.isEmpty()) {
            lugar = Lugar.builder()
                    .nombre("Parque Central Demo")
                    .tipo(TipoLugar.PARQUE)
                    .descripcion("Lugar de pruebas para eventos y demostraciones")
                    .ciudad("Bogota")
                    .direccion("Calle Falsa 123")
                    .estado(com.example.budgetmap.model.enums.EstadoLugar.PUBLICADO)
                    .build();
            lugar = lugarRepository.save(lugar);
        } else {
            lugar = maybeLugar.get();
        }

        // crear un evento si no existe
        if (eventoRepository.findAll().isEmpty()) {
            Evento evento = Evento.builder()
                    .titulo("Feria Cultural Demo")
                    .tipo(TipoEvento.CULTURAL)
                    .fechaInicio(LocalDateTime.now().plusDays(7))
                    .fechaFin(LocalDateTime.now().plusDays(7).plusHours(4))
                    .descripcion("Evento de ejemplo generado por DataInitializer")
                    .lugar(lugar)
                    .build();
            eventoRepository.save(evento);
        }

        // crear un establecimiento si no existe
        if (establecimientoRepository.findAll().isEmpty()) {
            Usuario propietario = usuarioRepository.findByUserName("establecimiento")
                    .orElse(null);
            Establecimiento est = Establecimiento.builder()
                    .nombre("Demo Restaurante")
                    .tipo(TipoEstablecimiento.RESTAURANTE)
                    .descripcion("Restaurante de pruebas creado por initializer")
                    .direccion("Av. Demo 1")
                    .ciudad("Bogota")
                    .contacto("+57 300 0000000")
                    .horarios("08:00-22:00")
                    .estado(EstadoEstablecimiento.APROBADO)
                    .creadoPor(propietario)
                    .build();
            establecimientoRepository.save(est);
        }
    }

    private void seedReservaPqrsNotifications() {
        // crear reserva de ejemplo por cliente al primer establecimiento
        Optional<Establecimiento> maybeEst = establecimientoRepository.findAll().stream().findFirst();
        Optional<Usuario> maybeCliente = usuarioRepository.findByUserName("cliente");
        if (maybeEst.isPresent() && maybeCliente.isPresent() && reservaRepository.findAll().isEmpty()) {
            Reserva r = Reserva.builder()
                    .usuario(maybeCliente.get())
                    .establecimiento(maybeEst.get())
                    .fechaReserva(LocalDateTime.now().plusDays(3))
                    .cantidad(2)
                    .estado(EstadoReserva.PENDIENTE)
                    .build();
            reservaRepository.save(r);
        }

        // crear PQRS de ejemplo
        Optional<Usuario> maybeUser = usuarioRepository.findByUserName("cliente");
        if (maybeUser.isPresent() && pqrsRepository.findAll().isEmpty()) {
            Pqrs p = Pqrs.builder()
                    .usuario(maybeUser.get())
                    .tipo(com.example.budgetmap.model.enums.TipoPqrs.SUGERENCIA)
                    .mensaje("Este es un mensaje de prueba para PQRS")
                    .estado(EstadoPqrs.ABIERTA)
                    .build();
            pqrsRepository.save(p);
        }

        // notificación de ejemplo para cliente
        if (maybeUser.isPresent() && notificacionRepository.findAll().isEmpty()) {
            Notificacion n = Notificacion.builder()
                    .usuario(maybeUser.get())
                    .tipo("INFO")
                    .contenido("Bienvenido a BudgetMap - cuenta de prueba creada")
                    .leido(false)
                    .build();
            notificacionRepository.save(n);
        }
    }

    private void createUserIfNotExists(String userName, String email, String rawPassword, Role role,
            EstadoUsuario estado, String nombre) {
        if (usuarioRepository.existsByUserName(userName))
            return;
        Usuario u = Usuario.builder()
                .userName(userName)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .rol(role)
                .estado(estado)
                .nombre(nombre)
                .build();
        usuarioRepository.save(u);
    }
}
