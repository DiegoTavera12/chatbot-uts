package com.uts.chatbotuts.application.port.out;

import com.uts.chatbotuts.domain.model.FaqDomain;
import com.uts.chatbotuts.domain.model.UsuarioDomain;

import java.util.List;
import java.util.Map;

public interface FaqRepository {
    FaqDomain save(FaqDomain faqDomain);

    List<FaqDomain> findAll();

    List<FaqDomain> findByIds(List<Long> idsFaq);

    int countByFilters(Map<String, Object> filters);

    // Método para obtener registros paginados y filtrados
    List<FaqDomain> findByFilters(int first, int pageSize, String sortField, boolean ascending, Map<String, Object> filters);

    void eliminarFaqs(List<Long> ids);

    void eliminarFaq(Long id);

    void actualzarUsoFaq(Long id);

    FaqDomain findById(Long id);

}
