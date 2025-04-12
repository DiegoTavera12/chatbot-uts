package com.uts.chatbotuts.application.service;

import com.uts.chatbotuts.application.port.in.ObtenerUsuariosUseCase;
import com.uts.chatbotuts.application.port.out.UsuarioRepository;
import com.uts.chatbotuts.domain.model.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ObtenerUsuariosUseCaseImpl implements ObtenerUsuariosUseCase {
    private final UsuarioRepository usuarioRepository;

    @Override
    public int countUsuarios(Map<String, Object> filters) {
        // Implementa la lógica para contar usuarios aplicando los filtros.
        return usuarioRepository.countByFilters(filters);
    }

    @Override
    public List<UsuarioDomain> getUsuarios(int first, int pageSize, String sortField, boolean ascending, Map<String, Object> filters) {
        // Implementa la lógica para recuperar una lista de usuarios aplicando paginación, ordenamiento y filtros.
        return usuarioRepository.findByFilters(first, pageSize, sortField, ascending, filters);
    }
}
