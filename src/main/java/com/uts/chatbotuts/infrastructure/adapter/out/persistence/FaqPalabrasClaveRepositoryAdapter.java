package com.uts.chatbotuts.infrastructure.adapter.out.persistence;

import com.uts.chatbotuts.application.port.out.FaqPalabrasClaveRepository;
import com.uts.chatbotuts.infrastructure.entity.FaqEntity;
import com.uts.chatbotuts.infrastructure.entity.FaqPalabrasClaveEntity;
import com.uts.chatbotuts.infrastructure.entity.FaqPalabrasClaveIdEntity;
import com.uts.chatbotuts.infrastructure.entity.PalabrasClaveEntity;
import com.uts.chatbotuts.infrastructure.persistence.repository.FaqJpaRepository;
import com.uts.chatbotuts.infrastructure.persistence.repository.FaqPalabrasClaveJpaRepository;
import com.uts.chatbotuts.infrastructure.persistence.repository.PalabrasClaveJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class FaqPalabrasClaveRepositoryAdapter implements FaqPalabrasClaveRepository {

    private static final Logger logger = LoggerFactory.getLogger(FaqPalabrasClaveRepositoryAdapter.class);


    private final FaqPalabrasClaveJpaRepository repository;
    private final FaqJpaRepository faqJpaRepository;
    private final PalabrasClaveJpaRepository palabrasClaveJpaRepository;


    @Transactional
    @Override
    public void guardarAsociacion(Long faqId, Long palabraClaveId) {
        try {
            logger.info("Iniciando el proceso para guardar la unión FAQ - Palabras Clave");

            // Crear la entidad de unión y su clave compuesta
            FaqPalabrasClaveEntity faqPalabrasClaveEntity = new FaqPalabrasClaveEntity();
            faqPalabrasClaveEntity.setId(new FaqPalabrasClaveIdEntity());
            logger.debug("Entidad de unión inicializada");

            // Validar y obtener la FAQ
            Optional<FaqEntity> optionalFaq = faqJpaRepository.findById(faqId);
            if (optionalFaq.isEmpty()) {
                logger.error("No se encontró la FAQ con id: {}", faqId);
                throw new RuntimeException("FAQ no encontrada");
            }
            FaqEntity faqEntity = optionalFaq.get();
            logger.info("FAQ encontrada: id={}", faqEntity.getId());

            Optional<PalabrasClaveEntity> optionalPalabrasClaveEntity = palabrasClaveJpaRepository.findById(palabraClaveId);

            if (optionalPalabrasClaveEntity.isEmpty()) {

                throw new RuntimeException("Palabra clave no encontrada, id: " + palabraClaveId);

            }


            // Asignar las entidades relacionadas a la unión
            faqPalabrasClaveEntity.setIdFaqEntity(faqEntity);
            faqPalabrasClaveEntity.setIdPalabra(optionalPalabrasClaveEntity.get());
            logger.debug("Asociación FAQ-Palabra Clave asignada a la entidad de unión");

            // Opcional: Verificar si ya existe la asociación (si es necesaria la validación)
            // Ejemplo:
            if (repository.existsByIdFaqEntityAndIdPalabra(faqEntity, optionalPalabrasClaveEntity.get())) {
                logger.warn("Ya existe la asociación entre FAQ id={} y Palabra Clave id={}", faqEntity.getId(), optionalPalabrasClaveEntity.get().getId());
                // Se puede retornar o actualizar según la lógica de negocio
            }

            repository.save(faqPalabrasClaveEntity);
            logger.info("Guardado exitoso de la asociación FAQ - Palabra Clave");

        } catch (Exception e) {
            logger.error("Error guardando la unión de FAQ con Palabras Clave: ", e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public List<Long> buscarPalabrasClaveIdsByFaqId(Long faqId) {  try {

        return repository.buscarIdsPalabrasClaveByFaqId(faqId);
    }catch (Exception e){
        logger.debug(e.getMessage());
    }
        return null;
    }

    @Transactional
    @Override
    public List<Long> buscarFaqIdsByPalabrasClaveIds(List<Long> idsPalabrasClave) {
        return repository.buscarFaqIdsByPalabrasClaveIds(idsPalabrasClave);
    }

    @Override
    @Transactional
    public void eliminarAsociacion(Long faqId, Long palabraClaveId) {
        try {
            Optional<FaqEntity> optionalFaq = faqJpaRepository.findById(faqId);
            Optional<PalabrasClaveEntity> optionalPalabra = palabrasClaveJpaRepository.findById(palabraClaveId);
            if (optionalFaq.isPresent() && optionalPalabra.isPresent()) {
                FaqEntity faqEntity = optionalFaq.get();
                PalabrasClaveEntity palabraEntity = optionalPalabra.get();
                // Se asume que el repositorio JPA tiene un método para eliminar la asociación según ambas entidades
                repository.deleteByIdFaqEntityAndIdPalabra(faqEntity, palabraEntity);
                logger.info("Se eliminó la asociación entre FAQ id={} y Palabra Clave id={}", faqId, palabraClaveId);
            } else {
                logger.warn("No se encontró la FAQ o la Palabra Clave para eliminar la asociación");
            }
        } catch (Exception e) {
            logger.error("Error eliminando la asociación: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeAsociacion(Long faqId, Long palabraClaveId) {
        Optional<FaqEntity> optionalFaq = faqJpaRepository.findById(faqId);
        Optional<PalabrasClaveEntity> optionalPalabra = palabrasClaveJpaRepository.findById(palabraClaveId);
        if (optionalFaq.isPresent() && optionalPalabra.isPresent()) {
            return repository.existsByIdFaqEntityAndIdPalabra(optionalFaq.get(), optionalPalabra.get());
        }
        return false;
    }
}
