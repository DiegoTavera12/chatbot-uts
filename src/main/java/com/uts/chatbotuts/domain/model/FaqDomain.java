package com.uts.chatbotuts.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FaqDomain {
    private Long id;
    private String pregunta;
    private String respuesta;
    private Instant createdAt;
    private Instant updatedAt;
    private List<PalabraClave> palabrasClave;
    private Long vecesUsada;
    private Boolean estado = false;
    private boolean esBuscarPalabrasClave = false;
    private String palabrasClaveString;
}
