package com.uts.chatbotuts.infrastructure.adapter.out.persistence;

import com.uts.chatbotuts.application.port.out.UsuarioRepository;
import com.uts.chatbotuts.domain.model.RoleDomain;
import com.uts.chatbotuts.domain.model.UsuarioDomain;
import com.uts.chatbotuts.infrastructure.entity.RoleEntity;
import com.uts.chatbotuts.infrastructure.entity.UsuarioEntity;
import com.uts.chatbotuts.infrastructure.persistence.repository.UsuarioJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Para registrar logs
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    // Uso de SLF4J para logging en lugar de simplemente lanzar excepciones
    private static final Logger LOG = LoggerFactory.getLogger(UsuarioRepositoryAdapter.class);

    // Constantes para evitar errores de tipeo en nombres de campos
    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_UPDATED_AT = "updatedAt";
    private static final String FIELD_NOMBRE_ROLE = "role.descripcion";

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Registra o actualiza un usuario en la base de datos.
     * Si se envía un ID existente, se actualiza; de lo contrario, se crea uno nuevo.
     *
     * @param usuario Objeto de dominio con los datos del usuario.
     * @return Objeto UsuarioDomain con los datos guardados.
     */
    @Transactional
    @Override
    public UsuarioDomain registrarOActualizarUsuario(UsuarioDomain usuario) {
        try {
            UsuarioEntity usuarioEntity;
            boolean isNew = false;

            // Verifica si se provee un ID para actualizar o se creará uno nuevo.
            if (usuario.getId() != null && usuarioJpaRepository.existsById(usuario.getId())) {
                usuarioEntity = usuarioJpaRepository.findById(usuario.getId())
                        .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
            } else {
                usuarioEntity = new UsuarioEntity();
                usuarioEntity.setCreatedAt(Instant.now());
                isNew = true;
            }

            // Validaciones básicas
            if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
                throw new UsuarioPersistenceException("El nombre del usuario es obligatorio");
            }
            if (usuario.getDocumentoIdentificacion() == null || usuario.getDocumentoIdentificacion().trim().isEmpty()) {
                throw new UsuarioPersistenceException("El documento de identificación es obligatorio");
            }
            // Validación del rol (nuevo, ya que cada usuario debe tener un único rol)
            if (usuario.getRole() == null || usuario.getRole().getId() < 0) {
                throw new UsuarioPersistenceException("El rol del usuario es obligatorio");
            }

            // Actualización de campos
            usuarioEntity.setNombre(usuario.getNombre());
            usuarioEntity.setApellido(usuario.getApellido());
            usuarioEntity.setCorreoElectronico(usuario.getCorreoElectronico());
            usuarioEntity.setDocumentoIdentificacion(usuario.getDocumentoIdentificacion());
            usuarioEntity.setDocumentoEstudiante(usuario.getDocumentoEstudiante());
            usuarioEntity.setEstado(usuario.getEstado());
            usuarioEntity.setUpdatedAt(Instant.now());

            // Asigna el rol utilizando la llave foránea (se utiliza entityManager para obtener una referencia)
            usuarioEntity.setRol(entityManager.getReference(RoleEntity.class, usuario.getRole().getId()));

            // Manejo de la contraseña:
            // - Si el parámetro trae contraseña, se encripta y se asigna.
            // - Si viene vacía y es un usuario nuevo, se asigna por defecto "user".
            // - Si viene vacía y el usuario ya existe, se conserva la contraseña actual.
            if (usuario.getContrasena() != null && !usuario.getContrasena().trim().isEmpty()) {
                if (Objects.isNull(usuarioEntity.getContrasena()) || !usuarioEntity.getContrasena().equals(usuario.getContrasena())) {
                    usuarioEntity.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
                }
            } else if (isNew) {
                usuarioEntity.setContrasena(passwordEncoder.encode("user"));
            }
            // Para usuarios existentes con contraseña vacía en el parámetro, se conserva la contraseña ya guardada

            // Se guarda la entidad (creación o actualización)
            usuarioEntity = usuarioJpaRepository.save(usuarioEntity);
            return toDto(usuarioEntity);

        } catch (Exception ex) {
            LOG.error("Error al registrar o actualizar usuario", ex);
            throw new UsuarioPersistenceException("Error al registrar o actualizar usuario", ex);
        }
    }


    /**
     * Método auxiliar para generar la contraseña concatenando:
     * - Los últimos 3 caracteres del documento de identificación.
     * - Los primeros 3 (o menos) caracteres del nombre.
     * - Los últimos 3 (o menos) caracteres del apellido.
     *
     * Ejemplo: Si el documento es "123456", el nombre "Andrés" y el apellido "Gómez", la contraseña sería "456Andmez".
     *
     * @param usuario Datos del usuario
     * @return Contraseña generada en base a la información del usuario.
     */
    private String generarPassword(UsuarioDomain usuario) {
        // Obtener últimos 3 caracteres del documento de identificación
        String docIdentificacion = usuario.getDocumentoIdentificacion();
        String ultimos3Identificacion = docIdentificacion.length() >= 3
                ? docIdentificacion.substring(docIdentificacion.length() - 3)
                : docIdentificacion;

        // Obtener los primeros 3 (o menos) del nombre
        String nombre = usuario.getNombre();
        String primeros3Nombre = nombre.length() >= 3
                ? nombre.substring(0, 3)
                : nombre;

        // Obtener los últimos 3 (o menos) del apellido
        String apellido = usuario.getApellido();
        String ultimos3Apellido = apellido.length() >= 3
                ? apellido.substring(apellido.length() - 3)
                : apellido;

        // Se concatena la información para formar la contraseña
        return ultimos3Identificacion + primeros3Nombre + ultimos3Apellido;
    }

    /**
     * Mapea un objeto UsuarioEntity a su correspondiente UsuarioDomain.
     * Se incluye el nombre del rol, obteniendo el primer rol asociado.
     *
     * @param usuario Entidad de usuario
     * @return Objeto UsuarioDomain con los datos mapeados
     */
    public static UsuarioDomain toDto(UsuarioEntity usuario) {
        if (usuario == null) {
            return null;
        }

        RoleDomain role = new RoleDomain(usuario.getRol().getId(), usuario.getRol().getNombre(), usuario.getRol().getDescripcion());

        return new UsuarioDomain(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getDocumentoIdentificacion(),
                usuario.getDocumentoEstudiante(),
                usuario.getCorreoElectronico(),
                usuario.getEstado(),
                usuario.getCreatedAt(),
                usuario.getUpdatedAt(),
                usuario.getContrasena(),
                role
        );
    }

    /**
     * Cuenta la cantidad de usuarios que cumplen con los filtros.
     *
     * @param filters Filtros para la consulta.
     * @return Número total de usuarios que cumplen los filtros.
     */
    @Override
    public int countByFilters(Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<UsuarioEntity> root = cq.from(UsuarioEntity.class);
        cq.select(cb.countDistinct(root));

        List<Predicate> predicates = new ArrayList<>();

        // Join directo sobre "rol"
        Join<UsuarioEntity, RoleEntity> joinRole = root.join("rol", JoinType.LEFT);

        if (filters != null && filters.containsKey("globalFilter")) {
            Object globalValue = filters.get("globalFilter");
            if (globalValue != null && !globalValue.toString().trim().isEmpty()) {
                String search = "%" + globalValue.toString().toLowerCase() + "%";
                Predicate globalPredicate = cb.or(
                        cb.like(cb.lower(root.get("nombre").as(String.class)), search),
                        cb.like(cb.lower(root.get("apellido").as(String.class)), search),
                        cb.like(cb.lower(root.get("documentoIdentificacion").as(String.class)), search),
                        cb.like(cb.lower(root.get("correoElectronico").as(String.class)), search),
                        cb.like(cb.lower(joinRole.get("descripcion").as(String.class)), search)
                );
                predicates.add(globalPredicate);
            }
            filters.remove("globalFilter");
        }

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


    /**
     * Busca usuarios aplicando filtros, ordenamiento y paginación.
     *
     * @param first     Indica el primer registro a recuperar.
     * @param pageSize  Número de registros a recuperar.
     * @param sortField Campo para ordenar.
     * @param ascending Orden ascendente o descendente.
     * @param filters   Mapa de filtros a aplicar.
     * @return Lista de UsuarioDomain que cumplen con los filtros.
     */
    @Override
    @Transactional
    public List<UsuarioDomain> findByFilters(int first, int pageSize, String sortField, boolean ascending, Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UsuarioEntity> cq = cb.createQuery(UsuarioEntity.class);
        Root<UsuarioEntity> root = cq.from(UsuarioEntity.class);

        // Join directo sobre "rol"
        Join<UsuarioEntity, RoleEntity> joinRole = root.join("rol", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (filters != null && filters.containsKey("globalFilter")) {
            Object globalValue = filters.remove("globalFilter");
            if (globalValue != null && !globalValue.toString().trim().isEmpty()) {
                String search = "%" + globalValue.toString().toLowerCase() + "%";
                Predicate globalPredicate = cb.or(
                        cb.like(cb.lower(root.get("nombre").as(String.class)), search),
                        cb.like(cb.lower(root.get("apellido").as(String.class)), search),
                        cb.like(cb.lower(root.get("documentoIdentificacion").as(String.class)), search),
                        cb.like(cb.lower(root.get("correoElectronico").as(String.class)), search),
                        cb.like(cb.lower(joinRole.get("descripcion").as(String.class)), search)
                );
                predicates.add(globalPredicate);
            }
        }

        Predicate specificPredicates = buildPredicate(filters, cb, root);
        if (specificPredicates != null) {
            predicates.add(specificPredicates);
        }
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // Ordenamiento: si se ordena por rol, usar la propiedad "descripcion" del rol
        if (sortField != null && !sortField.isEmpty()) {
            if (FIELD_NOMBRE_ROLE.equals(sortField)) {
                cq.orderBy(ascending
                        ? cb.asc(cb.lower(joinRole.get("descripcion")))
                        : cb.desc(cb.lower(joinRole.get("descripcion"))));
            } else {
                cq.orderBy(ascending
                        ? cb.asc(root.get(sortField))
                        : cb.desc(root.get(sortField)));
            }
        }

        TypedQuery<UsuarioEntity> query = entityManager.createQuery(cq);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);
        List<UsuarioEntity> resultList = query.getResultList();

        return resultList.stream()
                .map(UsuarioRepositoryAdapter::toDto)
                .collect(Collectors.toList());
    }


    /**
     * Método auxiliar que construye un Predicate a partir de los filtros proporcionados.
     * Separa la lógica para filtros de fecha, filtro por rol y demás campos.
     *
     * Ejemplo: Si se pasa un filtro con "createdAt" como una lista de dos instantes,
     * se aplicará un "between" en la consulta.
     *
     * @param filters Mapa de filtros.
     * @param cb      CriteriaBuilder para construir la consulta.
     * @param root    Raíz de la entidad en la consulta.
     * @return Objeto Predicate con la condición o null si no hay filtros.
     */
    private Predicate buildPredicate(Map<String, Object> filters, CriteriaBuilder cb, Root<UsuarioEntity> root) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        List<Predicate> predicates = new ArrayList<>();
        Join<UsuarioEntity, RoleEntity> joinRole = null;

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            if (value == null || value.toString().trim().isEmpty()) {
                continue;
            }

            // Filtros de fecha
            if (FIELD_CREATED_AT.equals(field) || FIELD_UPDATED_AT.equals(field)) {
                if (value instanceof List<?> rangeList && rangeList.size() == 2) {
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
            // Filtro por rol
            else if (FIELD_NOMBRE_ROLE.equals(field)) {
                if (joinRole == null) {
                    joinRole = root.join("rol", JoinType.LEFT);
                }
                predicates.add(cb.like(
                        cb.lower(joinRole.get("descripcion").as(String.class)),
                        "%" + value.toString().toLowerCase() + "%"
                ));
            }
            // Otros campos
            else {
                Class<?> javaType = root.get(field).getJavaType();
                if (javaType.equals(String.class)) {
                    predicates.add(cb.like(
                            cb.lower(root.get(field).as(String.class)),
                            "%" + value.toString().toLowerCase() + "%"
                    ));
                } else if (javaType.equals(Boolean.class) || javaType.equals(boolean.class)) {
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
     * Elimina en lote los usuarios cuyos IDs coincidan con los proporcionados.
     *
     * @param ids Lista de IDs de los usuarios a eliminar.
     */
    @Transactional
    @Override
    public void eliminarUsuarios(List<Long> ids) {
        // Se obtienen las entidades a eliminar
        List<UsuarioEntity> usuarios = usuarioJpaRepository.findAllById(ids);
        if (usuarios.isEmpty()) {
            LOG.warn("No se encontraron usuarios con los ids proporcionados: {}", ids);
            throw new UsuarioNotFoundException("No se encontraron usuarios con los ids proporcionados");
        }
        usuarioJpaRepository.deleteAll(usuarios);
    }

    /**
     * Método de conveniencia para eliminar un solo usuario.
     *
     * @param id ID del usuario a eliminar.
     */
    @Transactional
    @Override
    public void eliminarUsuario(Long id) {
        eliminarUsuarios(List.of(id));
    }

    public static class UsuarioNotFoundException extends RuntimeException {
        public UsuarioNotFoundException(String message) {
            super(message);
        }
    }

    public static class UsuarioPersistenceException extends RuntimeException {
        public UsuarioPersistenceException(String message) {
            super(message);
        }

        public UsuarioPersistenceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
