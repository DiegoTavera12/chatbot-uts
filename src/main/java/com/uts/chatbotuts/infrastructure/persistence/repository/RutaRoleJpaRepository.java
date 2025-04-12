package com.uts.chatbotuts.infrastructure.persistence.repository;

import com.uts.chatbotuts.infrastructure.entity.RutaRoleEntity;
import org.springframework.data.repository.CrudRepository;

public interface RutaRoleJpaRepository extends CrudRepository<RutaRoleEntity, Long> {
    boolean existsByIdRuta_IdRuta(Long idRuta);

    boolean existsByIdRuta_IdRutaAndIdRol_Id(Long idRuta, Long idRol);


}
