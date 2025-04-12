package com.uts.chatbotuts.application.service;

import com.uts.chatbotuts.application.port.in.ObtenerUsuariosUseCase;
import com.uts.chatbotuts.domain.model.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class LazyUsuarioDataModel extends LazyDataModel<UsuarioDomain> {

    private final ObtenerUsuariosUseCase obtenerUsuariosUseCase;
    private List<UsuarioDomain> datasource;

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
        return obtenerUsuariosUseCase.countUsuarios(filters);
    }

    @Override
    public List<UsuarioDomain> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
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


        List<UsuarioDomain> list = obtenerUsuariosUseCase.getUsuarios(first, pageSize, sortField, ascending, filters);
        // Establece el total de registros para la paginación.
        this.setRowCount(obtenerUsuariosUseCase.countUsuarios(filters));
        datasource = list;

        // Delegar al caso de uso para obtener la lista paginada de usuarios
        return list;
    }


    @Override
    public String getRowKey(UsuarioDomain usuario) {
        // Asegúrate de que el ID es único; si usuario es nulo, retorna null.
        return String.valueOf(usuario != null ? usuario.getId() : null);
    }

    @Override
    public UsuarioDomain getRowData(String rowKey) {
        if (datasource != null) {
            for (UsuarioDomain usuario : datasource) {
                if (usuario.getId() != null && usuario.getId().toString().equals(rowKey)) {
                    return usuario;
                }
            }
        }
        return null;
    }
}
