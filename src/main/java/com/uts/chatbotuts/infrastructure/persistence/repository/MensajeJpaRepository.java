package com.uts.chatbotuts.infrastructure.persistence.repository;

import com.uts.chatbotuts.infrastructure.entity.MensajeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeJpaRepository extends JpaRepository<MensajeEntity, Long> {
}