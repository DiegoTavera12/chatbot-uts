package com.uts.chatbotuts.application.service;

import com.uts.chatbotuts.application.port.in.EliminarFaqUseCase;
import com.uts.chatbotuts.application.port.out.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EliminarFaqUseCaseImp implements EliminarFaqUseCase {

    private final FaqRepository faqRepository;

    @Override
    public void eliminarFaqs(List<Long> ids) {
        faqRepository.eliminarFaqs(ids);

    }

    @Override
    public void eliminarFaq(Long id) {

        faqRepository.eliminarFaq(id);
    }
}
