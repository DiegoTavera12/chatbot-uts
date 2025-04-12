package com.uts.chatbotuts.application.port.out;

import com.uts.chatbotuts.domain.model.PalabraClave;

import java.util.List;

public interface PalabraClaveRepository {

    PalabraClave save(PalabraClave palabraClave);

    List<Long> findIdsBySimilarPalabras(String[] palabrasClave);

    List<PalabraClave> buscarPalabrasClavesByIds(List<Long> ids);

    PalabraClave obtenerPorId(Long id);

    PalabraClave buscarPorPalabra(String palabra);

}
