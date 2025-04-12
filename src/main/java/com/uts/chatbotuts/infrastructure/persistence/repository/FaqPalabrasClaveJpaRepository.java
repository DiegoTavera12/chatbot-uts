package com.uts.chatbotuts.infrastructure.persistence.repository;

import com.uts.chatbotuts.infrastructure.entity.FaqEntity;
import com.uts.chatbotuts.infrastructure.entity.FaqPalabrasClaveEntity;
import com.uts.chatbotuts.infrastructure.entity.FaqPalabrasClaveIdEntity;
import com.uts.chatbotuts.infrastructure.entity.PalabrasClaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FaqPalabrasClaveJpaRepository extends JpaRepository<FaqPalabrasClaveEntity, FaqPalabrasClaveIdEntity> {

    boolean existsByIdFaqEntityAndIdPalabra(FaqEntity idFaqEntity, PalabrasClaveEntity idPalabra);

    // Método derivado para eliminar la asociación entre una FAQ y una Palabra Clave.
    void deleteByIdFaqEntityAndIdPalabra(FaqEntity faqEntity, PalabrasClaveEntity palabraEntity);

    @Query("SELECT f.id.idPalabra FROM FaqPalabrasClaveEntity f WHERE f.idFaqEntity.id = :idFaq")
    List<Long> buscarIdsPalabrasClaveByFaqId(@Param("idFaq") Long idFaq);

    @Query("SELECT DISTINCT f.idFaqEntity.id FROM FaqPalabrasClaveEntity f WHERE f.idPalabra.id IN :idsPalabrasClave")
    List<Long> buscarFaqIdsByPalabrasClaveIds(@Param("idsPalabrasClave") List<Long> idsPalabrasClave);
}
