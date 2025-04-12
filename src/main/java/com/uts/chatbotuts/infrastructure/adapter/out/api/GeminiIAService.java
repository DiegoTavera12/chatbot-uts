package com.uts.chatbotuts.infrastructure.adapter.out.api;

import com.uts.chatbotuts.application.port.out.GeminiIAServicePort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiIAService implements GeminiIAServicePort {
    private String apiKey="AIzaSyBZM2iLhZBuZH0ne1kbuUiddTc6jA2houA";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    @Override
    public String obtenerPalabrasClave(String texto, String respuesta) {
        // Construir la URL completa con la API key
        String url = GEMINI_API_URL + apiKey;
        String prompt = "A partir de la siguiente pregunta y respuesta, genera palabras clave en español para enlazarlas y facilitar su búsqueda."
                + "dame 20 palabras, dame solo y unicamente las palabras, no coloques enunciado ni nada mas"
                + "Las palabras clave deben estar separadas por comas.\n\n"
                + "Pregunta: " + texto + "\n"
                + "Respuesta: " + respuesta;
        // Construir el cuerpo de la petición de acuerdo al formato requerido
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        List<Map<String, Object>> parts = Collections.singletonList(part);
        Map<String, Object> content = new HashMap<>();
        content.put("parts", parts);
        List<Map<String, Object>> contents = Collections.singletonList(content);
        requestBody.put("contents", contents);

        // Configurar las cabeceras
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        // Procesar la respuesta para extraer las palabras clave
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map responseBody = response.getBody();
            List<Map> candidates = (List<Map>) responseBody.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map candidate = candidates.get(0);
                Map contentCandidate = (Map) candidate.get("content");
                List<Map> partsList = (List<Map>) contentCandidate.get("parts");
                if (partsList != null && !partsList.isEmpty()) {
                    String palabrasClave = (String) partsList.get(0).get("text");
                    return palabrasClave;
                }
            }
        }
        return "Error al generar contenido";
    }
}
