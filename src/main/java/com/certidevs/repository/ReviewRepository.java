package com.certidevs.repository;

import com.certidevs.model.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Review}.
 *
 * <p>Todos los métodos usan derived queries navegando las relaciones
 * {@code review.book.id}, {@code review.user.id} y el campo {@code review.rating}.</p>
 *
 * @see JpaRepository
 * @see Review
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Variante para listados: precarga libro y usuario para renderizar la tabla sin OSIV.
     */
    @EntityGraph(attributePaths = {"book", "user"})
    List<Review> findAllByOrderByCreatedAtDesc();

    /**
     * Busca todas las reseñas de un libro específico.
     *
     * @param bookId ID del libro
     * @return lista de reseñas del libro
     */
    List<Review> findByBookId(Long bookId);

    /**
     * Busca todas las reseñas escritas por un usuario específico.
     *
     * @param userId ID del usuario
     * @return lista de reseñas del usuario
     */
    List<Review> findByUserId(Long userId);

    /**
     * Busca todas las reseñas con una puntuación exacta.
     *
     * @param rating puntuación a buscar (1-5)
     * @return lista de reseñas con esa puntuación
     */
    List<Review> findByRating(Integer rating);

    /**
     * Variante para listados filtrados por valoración con asociaciones precargadas.
     */
    @EntityGraph(attributePaths = {"book", "user"})
    List<Review> findByRatingOrderByCreatedAtDesc(Integer rating);

    /**
     * Carga la reseña junto con su libro y usuario para formularios y vistas.
     */
    @EntityGraph(attributePaths = {"book", "user"})
    Optional<Review> findDetailedById(Long id);
}
