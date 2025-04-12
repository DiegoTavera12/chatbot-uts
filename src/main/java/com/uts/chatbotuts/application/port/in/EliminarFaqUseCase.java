package com.uts.chatbotuts.application.port.in;

import java.util.List;

public interface EliminarFaqUseCase {

    public void eliminarFaqs(List<Long> ids);

    void eliminarFaq(Long id);

}
