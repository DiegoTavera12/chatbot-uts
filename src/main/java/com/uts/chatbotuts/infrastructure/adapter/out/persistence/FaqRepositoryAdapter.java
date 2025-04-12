package com.uts.chatbotuts.infrastructure.adapter.out.persistence;

import com.uts.chatbotuts.application.port.out.FaqRepository;
import com.uts.chatbotuts.domain.model.FaqDomain;
import com.uts.chatbotuts.domain.model.PalabraClave;
import com.uts.chatbotuts.infrastructure.entity.FaqEntity;
import com.uts.chatbotuts.infrastructure.persistence.repository.FaqJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class FaqRepositoryAdapter implements FaqRepository {

    private final FaqJpaRepository faqJpaRepository;

    private static final Logger LOG = LoggerFactory.getLogger(FaqRepositoryAdapter.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public FaqDomain save(FaqDomain faqDomain) {
        // Mapear del dominio a entidad
        FaqEntity entity = mapToEntity(faqDomain);
        FaqEntity saved = faqJpaRepository.save(entity);
        return mapToDomain(saved);
    }

    @Transactional
    @Override
    public List<FaqDomain> findAll() {
        List<FaqEntity> entities = faqJpaRepository.findAll();
        return entities.stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    private FaqEntity mapToEntity(FaqDomain faqDomain) {
        FaqEntity entity = new FaqEntity();
        entity.setId(faqDomain.getId());
        entity.setPregunta(faqDomain.getPregunta());
        entity.setRespuesta(faqDomain.getRespuesta());
        entity.setCreatedAt(faqDomain.getCreatedAt());
        entity.setUpdatedAt(faqDomain.getUpdatedAt());
        entity.setEstado(faqDomain.getEstado());
        entity.setVecesUsada(faqDomain.getVecesUsada());
        return entity;
    }

    private FaqDomain mapToDomain(FaqEntity entity) {
        FaqDomain faqDomain = new FaqDomain();
        faqDomain.setId(entity.getId());
        faqDomain.setPregunta(entity.getPregunta());
        faqDomain.setRespuesta(entity.getRespuesta());
        faqDomain.setCreatedAt(entity.getCreatedAt());
        faqDomain.setUpdatedAt(entity.getUpdatedAt());
        return faqDomain;
    }

    @Transactional
    @Override
    public List<FaqDomain> findByIds(List<Long> idsFaq) {
        List<FaqEntity> entities = faqJpaRepository.findByIdIn(idsFaq);
        return entities.stream()
                .map(entity -> {
                    FaqDomain faqDomain = new FaqDomain();
                    faqDomain.setId(entity.getId());
                    faqDomain.setPregunta(entity.getPregunta());
                    faqDomain.setRespuesta(entity.getRespuesta());
                    faqDomain.setCreatedAt(entity.getCreatedAt());
                    faqDomain.setUpdatedAt(entity.getUpdatedAt());
                    return faqDomain;
                })
                .collect(Collectors.toList());
    }

    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_UPDATED_AT = "updatedAt";
    private static final String FIELD_PALABRAS_CLAVES = "palabrasClaveString";

    /**
     * Cuenta la cantidad de faqs que cumplen con los filtros.
     *
     * @param filters Filtros para la consulta.
     * @return Número total de faqs que cumplen los filtros.
     */
    @Override
    public int countByFilters(Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<FaqEntity> root = cq.from(FaqEntity.class);

        // Usa countDistinct para evitar duplicados a causa de los joins
        cq.select(cb.countDistinct(root));

        List<Predicate> predicates = new ArrayList<>();
        // Se realiza LEFT JOIN para poder buscar en palabras claves
        Join<FaqEntity, ?> joinPalabrasClave = root.join("palabrasClaves", JoinType.LEFT);

        // Manejo del filtro global: se aplica a campos de tipo String
        if (filters != null && filters.containsKey("globalFilter")) {
            Object globalValue = filters.get("globalFilter");
            if (globalValue != null && !globalValue.toString().trim().isEmpty()) {
                String search = "%" + globalValue.toString().toLowerCase() + "%";
                Predicate globalPredicate = cb.or(
                        cb.like(cb.lower(root.get("pregunta").as(String.class)), search),
                        cb.like(cb.lower(root.get("respuesta").as(String.class)), search),
                        cb.like(cb.lower(joinPalabrasClave.get("palabra").as(String.class)), search)
                );
                predicates.add(globalPredicate);
            }
            // Se elimina para que no sea procesado nuevamente en buildPredicate
            filters.remove("globalFilter");
        }

        // Filtros específicos utilizando el método auxiliar buildPredicate
        Predicate specificPredicate = buildPredicate(filters, cb, root);
        if (specificPredicate != null) {
            predicates.add(specificPredicate);
        }
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        Long count = entityManager.createQuery(cq).getSingleResult();
        return count.intValue();
    }


    @Override
    public List<FaqDomain> findByFilters(int first, int pageSize, String sortField, boolean ascending, Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<FaqEntity> cq = cb.createQuery(FaqEntity.class);
        Root<FaqEntity> root = cq.from(FaqEntity.class);

        // Realiza LEFT JOIN para obtener la información de las palabras claves
        Join<FaqEntity, ?> joinPalabrasClave = root.join("palabrasClaves", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        // Filtro global: se aplica a campos de tipo String
        if (filters != null && filters.containsKey("globalFilter")) {
            Object globalValue = filters.remove("globalFilter");
            if (globalValue != null && !globalValue.toString().trim().isEmpty()) {
                String search = "%" + globalValue.toString().toLowerCase() + "%";
                Predicate globalPredicate = cb.or(
                        cb.like(cb.lower(root.get("pregunta").as(String.class)), search),
                        cb.like(cb.lower(root.get("respuesta").as(String.class)), search),
                        cb.like(cb.lower(joinPalabrasClave.get("palabra").as(String.class)), search)
                );
                predicates.add(globalPredicate);
            }
        }

        // Filtros específicos utilizando el método auxiliar buildPredicate
        Predicate specificPredicates = buildPredicate(filters, cb, root);
        if (specificPredicates != null) {
            predicates.add(specificPredicates);
        }
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // Ordenamiento: si se ordena por palabras claves se utiliza el join
        if (sortField != null && !sortField.isEmpty()) {
            if (FIELD_PALABRAS_CLAVES.equals(sortField)) {
                cq.orderBy(ascending
                        ? cb.asc(cb.lower(joinPalabrasClave.get("palabra").as(String.class)))
                        : cb.desc(cb.lower(joinPalabrasClave.get("palabra").as(String.class))));
            } else {
                cq.orderBy(ascending
                        ? cb.asc(root.get(sortField))
                        : cb.desc(root.get(sortField)));
            }
        }
        cq.distinct(true);

        TypedQuery<FaqEntity> query = entityManager.createQuery(cq);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);
        List<FaqEntity> resultList = query.getResultList();

        // Se mapea la entidad a DTO para devolver la respuesta
        return resultList.stream()
                .map(FaqRepositoryAdapter::toDto)
                .collect(Collectors.toList());
    }

    private Predicate buildPredicate(Map<String, Object> filters, CriteriaBuilder cb, Root<FaqEntity> root) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        List<Predicate> predicates = new ArrayList<>();
        Join<FaqEntity, ?> joinPalabrasClave = null;

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            if (value == null || value.toString().trim().isEmpty()) {
                continue;
            }

            // Manejo de filtros de fecha para campos createdAt y updatedAt
            if (FIELD_CREATED_AT.equals(field) || FIELD_UPDATED_AT.equals(field)) {
                if (value instanceof List<?> rangeList) {
                    if (rangeList.size() == 2) {
                        Instant start = rangeList.get(0) != null ? (Instant) rangeList.get(0) : null;
                        Instant end = rangeList.get(1) != null ? (Instant) rangeList.get(1) : null;
                        if (start != null && end != null) {
                            predicates.add(cb.between(root.get(field), start, end));
                        } else if (start != null) {
                            predicates.add(cb.greaterThanOrEqualTo(root.get(field), start));
                        } else if (end != null) {
                            predicates.add(cb.lessThanOrEqualTo(root.get(field), end));
                        }
                    }
                }
            }
            // Filtro por palabras claves
            else if (FIELD_PALABRAS_CLAVES.equals(field)) {
                if (joinPalabrasClave == null) {
                    joinPalabrasClave = root.join("palabrasClaves", JoinType.LEFT);
                }
                predicates.add(cb.like(
                        cb.lower(joinPalabrasClave.get("palabra").as(String.class)),
                        "%" + value.toString().toLowerCase() + "%"
                ));
            }
            // Para otros campos: se distingue entre campos de texto, booleanos y otros tipos
            else {
                Class<?> javaType = root.get(field).getJavaType();
                if (javaType.equals(String.class)) {
                    predicates.add(cb.like(
                            cb.lower(root.get(field).as(String.class)),
                            "%" + value.toString().toLowerCase() + "%"
                    ));
                } else if (javaType.equals(Boolean.class) || javaType.equals(boolean.class)) {
                    // Convertir explícitamente el valor a Boolean
                    Boolean booleanValue = Boolean.parseBoolean(value.toString());
                    predicates.add(cb.equal(root.get(field), booleanValue));
                } else {
                    predicates.add(cb.equal(root.get(field), value));
                }
            }
        }
        return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
    }

    /**
     * Mapea un objeto FaqEntity a su correspondiente FaqDomain.
     * Se incluye la lista de palabras clave, extrayendo la propiedad "palabra" de cada entidad asociada.
     *
     * @param faq Entidad de FAQ
     * @return Objeto FaqDomain con los datos mapeados
     */
    public static FaqDomain toDto(FaqEntity faq) {
        if (faq == null) {
            return null;
        }
        List<PalabraClave> palabrasClave = faq.getPalabrasClaves().stream()
                .map(palabraClaveEntity -> new PalabraClave(palabraClaveEntity.getId(), palabraClaveEntity.getPalabra()))
                .collect(Collectors.toList());

        return new FaqDomain(
                faq.getId(),
                faq.getPregunta(),
                faq.getRespuesta(),
                faq.getCreatedAt(),
                faq.getUpdatedAt(),
                palabrasClave,
                faq.getVecesUsada(),
                faq.getEstado(), false,
                palabrasClave.stream()
                        .map(PalabraClave::getPalabra)
                        .collect(Collectors.joining(", "))
        );
    }

    /**
     * Elimina en lote los faqs cuyos IDs coincidan con los proporcionados.
     *
     * @param ids Lista de IDs de los faqs a eliminar.
     */
    @Transactional
    @Override
    public void eliminarFaqs(List<Long> ids) {
        // Se obtienen las entidades a eliminar
        List<FaqEntity> faqEntityList = faqJpaRepository.findAllById(ids);
        if (faqEntityList.isEmpty()) {
            // Se registra el error y se lanza una excepción personalizada para manejar el caso en la capa superior.
            LOG.warn("No se encontraron usuarios con los ids proporcionados: {}", ids);
            throw new UsuarioRepositoryAdapter.UsuarioNotFoundException("No se encontraron usuarios con los ids proporcionados");
        }
        // Se eliminan las entidades en lote
        faqJpaRepository.deleteAll(faqEntityList);
    }

    /**
     * Método de conveniencia para eliminar un solo faq.
     *
     * @param id ID del faq a eliminar.
     */
    @Transactional
    @Override
    public void eliminarFaq(Long id) {
        eliminarFaqs(List.of(id));
    }

    @Transactional
    @Override
    public void actualzarUsoFaq(Long id) {
        faqJpaRepository.incrementUso(id);
    }

    @Override
    public  FaqDomain findById(Long id) {
        return toDto(faqJpaRepository.findById(id).orElse(null));
    }
}
