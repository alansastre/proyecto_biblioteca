package com.certidevs.repository;

import com.certidevs.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Book}.
 *
 * <p>Combina <strong>derived queries</strong> (métodos generados por nombre) con una
 * <strong>consulta JPQL personalizada</strong> ({@code @Query}) para la búsqueda por categoría.</p>
 *
 * <h3>Derived queries vs @Query:</h3>
 * <ul>
 *   <li>Derived queries: Spring genera la consulta a partir del nombre del método. Ideal para consultas simples.</li>
 *   <li>{@code @Query}: se escribe JPQL manualmente. Necesario para consultas complejas como JOINs explicitos.</li>
 * </ul>
 *
 * @see JpaRepository
 * @see Book
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Variante para listados: precarga el autor para que Thymeleaf pueda renderizarlo
     * con {@code spring.jpa.open-in-view=false}.
     */
    @EntityGraph(attributePaths = "author")
    @Query("SELECT b FROM Book b ORDER BY b.title ASC")
    List<Book> findAllForList();

    /**
     * Busca libros cuyo título contenga el texto dado (case-insensitive).
     *
     * @param title texto a buscar en el título
     * @return lista de libros que coinciden
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Variante para listados filtrados por título con el autor precargado.
     */
    @EntityGraph(attributePaths = "author")
    List<Book> findByTitleContainingIgnoreCaseOrderByTitleAsc(String title);

    /**
     * Busca libros por el ID de su autor.
     * Spring Data navega la relacion {@code book.author.id} automáticamente.
     *
     * @param authorId ID del autor
     * @return lista de libros de ese autor
     */
    List<Book> findByAuthorId(Long authorId);

    /**
     * Variante para listados filtrados por autor con el propio autor precargado.
     */
    @EntityGraph(attributePaths = "author")
    List<Book> findByAuthorIdOrderByTitleAsc(Long authorId);

    /**
     * Busca libros cuyo precio este en un rango.
     * Genera: {@code WHERE b.price BETWEEN ?1 AND ?2}
     *
     * @param min precio minimo (inclusive)
     * @param max precio máximo (inclusive)
     * @return lista de libros en ese rango de precio
     */
    List<Book> findByPriceBetween(Double min, Double max);

    /**
     * Variante para listados filtrados por precio con el autor precargado.
     */
    @EntityGraph(attributePaths = "author")
    List<Book> findByPriceBetweenOrderByTitleAsc(Double min, Double max);

    /**
     * Busca libros que pertenezcan a una categoría especifica.
     *
     * <p>Requiere {@code @Query} con JPQL porque necesita un JOIN explicito
     * a través de la relacion ManyToMany {@code book.categories}.
     * {@code DISTINCT} evita duplicados cuando un libro tiene múltiples categorías.</p>
     *
     * @param categoryId ID de la categoría
     * @return lista de libros en esa categoría
     */
    @Query("SELECT DISTINCT b FROM Book b JOIN b.categories c WHERE c.id = :categoryId")
    List<Book> findByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Variante para listados por categoría con el autor ya cargado.
     */
    @EntityGraph(attributePaths = "author")
    @Query("SELECT DISTINCT b FROM Book b JOIN b.categories c WHERE c.id = :categoryId ORDER BY b.title ASC")
    List<Book> findByCategoryIdForList(@Param("categoryId") Long categoryId);

    /**
     * Carga el detalle del libro junto con autor y reseñas para evitar fallos LAZY
     * al renderizar la vista de detalle con OSIV desactivado.
     */
    @Query("""
            SELECT DISTINCT b
            FROM Book b
            LEFT JOIN FETCH b.author
            LEFT JOIN FETCH b.reviews r
            LEFT JOIN FETCH r.user
            WHERE b.id = :id
            """)
    Optional<Book> findDetailedById(@Param("id") Long id);

    /**
     * Carga el libro junto con autor y categorías para reutilizar la plantilla del formulario
     * de edición sin acceder a asociaciones LAZY desde Thymeleaf.
     */
    @Query("""
            SELECT DISTINCT b
            FROM Book b
            LEFT JOIN FETCH b.author
            LEFT JOIN FETCH b.categories
            WHERE b.id = :id
            """)
    Optional<Book> findFormById(@Param("id") Long id);
}
