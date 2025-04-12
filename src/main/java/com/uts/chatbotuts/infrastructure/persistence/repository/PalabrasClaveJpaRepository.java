package com.uts.chatbotuts.infrastructure.persistence.repository;

import com.uts.chatbotuts.infrastructure.entity.PalabrasClaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PalabrasClaveJpaRepository extends JpaRepository<PalabrasClaveEntity, Long> {

    Optional<PalabrasClaveEntity> findByPalabra(String palabra);

    boolean existsByPalabra(String palabra);

    /**
     * Busca en la tabla "palabras_claves" todas las entradas cuya columna "palabra"
     * contenga de forma similar el valor especificado. La búsqueda es insensible a:
     * <ul>
     *   <li>Mayúsculas y minúsculas: se utiliza la función {@code lower()} para normalizar el texto.</li>
     *   <li>Tildes y diacríticos: se utiliza la función {@code unaccent()} para eliminar acentos.</li>
     * </ul>
     *
     * <p>La consulta utiliza el operador {@code LIKE} junto con los comodines "%" para permitir
     * búsquedas parciales. Por ejemplo, si se busca "cafe", encontrará palabras como "café", "CAFETERÍA", etc.
     *
     * <p><b>Requisitos:</b> La función {@code unaccent()} es parte de una extensión de PostgreSQL, por lo que
     * es necesario que la extensión esté instalada y habilitada en la base de datos.
     *
     * @param palabra la cadena que se desea buscar, pudiendo ser una subcadena de la palabra completa.
     * @return una lista de {@link PalabrasClaveEntity} que contienen la subcadena especificada,
     *         ignorando diferencias de mayúsculas, minúsculas y tildes.
     */
    @Query(
            value = "SELECT * FROM palabras_claves " +
                    "WHERE lower(unaccent(palabra)) LIKE lower(unaccent(concat('%', :palabra, '%')))",
            nativeQuery = true
    )
    List<PalabrasClaveEntity> findByPalabraSimilar(@Param("palabra") String palabra);

    List<PalabrasClaveEntity> findByIdIn(List<Long> ids);


}