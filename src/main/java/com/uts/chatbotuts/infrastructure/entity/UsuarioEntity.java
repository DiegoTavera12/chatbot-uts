package com.uts.chatbotuts.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "usuarios", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "usuarios_documento_identificacion_key", columnNames = {"documento_identificacion"}),
        @UniqueConstraint(name = "usuarios_correo_electronico_key", columnNames = {"correo_electronico"})
})
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuarios_seq")
    @SequenceGenerator(name = "usuarios_seq", sequenceName = "usuarios_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "documento_identificacion", nullable = false, length = 50)
    private String documentoIdentificacion;

    @Column(name = "documento_estudiante", length = 50)
    private String documentoEstudiante;

    @Column(name = "correo_electronico", nullable = false, length = 150)
    private String correoElectronico;

    @ColumnDefault("true")
    @Column(name = "estado")
    private Boolean estado;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "idUser")
    private Set<HistorialChatEntity> historialChats = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUser")
    private Set<MensajeEntity> mensajes = new LinkedHashSet<>();

    @Column(name = "contrasena", length = Integer.MAX_VALUE)
    private String contrasena;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private RoleEntity rol;

}