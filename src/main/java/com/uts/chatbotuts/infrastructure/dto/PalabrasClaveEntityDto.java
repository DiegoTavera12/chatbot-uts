package com.uts.chatbotuts.infrastructure.dto;

import com.uts.chatbotuts.infrastructure.entity.PalabrasClaveEntity;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link PalabrasClaveEntity}
 */
@Value
public class PalabrasClaveEntityDto implements Serializable {
    Long id;
    String palabra;
}