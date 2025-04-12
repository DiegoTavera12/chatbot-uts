package com.uts.chatbotuts.application.port.in;

import com.uts.chatbotuts.domain.model.FaqDomain;

import java.util.List;

public interface ChatConsultaUseCase {
    List<Long> obtenerIdsPalabrasClaves(String[] palabrasClaves);

    List<Long> obtenerIdsFaqByPalabrasClaveIds(List<Long> idsPalabrasClave);

    List<FaqDomain> obtenerFaqsByIds(List<Long> idsFaq);

}
