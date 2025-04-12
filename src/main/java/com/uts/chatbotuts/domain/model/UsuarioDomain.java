package com.uts.chatbotuts.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class UsuarioDomain {

    Long id;
    String nombre;
    String apellido;
    String documentoIdentificacion;
    String documentoEstudiante;
    String correoElectronico;
    Boolean estado;
    Instant createdAt;
    Instant updatedAt;
    String contrasena;
    RoleDomain role;

}
