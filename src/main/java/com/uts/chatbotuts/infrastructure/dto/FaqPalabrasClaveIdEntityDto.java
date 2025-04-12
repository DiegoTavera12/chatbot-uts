package com.uts.chatbotuts.infrastructure.dto;

import com.uts.chatbotuts.infrastructure.entity.FaqPalabrasClaveIdEntity;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link FaqPalabrasClaveIdEntity}
 */
@Value
public class FaqPalabrasClaveIdEntityDto implements Serializable {
    Long idFaq;
    Long idPalabra;
}