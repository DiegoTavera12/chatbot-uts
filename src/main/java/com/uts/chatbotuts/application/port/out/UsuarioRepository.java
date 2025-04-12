package com.uts.chatbotuts.application.port.out;

import com.uts.chatbotuts.domain.model.UsuarioDomain;

import java.util.List;
import java.util.Map;

public interface UsuarioRepository {

    UsuarioDomain registrarOActualizarUsuario(UsuarioDomain usuario);

    // Método para contar registros filtrados
    int countByFilters(Map<String, Object> filters);

    // Método para obtener registros paginados y filtrados
    List<UsuarioDomain> findByFilters(int first, int pageSize, String sortField, boolean ascending, Map<String, Object> filters);

    void eliminarUsuarios(List<Long> ids);

    void eliminarUsuario(Long id);
}