package com.uts.chatbotuts.infrastructure.dto;

import com.uts.chatbotuts.infrastructure.entity.ChatEntity;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link ChatEntity}
 */
@Value
public class ChatEntityDto implements Serializable {
    Long id;
    HistorialChatEntityDto idHistorial;
    Instant createdAt;
}