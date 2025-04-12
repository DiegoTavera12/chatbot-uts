package com.uts.chatbotuts.application.port.in;

import com.uts.chatbotuts.domain.model.FaqDomain;

public interface RegistrarFaqUseCase {

    FaqDomain guardarFaq(FaqDomain domain);

}
