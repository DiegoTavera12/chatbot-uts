package com.uts.chatbotuts.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class RoleDomain {
    private long id;
    private String nombre;
    private String descripcion;
}
