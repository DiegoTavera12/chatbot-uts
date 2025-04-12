package com.uts.chatbotuts.infrastructure.dto;

import com.uts.chatbotuts.infrastructure.entity.FaqEntity;
import com.uts.chatbotuts.infrastructure.entity.PalabrasClaveEntity;
import lombok.*;

        import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link FaqEntity}
 */
@Value
@Data
@AllArgsConstructor
@Builder
@Setter
public class FaqEntityDto implements Serializable {
    Long id;
    String pregunta;
    String respuesta;
    Instant createdAt;
    Instant updatedAt;

    List<PalabrasClaveEntityDto>  listPalabrasClave;
}