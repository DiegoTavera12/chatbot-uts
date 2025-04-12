package com.uts.chatbotuts.infrastructure.adapter.out.persistence;

import com.uts.chatbotuts.application.port.out.UsuarioTransactionalRepository;
import com.uts.chatbotuts.domain.model.RoleDomain;
import com.uts.chatbotuts.domain.model.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class UsuarioTransactionalRepositoryAdapter implements UsuarioTransactionalRepository {

    private final UsuarioRepositoryAdapter usuarioRepositoryAdapter;

    @Override
    @Transactional
    public UsuarioDomain registrarUsuarioYAsignarRol(UsuarioDomain usuario, long idRole) {
        RoleDomain roleDomain = new RoleDomain();
        roleDomain.setId(idRole);
        usuario.setRole(roleDomain);
        UsuarioDomain userDto = usuarioRepositoryAdapter.registrarOActualizarUsuario(usuario);
        return userDto;
    }

    @Override
    public int countByFilters(Map<String, Object> filters) {
        return 0;
    }

    @Override
    public List<UsuarioDomain> findByFilters(int first, int pageSize, String sortField, boolean ascending, Map<String, Object> filters) {
        return List.of();
    }

    // Método para eliminar uno o varios usuarios de forma transaccional
    @Override
    @Transactional
    public void eliminarUsuarios(List<Long> ids) {
        usuarioRepositoryAdapter.eliminarUsuarios(ids);
    }

    // Método de conveniencia para eliminar un solo usuario
    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepositoryAdapter.eliminarUsuario(id);
    }

}
