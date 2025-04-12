package com.uts.chatbotuts.infrastructure.persistence.repository;

import com.uts.chatbotuts.infrastructure.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
    //Optional<UsuarioEntity> findByUsername(String username);
    Optional<UsuarioEntity> findByCorreoElectronico(String correoElectronico);

    Optional<UsuarioEntity> findByNombre(String nombre);

    boolean existsByDocumentoIdentificacionAndRol_Id(String documentoIdentificacion, Long rolId);

    boolean existsByDocumentoIdentificacion(String documentoIdentificacion);


}