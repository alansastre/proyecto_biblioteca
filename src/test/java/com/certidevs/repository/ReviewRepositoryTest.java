package com.certidevs.repository;

import com.certidevs.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración para {@link ReviewRepository}.
 *
 * <p>Verifica las consultas personalizadas del repositorio de reseñas,
 * incluyendo filtros por libro, usuario y rating, y carga eager con EntityGraph.</p>
 *
 * @see ReviewRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ReviewRepository - Tests de integración JPA")
class ReviewRepositoryTest {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;

    private User user;
    private User admin;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        Author author = authorRepository.save(Author.builder().name("Test Author").build());

        book1 = bookRepository.save(Book.builder().title("Libro 1").author(author).build());
        book2 = bookRepository.save(Book.builder().title("Libro 2").author(author).build());

        user = userRepository.save(User.builder()
                .username("user").email("user@test.com")
                .password("{bcrypt}pass").role(Role.ROLE_USER).build());

        admin = userRepository.save(User.builder()
                .username("admin").email("admin@test.com")
                .password("{bcrypt}pass").role(Role.ROLE_ADMIN).build());

        // Crear reseñas con diferentes ratings
        reviewRepository.save(Review.builder()
                .comment("Excelente").rating(5)
                .createdAt(LocalDateTime.now().minusDays(2))
                .user(user).book(book1).build());

        reviewRepository.save(Review.builder()
                .comment("Bueno").rating(4)
                .createdAt(LocalDateTime.now().minusDays(1))
                .user(admin).book(book1).build());

        reviewRepository.save(Review.builder()
                .comment("Regular").rating(3)
                .createdAt(LocalDateTime.now())
                .user(user).book(book2).build());
    }

    @Nested
    @DisplayName("findByBookId")
    class FindByBookIdTests {

        @Test
        @DisplayName("Devuelve solo las reseñas del libro indicado")
        void findByBookId_returnsBookReviews() {
            List<Review> result = reviewRepository.findByBookId(book1.getId());

            assertThat(result).hasSize(2);
            assertThat(result).extracting(Review::getComment)
                    .containsExactlyInAnyOrder("Excelente", "Bueno");
        }

        @Test
        @DisplayName("Devuelve lista vacia si el libro no tiene reseñas")
        void findByBookId_returnsEmptyForBookWithoutReviews() {
            Author author = authorRepository.save(Author.builder().name("Otro").build());
            Book emptyBook = bookRepository.save(Book.builder().title("Sin reseñas").author(author).build());

            List<Review> result = reviewRepository.findByBookId(emptyBook.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserIdTests {

        @Test
        @DisplayName("Devuelve solo las reseñas del usuario indicado")
        void findByUserId_returnsUserReviews() {
            List<Review> result = reviewRepository.findByUserId(user.getId());

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("El admin tiene solo una reseña")
        void findByUserId_returnsAdminReviews() {
            List<Review> result = reviewRepository.findByUserId(admin.getId());

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getComment()).isEqualTo("Bueno");
        }
    }

    @Nested
    @DisplayName("findByRating")
    class FindByRatingTests {

        @Test
        @DisplayName("Filtra reseñas por rating exacto")
        void findByRating_filtersCorrectly() {
            List<Review> fiveStars = reviewRepository.findByRating(5);

            assertThat(fiveStars).hasSize(1);
            assertThat(fiveStars.getFirst().getComment()).isEqualTo("Excelente");
        }

        @Test
        @DisplayName("Devuelve lista vacia si no hay reseñas con ese rating")
        void findByRating_returnsEmptyForNoMatches() {
            List<Review> oneStars = reviewRepository.findByRating(1);

            assertThat(oneStars).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByOrderByCreatedAtDesc")
    class FindAllOrderedTests {

        @Test
        @DisplayName("Devuelve las reseñas ordenadas por fecha descendente (más recientes primero)")
        void findAllOrdered_returnsMostRecentFirst() {
            List<Review> result = reviewRepository.findAllByOrderByCreatedAtDesc();

            assertThat(result).hasSize(3);
            // La más reciente (createdAt = now()) debe estar primera
            assertThat(result.getFirst().getComment()).isEqualTo("Regular");
        }
    }

    @Nested
    @DisplayName("findDetailedById (con @EntityGraph)")
    class FindDetailedTests {

        @Test
        @DisplayName("Carga la reseña con libro y usuario (eager loading)")
        void findDetailedById_loadsAssociations() {
            List<Review> allReviews = reviewRepository.findAll();
            Long reviewId = allReviews.getFirst().getId();

            Optional<Review> result = reviewRepository.findDetailedById(reviewId);

            assertThat(result).isPresent();
            assertThat(result.get().getBook()).isNotNull();
            assertThat(result.get().getUser()).isNotNull();
        }
    }
}
