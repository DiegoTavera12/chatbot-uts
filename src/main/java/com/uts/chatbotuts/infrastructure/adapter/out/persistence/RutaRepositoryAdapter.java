package com.uts.chatbotuts.infrastructure.adapter.out.persistence;

import com.uts.chatbotuts.domain.model.RoleDomain;
import com.uts.chatbotuts.domain.model.Ruta;
import com.uts.chatbotuts.application.port.out.RutaRepository;
import com.uts.chatbotuts.infrastructure.entity.RutaEntity;
import com.uts.chatbotuts.infrastructure.persistence.repository.RutaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RutaRepositoryAdapter implements RutaRepository {

    private final RutaJpaRepository rutaJpaRepository;

    public RutaRepositoryAdapter(RutaJpaRepository rutaJpaRepository) {
        this.rutaJpaRepository = rutaJpaRepository;
    }

    @Override
    public List<Ruta> findAll() {
        List<RutaEntity> rutaEntities = (List<RutaEntity>) rutaJpaRepository.findAll();
        return rutaEntities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Ruta toDomain(RutaEntity entity) {
        Ruta ruta = new Ruta();
        ruta.setRutaUrl(entity.getRutaUrl());
        // Conversión de las entidades de Role a objetos de dominio
        List<RoleDomain> roleDomains = entity.getRoles().stream()
                .map(roleEntity -> new RoleDomain(roleEntity.getId(), roleEntity.getNombre(), roleEntity.getDescripcion()))
                .collect(Collectors.toList());
        ruta.setRoleDomains(roleDomains);
        return ruta;
    }
}