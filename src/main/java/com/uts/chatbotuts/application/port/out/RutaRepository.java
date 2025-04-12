package com.uts.chatbotuts.application.port.out;

import com.uts.chatbotuts.domain.model.Ruta;

import java.util.List;

public interface RutaRepository {

    List<Ruta> findAll();
}
