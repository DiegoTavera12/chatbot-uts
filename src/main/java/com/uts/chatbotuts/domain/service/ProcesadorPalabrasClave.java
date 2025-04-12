package com.uts.chatbotuts.domain.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProcesadorPalabrasClave {
    public ProcesadorPalabrasClave() {
    }

    public List<String> procesar(String palabrasClaveStr) {
        if (palabrasClaveStr == null || palabrasClaveStr.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(palabrasClaveStr.split(","))
                .map(String::trim)
                .filter(p -> !p.isEmpty())
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }
}
