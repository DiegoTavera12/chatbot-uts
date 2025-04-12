package com.uts.chatbotuts.infrastructure.dto;

import com.uts.chatbotuts.infrastructure.entity.FaqPalabrasClaveEntity;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link FaqPalabrasClaveEntity}
 */
@Value
public class FaqPalabrasClaveEntityDto implements Serializable {
    FaqPalabrasClaveIdEntityDto id;
    FaqEntityDto idFaqEntity;
    PalabrasClaveEntityDto idPalabra;
}