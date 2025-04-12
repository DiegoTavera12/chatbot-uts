package com.uts.chatbotuts.infrastructure.adapter.out.persistence;

import com.uts.chatbotuts.application.port.out.RolRepository;
import com.uts.chatbotuts.domain.model.RoleDomain;
import com.uts.chatbotuts.infrastructure.entity.RoleEntity;
import com.uts.chatbotuts.infrastructure.persistence.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class RoleRepositoryAdapter implements RolRepository {

    private final RoleJpaRepository roleJpaRepository;

    @Transactional
    @Override
    public RoleDomain obtenerRolByNombre(String rol) {
        RoleEntity ent = roleJpaRepository.findByNombre(rol).orElse(null);

        return new RoleDomain(ent.getId(), ent.getNombre(), ent.getDescripcion());
    }
}
