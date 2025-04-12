package com.uts.chatbotuts.application.port.in;

import com.uts.chatbotuts.domain.model.UsuarioDomain;

import java.util.List;
import java.util.Map;

public interface ObtenerUsuariosUseCase {
    int countUsuarios(Map<String, Object> filters);
    List<UsuarioDomain> getUsuarios(int first, int pageSize, String sortField, boolean ascending, Map<String, Object> filters);


}
