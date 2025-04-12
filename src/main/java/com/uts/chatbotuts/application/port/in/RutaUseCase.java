package com.uts.chatbotuts.application.port.in;

import com.uts.chatbotuts.domain.model.Ruta;

import java.util.List;
import java.util.Map;

public interface RutaUseCase {

    List<Ruta> listar();

    Map<String, List<String>> getRutaRolMappings();

}
