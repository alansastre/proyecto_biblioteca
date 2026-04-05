package com.certidevs.repository;

import com.certidevs.model.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Author}.
 *
 * <p>Los métodos de búsqueda usan <strong>derived queries</strong> (consultas derivadas):
 * Spring Data JPA interpreta el nombre del método y genera la consulta SQL correspondiente.</p>
 *
 * <h3>Palabras clave usadas:</h3>
 * <ul>
 *   <li>{@code Containing} - equivale a {@code LIKE %valor%} en SQL</li>
 *   <li>{@code IgnoreCase} - búsqueda sin distinguir mayúsculas/minúsculas (case-insensitive)</li>
 * </ul>
 *
 * @see JpaRepository
 * @see Author
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * Busca autores cuyo nombre contenga el texto dado (case-insensitive).
     * Genera: {@code WHERE LOWER(a.name) LIKE LOWER('%name%')}
     *
     * @param name texto a buscar en el nombre
     * @return lista de autores que coinciden
     */
    List<Author> findByNameContainingIgnoreCase(String name);

    /**
     * Busca autores por nacionalidad exacta (case-insensitive).
     * Genera: {@code WHERE LOWER(a.nationality) = LOWER(?1)}
     *
     * @param nationality nacionalidad a buscar
     * @return lista de autores de esa nacionalidad
     */
    List<Author> findByNationalityIgnoreCase(String nationality);

    /**
     * Carga el autor junto con sus libros para la pantalla de detalle.
     */
    @EntityGraph(attributePaths = "books")
    Optional<Author> findDetailedById(Long id);
}
