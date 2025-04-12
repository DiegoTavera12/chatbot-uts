package com.uts.chatbotuts.infrastructure.dto;

import com.uts.chatbotuts.infrastructure.entity.UsuarioEntity;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link UsuarioEntity}
 */
@Value
public class UsuarioEntityDto implements Serializable {
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
}