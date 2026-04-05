package com.certidevs.service;

import com.certidevs.model.Review;
import com.certidevs.model.User;
import com.certidevs.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio con la lógica de negocio para la gestión de reseñas de libros.
 *
 * <p>Además de las operaciones CRUD, incluye un método de autorización a nivel de negocio:
 * {@link #canModify(Long, User)} que comprueba si un usuario es el autor de una reseña.</p>
 *
 * <h3>Nota sobre autorización:</h3>
 * <p>La autorización de "solo el autor puede editar/eliminar su reseña" se implementa
 * como lógica de negocio en el servicio, no como regla de Spring Security.
 * Esto es valido cuando la regla depende de datos de la entidad (propietario de la reseña).</p>
 *
 * @see ReviewRepository
 * @see Review
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * Obtiene todas las reseñas.
     *
     * @return lista de todas las reseñas
     */
    @Transactional(readOnly = true)
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    /**
     * Obtiene las reseñas listas para renderizar la tabla sin disparar cargas LAZY.
     */
    @Transactional(readOnly = true)
    public List<Review> findAllForList() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Busca una reseña por su ID.
     *
     * @param id identificador de la reseña
     * @return Optional con la reseña si existe
     */
    @Transactional(readOnly = true)
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    /**
     * Carga la reseña junto con libro y usuario para pantallas de detalle o formulario.
     */
    @Transactional(readOnly = true)
    public Optional<Review> findByIdForView(Long id) {
        return reviewRepository.findDetailedById(id);
    }

    /**
     * Guarda o actualiza una reseña.
     *
     * @param review reseña a guardar
     * @return reseña persistida con ID asignado
     */
    @Transactional
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    /**
     * Elimina una reseña por su ID.
     *
     * @param id identificador de la reseña a eliminar
     */
    @Transactional
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    /**
     * Busca reseñas de un libro específico.
     * Si el bookId es null, devuelve todas las reseñas.
     *
     * @param bookId ID del libro (puede ser null)
     * @return lista de reseñas
     */
    @Transactional(readOnly = true)
    public List<Review> findByBookId(Long bookId) {
        return bookId == null ? findAll() : reviewRepository.findByBookId(bookId);
    }

    /**
     * Busca reseñas de un usuario específico.
     * Si el userId es null, devuelve todas las reseñas.
     *
     * @param userId ID del usuario (puede ser null)
     * @return lista de reseñas
     */
    @Transactional(readOnly = true)
    public List<Review> findByUserId(Long userId) {
        return userId == null ? findAll() : reviewRepository.findByUserId(userId);
    }

    /**
     * Busca reseñas con una puntuación exacta.
     * Si el rating es null, devuelve todas las reseñas.
     *
     * @param rating puntuación a filtrar (puede ser null)
     * @return lista de reseñas
     */
    @Transactional(readOnly = true)
    public List<Review> findByRating(Integer rating) {
        return rating == null ? findAll() : reviewRepository.findByRating(rating);
    }

    /**
     * Variante para listados filtrados por valoración con asociaciones precargadas.
     */
    @Transactional(readOnly = true)
    public List<Review> findByRatingForList(Integer rating) {
        return rating == null ? findAllForList() : reviewRepository.findByRatingOrderByCreatedAtDesc(rating);
    }

    /**
     * Comprueba si el usuario puede modificar (editar/eliminar) una reseña.
     * Solo el autor de la reseña puede modificarla.
     *
     * @param reviewId ID de la reseña
     * @param user     usuario que intenta la operación (puede ser null)
     * @return true si el usuario es el autor de la reseña
     */
    @Transactional(readOnly = true)
    public boolean canModify(Long reviewId, User user) {
        if (user == null) return false;
        return findById(reviewId)
                .map(r -> r.getUser().getId().equals(user.getId()))
                .orElse(false);
    }

    /**
     * Sobrecarga util cuando la reseña ya se ha cargado previamente en el controlador.
     */
    public boolean canModify(Review review, User user) {
        return user != null
                && review != null
                && review.getUser() != null
                && review.getUser().getId() != null
                && review.getUser().getId().equals(user.getId());
    }
}
