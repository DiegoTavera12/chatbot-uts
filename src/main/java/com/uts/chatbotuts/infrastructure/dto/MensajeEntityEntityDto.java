package com.uts.chatbotuts.infrastructure.dto;

import com.uts.chatbotuts.infrastructure.entity.MensajeEntity;
import com.uts.chatbotuts.infrastructure.entity.UsuarioEntity;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link MensajeEntity}
 */
@Value
public class MensajeEntityEntityDto implements Serializable {
    Long id;
    ChatEntityDto idChat;
    UsuarioEntity idUser;
    String mensaje;
    Instant createdAt;
}