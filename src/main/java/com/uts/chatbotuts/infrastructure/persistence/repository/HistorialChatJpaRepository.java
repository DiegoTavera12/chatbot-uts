package com.uts.chatbotuts.infrastructure.persistence.repository;

import com.uts.chatbotuts.infrastructure.entity.HistorialChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialChatJpaRepository extends JpaRepository<HistorialChatEntity, Long> {
}