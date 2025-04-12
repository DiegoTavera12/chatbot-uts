package com.uts.chatbotuts.application.service;


import com.uts.chatbotuts.application.port.in.ObtenerFaqsUseCase;
import com.uts.chatbotuts.domain.model.FaqDomain;
import lombok.RequiredArgsConstructor;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class LazyFaqDataModel extends LazyDataModel<FaqDomain> {


    private final ObtenerFaqsUseCase obtenerFaqsUseCase;
    private List<FaqDomain> datasource;

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        // Convertir el mapa de FilterMeta a un mapa simple de filtros
        Map<String, Object> filters = new HashMap<>();
        if (filterBy != null) {
            filterBy.forEach((key, filterMeta) -> {
                filters.put(key, filterMeta.getFilterValue());
            });
        }
        // Delegar al caso de uso para obtener el total de registros filtrados
        return obtenerFaqsUseCase.countFaqs(filters);
    }

    @Override
    public List<FaqDomain> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        // Procesar el ordenamiento: tomamos el primer criterio si existe
        String sortField = null;
        boolean ascending = true;
        if (sortBy != null && !sortBy.isEmpty()) {
            Map.Entry<String, SortMeta> entry = sortBy.entrySet().iterator().next();
            sortField = entry.getKey();
            ascending = SortOrder.ASCENDING.equals(entry.getValue().getOrder());
        }
        // Convertir el mapa de FilterMeta a un mapa simple de filtros
        Map<String, Object> filters = new HashMap<>();
        if (filterBy != null) {
            filterBy.forEach((key, filterMeta) -> {
                filters.put(key, filterMeta.getFilterValue());
            });
        }


        List<FaqDomain> list = obtenerFaqsUseCase.getFaqs(first, pageSize, sortField, ascending, filters);
        // Establece el total de registros para la paginación.
        this.setRowCount(obtenerFaqsUseCase.countFaqs(filters));
        datasource = list;

        // Delegar al caso de uso para obtener la lista paginada de usuarios
        return list;
    }


    @Override
    public String getRowKey(FaqDomain faqDomain) {
        // Asegúrate de que el ID es único; si usuario es nulo, retorna null.
        return String.valueOf(faqDomain != null ? faqDomain.getId() : null);
    }

    @Override
    public FaqDomain getRowData(String rowKey) {
        if (datasource != null) {
            for (FaqDomain faqDomain : datasource) {
                if (faqDomain.getId() != null && faqDomain.getId().toString().equals(rowKey)) {
                    return faqDomain;
                }
            }
        }
        return null;
    }
}

