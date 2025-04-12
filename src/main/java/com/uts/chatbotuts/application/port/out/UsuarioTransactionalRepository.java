package com.uts.chatbotuts.application.port.out;

import com.uts.chatbotuts.domain.model.UsuarioDomain;

import java.util.List;
import java.util.Map;

public interface UsuarioTransactionalRepository {

    UsuarioDomain registrarUsuarioYAsignarRol(UsuarioDomain usuario, long idRole);


    // Método para contar registros filtrados
    int countByFilters(Map<String, Object> filters);

    // Método para obtener registros paginados y filtrados
    List<UsuarioDomain> findByFilters(int first, int pageSize, String sortField, boolean ascending, Map<String, Object> filters);

    public void eliminarUsuarios(List<Long> ids);

    void eliminarUsuario(Long id);

}
