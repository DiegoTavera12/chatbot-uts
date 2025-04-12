package com.uts.chatbotuts.application.service;

import com.uts.chatbotuts.application.port.in.ChatConsultaUseCase;
import com.uts.chatbotuts.application.port.out.FaqPalabrasClaveRepository;
import com.uts.chatbotuts.application.port.out.FaqRepository;
import com.uts.chatbotuts.application.port.out.PalabraClaveRepository;
import com.uts.chatbotuts.domain.model.FaqDomain;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatConsultaService implements ChatConsultaUseCase {

    private final PalabraClaveRepository palabraClaveRepository;
    private final FaqPalabrasClaveRepository faqPalabrasClaveRepository;
    private final FaqRepository faqRepository;

    public ChatConsultaService(PalabraClaveRepository palabraClaveRepository,
                               FaqPalabrasClaveRepository faqPalabrasClaveRepository,
                               FaqRepository faqRepository) {
        this.palabraClaveRepository = palabraClaveRepository;
        this.faqPalabrasClaveRepository = faqPalabrasClaveRepository;
        this.faqRepository = faqRepository;
    }

    @Override
    public List<Long> obtenerIdsPalabrasClaves(String[] palabrasClaves) {
        // Se delega en el puerto de salida que se encarga de buscar IDs de palabras clave similares
        return palabraClaveRepository.findIdsBySimilarPalabras(palabrasClaves);
    }

    @Override
    public List<Long> obtenerIdsFaqByPalabrasClaveIds(List<Long> idsPalabrasClave) {
        // Se delega en el puerto de salida que se encarga de obtener las asociaciones FAQ – PalabraClave
        return faqPalabrasClaveRepository.buscarFaqIdsByPalabrasClaveIds(idsPalabrasClave);
    }

    @Override
    public List<FaqDomain> obtenerFaqsByIds(List<Long> idsFaq) {
        // Se delega en el puerto de salida para recuperar las FAQs
        return faqRepository.findByIds(idsFaq);
    }
}
