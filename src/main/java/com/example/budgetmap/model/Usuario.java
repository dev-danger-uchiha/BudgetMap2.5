package com.example.budgetmap.model;

import com.example.budgetmap.model.enums.Role;
import com.example.budgetmap.model.enums.EstadoUsuario;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Usuario extends BaseEntity {

    @Column(name = "user_name", nullable = false, unique = true, length = 100)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Role rol;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private EstadoUsuario estado = EstadoUsuario.PENDIENTE;

    private String nombre;

    @Column(unique = true)
    private String email;

    private String telefono;

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
