package com.certidevs.service;

import com.certidevs.model.Review;
import com.certidevs.model.User;
import com.certidevs.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test unitario para {@link ReviewService}, enfocado en la lógica de autorización.
 *
 * <p>Verifica que {@link ReviewService#canModify(Long, User)} solo permite
 * al autor de una reseña editarla o eliminarla.</p>
 *
 * @see ReviewService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService - Tests unitarios con Mockito")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("canModify: devuelve true si el usuario es el autor de la reseña")
    void canModify_returnsTrueForOwner() {
        User owner = User.builder().id(1L).username("owner").build();
        Review review = Review.builder().id(10L).user(owner).build();
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));

        boolean result = reviewService.canModify(10L, owner);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("canModify: devuelve false si el usuario NO es el autor")
    void canModify_returnsFalseForOtherUser() {
        User owner = User.builder().id(1L).username("owner").build();
        User other = User.builder().id(2L).username("other").build();
        Review review = Review.builder().id(10L).user(owner).build();
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));

        boolean result = reviewService.canModify(10L, other);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("canModify: devuelve false si el usuario es null")
    void canModify_returnsFalseForNullUser() {
        boolean result = reviewService.canModify(10L, null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("canModify: devuelve false si la reseña no existe")
    void canModify_returnsFalseForNonExistentReview() {
        User user = User.builder().id(1L).username("user").build();
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = reviewService.canModify(999L, user);

        assertThat(result).isFalse();
    }
}
