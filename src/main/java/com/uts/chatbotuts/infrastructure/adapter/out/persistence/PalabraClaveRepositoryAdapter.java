package com.uts.chatbotuts.infrastructure.adapter.out.persistence;

import com.uts.chatbotuts.application.port.out.PalabraClaveRepository;
import com.uts.chatbotuts.domain.model.PalabraClave;
import com.uts.chatbotuts.infrastructure.adapter.out.FuzzyMatcherService;
import com.uts.chatbotuts.infrastructure.adapter.out.NlpService;
import com.uts.chatbotuts.infrastructure.entity.PalabrasClaveEntity;
import com.uts.chatbotuts.infrastructure.persistence.repository.PalabrasClaveJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class PalabraClaveRepositoryAdapter implements PalabraClaveRepository {

    private final PalabrasClaveJpaRepository repository;
    private NlpService nlpService;
    private FuzzyMatcherService fuzzyMatcherService;

    private static final double SIMILARITY_THRESHOLD = 0.85;


    @Transactional
    @Override
    public PalabraClave save(PalabraClave palabraClave) {
        try {
            // Si se proporciona un ID, se intenta recuperar la entidad existente para actualizarla
            if (palabraClave.getId() != null) {
                Optional<PalabrasClaveEntity> entidadExistenteOpt = repository.findById(palabraClave.getId());
                if (entidadExistenteOpt.isPresent()) {
                    // Se encontró la entidad, se actualizan los campos necesarios
                    PalabrasClaveEntity entidadExistente = entidadExistenteOpt.get();
                    // Actualizamos la palabra solo si es diferente
                    if (!entidadExistente.getPalabra().equals(palabraClave.getPalabra())) {
                        entidadExistente.setPalabra(palabraClave.getPalabra());
                        entidadExistente = repository.save(entidadExistente);
                    }
                    return new PalabraClave(entidadExistente.getId(), entidadExistente.getPalabra());
                }
                // Si el ID fue proporcionado pero no se encontró la entidad, se procede a crear una nueva.
            }

            // Si no se proporciona un ID o no se encontró la entidad por ID,
            // se verifica si la palabra ya existe en la base de datos
            Optional<PalabrasClaveEntity> entidadPorPalabraOpt = repository.findByPalabra(palabraClave.getPalabra());
            if (entidadPorPalabraOpt.isPresent()) {
                // La palabra ya existe, se retorna la entidad encontrada
                PalabrasClaveEntity entidadExistente = entidadPorPalabraOpt.get();
                // Es posible registrar o notificar que la palabra ya estaba almacenada
                System.out.println("----La palabra clave ya existe----");
                return new PalabraClave(entidadExistente.getId(), entidadExistente.getPalabra());
            } else {
                // La palabra no existe, se crea una nueva entidad
                PalabrasClaveEntity nuevaEntidad = new PalabrasClaveEntity();
                nuevaEntidad.setPalabra(palabraClave.getPalabra());
                nuevaEntidad = repository.save(nuevaEntidad);
                return new PalabraClave(nuevaEntidad.getId(), nuevaEntidad.getPalabra());
            }
        } catch (Exception e) {
            // Se captura cualquier excepción, se registra el error y se relanza la excepción con la causa original.
            System.err.println("Error guardando Palabras Clave: " + e.getMessage());
            throw new RuntimeException("Error guardando Palabras Clave", e);
        }
    }

    @Transactional
    @Override
    public List<Long> findIdsBySimilarPalabras(String[] palabrasClave) {

        try {


            this.nlpService = new NlpService();
            this.fuzzyMatcherService = new FuzzyMatcherService();

            // Recuperar todas las palabras clave configuradas desde la base de datos
            List<PalabrasClaveEntity> allKeywords = repository.findAll();
            List<Long> matchingKeywordIds = new ArrayList<>();

            // Iterar sobre cada token (palabra extraída de la entrada del usuario)
            for (String token : palabrasClave) {
                String tokenLower = token.toLowerCase();
                for (PalabrasClaveEntity keyword : allKeywords) {
                    // Convertir la palabra clave a minúsculas para la comparación
                    String keywordName = keyword.getPalabra().toLowerCase();
                    double similarity = fuzzyMatcherService.getSimilarity(tokenLower, keywordName);
                    // Si la similitud supera el umbral, se agrega el ID de la palabra clave
                    if (similarity >= SIMILARITY_THRESHOLD) {
                        matchingKeywordIds.add(keyword.getId());
                    }
                }
            }
            return matchingKeywordIds;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    @Override
    public List<PalabraClave> buscarPalabrasClavesByIds(List<Long> ids) {

        List<PalabrasClaveEntity> listEnt = repository.findByIdIn(ids);

        List<PalabraClave> palabraClaveList = new ArrayList<>();
        for (PalabrasClaveEntity p : listEnt) {
            PalabraClave palabraClave = new PalabraClave(p.getId(), p.getPalabra());

            palabraClaveList.add(palabraClave);

        }

        return palabraClaveList;
    }

    @Override
    public PalabraClave obtenerPorId(Long id) {
        Optional<PalabrasClaveEntity> entityOpt = repository.findById(id);
        return entityOpt.map(this::convertToDomain).orElse(null);
    }

    @Override
    public PalabraClave buscarPorPalabra(String palabra) {
        Optional<PalabrasClaveEntity> entityOpt = repository.findByPalabra(palabra);
        return entityOpt.map(this::convertToDomain).orElse(null);
    }
    private PalabraClave convertToDomain(PalabrasClaveEntity entity) {
        PalabraClave domain = new PalabraClave();
        domain.setId(entity.getId());
        domain.setPalabra(entity.getPalabra());
        return domain;
    }
}
