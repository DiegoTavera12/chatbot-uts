package com.uts.chatbotuts.infrastructure.persistence.repository;

import com.uts.chatbotuts.infrastructure.entity.FaqEntity;
import com.uts.chatbotuts.infrastructure.entity.PalabrasClaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FaqJpaRepository extends JpaRepository<FaqEntity, Long> {

    List<FaqEntity> findByIdIn(List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE FaqEntity f SET f.vecesUsada = f.vecesUsada + 1 WHERE f.id = :id")
    int incrementUso(@Param("id") Long id);
}
