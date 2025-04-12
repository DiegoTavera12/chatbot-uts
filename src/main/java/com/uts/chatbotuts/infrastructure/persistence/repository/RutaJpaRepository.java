package com.uts.chatbotuts.infrastructure.persistence.repository;

import com.uts.chatbotuts.infrastructure.entity.RutaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RutaJpaRepository extends CrudRepository<RutaEntity, Long> {
    Optional<RutaEntity> findByRutaUrl(String rutaUrl);
}
