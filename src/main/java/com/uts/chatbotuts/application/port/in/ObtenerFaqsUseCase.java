package com.uts.chatbotuts.application.port.in;

import com.uts.chatbotuts.domain.model.FaqDomain;
import com.uts.chatbotuts.domain.model.UsuarioDomain;

import java.util.List;
import java.util.Map;

public interface ObtenerFaqsUseCase {

    List<FaqDomain> obtenerFaqs();

    int countFaqs(Map<String, Object> filters);
    List<FaqDomain> getFaqs(int first, int pageSize, String sortField, boolean ascending, Map<String, Object> filters);

}
