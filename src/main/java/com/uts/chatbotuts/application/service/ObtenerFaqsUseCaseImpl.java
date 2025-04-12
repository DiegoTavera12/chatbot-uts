package com.uts.chatbotuts.application.service;

import com.uts.chatbotuts.application.port.in.ObtenerFaqsUseCase;
import com.uts.chatbotuts.application.port.out.FaqPalabrasClaveRepository;
import com.uts.chatbotuts.application.port.out.FaqRepository;
import com.uts.chatbotuts.application.port.out.PalabraClaveRepository;
import com.uts.chatbotuts.domain.model.FaqDomain;
import com.uts.chatbotuts.domain.model.UsuarioDomain;
import com.uts.chatbotuts.infrastructure.adapter.out.persistence.FaqPalabrasClaveRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ObtenerFaqsUseCaseImpl implements ObtenerFaqsUseCase {

    private final FaqRepository faqRepository;
    private final FaqPalabrasClaveRepository faqPalabrasClaveRepository;
    private final PalabraClaveRepository palabraClaveRepository;

    private static final Logger logger = LoggerFactory.getLogger(FaqPalabrasClaveRepositoryAdapter.class);


    @Override
    public List<FaqDomain> obtenerFaqs() {

        try {
            List<FaqDomain> faqDomains = faqRepository.findAll();
            for (FaqDomain faqDomain : faqDomains) {
                List<Long> ids = faqPalabrasClaveRepository.buscarPalabrasClaveIdsByFaqId(faqDomain.getId());
                faqDomain.setPalabrasClave(palabraClaveRepository.buscarPalabrasClavesByIds(ids));
            }
            return faqDomains;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        return null;
    }

    @Override
    public int countFaqs(Map<String, Object> filters) {
        return faqRepository.countByFilters(filters);
    }

    @Override
    public List<FaqDomain> getFaqs(int first, int pageSize, String sortField, boolean ascending, Map<String, Object> filters) {
        return faqRepository.findByFilters(first, pageSize, sortField, ascending, filters);
    }
}
