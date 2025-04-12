package com.uts.chatbotuts.application.port.in;

import com.uts.chatbotuts.domain.model.FaqDomain;

import java.util.List;

public interface FaqManagementUseCase {
    void guardarFaq(String pregunta, String respuesta, String palabrasClave, boolean isBuscarPalabrasClave);
    List<FaqDomain> obtenerFaqConPalabrasClave();
}
