package com.uts.chatbotuts.infrastructure.dto;

import com.uts.chatbotuts.infrastructure.entity.RoleEntity;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link RoleEntity}
 */
@Value
public class RoleEntityDto implements Serializable {
    Long id;
    String nombre;
    String descripcion;
    Instant createdAt;
    Instant updatedAt;
}