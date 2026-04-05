package com.certidevs.repository;

import com.certidevs.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Category}.
 *
 * @see JpaRepository
 * @see Category
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Busca categorías cuyo nombre contenga el texto dado (case-insensitive).
     *
     * @param name texto a buscar en el nombre
     * @return lista de categorías que coinciden
     */
    List<Category> findByNameContainingIgnoreCase(String name);

    /**
     * Carga la categoría junto con sus libros para la pantalla de detalle.
     */
    @EntityGraph(attributePaths = "books")
    Optional<Category> findDetailedById(Long id);
}
