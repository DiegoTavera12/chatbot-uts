package com.uts.chatbotuts.application.service;

import com.uts.chatbotuts.application.port.in.ActualizarUsosFaqUseCase;
import com.uts.chatbotuts.application.port.out.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ActualizarUsosFaqUseCaseImpl implements ActualizarUsosFaqUseCase {

    private final FaqRepository faqRepository;

    @Override
    public void actualizarUsoFaq(long id) {
        faqRepository.actualzarUsoFaq(id);
    }
}
