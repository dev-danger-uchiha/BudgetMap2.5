package com.example.budgetmap.model;

import com.example.budgetmap.model.enums.Role;
import com.example.budgetmap.model.enums.EstadoUsuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true, length = 100)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Role rol;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoUsuario estado = EstadoUsuario.PENDIENTE;

    private String nombre;

    @Column(unique = true)
    private String email;

    private String telefono;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "creadoPor", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Lugar> lugares;

    @OneToMany(mappedBy = "creador", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Evento> eventos;

    @OneToMany(mappedBy = "creadoPor", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Establecimiento> establecimientos;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pqrs> pqrs;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Notificacion> notificaciones;
}
