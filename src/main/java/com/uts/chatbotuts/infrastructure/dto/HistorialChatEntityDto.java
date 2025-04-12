package com.uts.chatbotuts.infrastructure.dto;

import com.uts.chatbotuts.infrastructure.entity.HistorialChatEntity;
import com.uts.chatbotuts.infrastructure.entity.UsuarioEntity;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link HistorialChatEntity}
 */
@Value
public class HistorialChatEntityDto implements Serializable {
    Long id;
    UsuarioEntity idUser;
    Boolean estado;
    Instant createdAt;
}