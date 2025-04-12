package com.uts.chatbotuts.application.port.out;

import java.util.List;

public interface FaqPalabrasClaveRepository {

    void guardarAsociacion(Long faqId, Long palabraClaveId);

    List<Long> buscarPalabrasClaveIdsByFaqId(Long faqId);

    List<Long> buscarFaqIdsByPalabrasClaveIds(List<Long> idsPalabrasClave);

    void eliminarAsociacion(Long faqId, Long palabraClaveId);

    boolean existeAsociacion(Long faqId, Long palabraClaveId);
}
