package com.uts.chatbotuts.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Ruta {
    private String rutaUrl;
    private List<RoleDomain> roleDomains;
}
