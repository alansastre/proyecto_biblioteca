package com.certidevs.unit.model;

import com.certidevs.model.Book;
import com.certidevs.model.Review;
import com.certidevs.model.User;
import com.certidevs.model.Role;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * NIVEL 3 - Test unitario con @ParameterizedTest: múltiples variantes.
 *
 * <p>Prueba la entidad {@link Review} y su relacion con User y Book.
 * Introduce tests parametrizados, que permiten ejecutar el mismo test
 * con diferentes datos de entrada.</p>
 *
 * <h3>Aspectos destacados:</h3>
 * <ul>
 *   <li>{@code @ParameterizedTest}: ejecuta el test múltiples veces con datos diferentes.</li>
 *   <li>{@code @ValueSource}: proporciona un array de valores simples (int, String, etc.).</li>
 *   <li>{@code @CsvSource}: proporciona pares/tuplas de valores separados por comas.</li>
 *   <li>{@code @MethodSource}: proporciona datos desde un método {@code static}.</li>
 *   <li>Mensaje personalizado en assertions: se muestra cuando el test falla.</li>
 * </ul>
 *
 * <h3>@ParameterizedTest: cuando usarlo</h3>
 * <p>Cuando quieres probar lo mismo con múltiples valores de entrada.
 * En lugar de escribir 5 tests casi identicos, escribes uno parametrizado.</p>
 *
 * @see Review
 */
@DisplayName("Review - Test con @ParameterizedTest (Nivel 3: tests parametrizados)")
class ReviewTest {

    // ══════════════════════════════════════════��════════════════════
    // Creación basica
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Creación con Builder")
    class BuilderTests {

        @Test
        @DisplayName("El Builder crea una review con todos los campos")
        void builder_createsReviewWithAllFields() {
            User user = User.builder().id(1L).username("user").email("u@t.com")
                    .password("p").role(Role.ROLE_USER).build();
            Book book = Book.builder().id(1L).title("Ficciones").build();
            LocalDateTime now = LocalDateTime.now();

            Review review = Review.builder()
                    .id(1L)
                    .comment("Excelente libro")
                    .rating(5)
                    .createdAt(now)
                    .user(user)
                    .book(book)
                    .build();

            assertAll(
                    () -> assertEquals(1L, review.getId()),
                    () -> assertEquals("Excelente libro", review.getComment()),
                    () -> assertEquals(5, review.getRating()),
                    () -> assertEquals(now, review.getCreatedAt()),
                    () -> assertEquals("user", review.getUser().getUsername()),
                    () -> assertEquals("Ficciones", review.getBook().getTitle())
            );
        }

        @Test
        @DisplayName("createdAt se asigna automáticamente con @Builder.Default")
        void createdAt_isAutoAssigned() {
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            Review review = Review.builder()
                    .comment("Test")
                    .rating(3)
                    .build();

            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(review.getCreatedAt())
                    .isNotNull()
                    .isAfter(before)
                    .isBefore(after);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // @ParameterizedTest con @ValueSource
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("@ValueSource - Rating valido (1 a 5)")
    class RatingValueSourceTests {

        /**
         * {@code @ValueSource(ints = {1, 2, 3, 4, 5})} ejecuta este test 5 veces,
         * una por cada valor del array. El parametro {@code rating} recibe cada valor.
         */
        @ParameterizedTest(name = "Rating {0} es un valor valido")
        @ValueSource(ints = {1, 2, 3, 4, 5})
        @DisplayName("Los ratings de 1 a 5 se asignan correctamente")
        void validRatings_areAccepted(int rating) {
            Review review = Review.builder()
                    .comment("Test")
                    .rating(rating)
                    .build();

            assertThat(review.getRating())
                    .isGreaterThanOrEqualTo(1)
                    .isLessThanOrEqualTo(5);
        }

        /**
         * Verifica que se pueden crear reviews con diferentes longitudes de comentario.
         */
        @ParameterizedTest(name = "Comentario: \"{0}\"")
        @ValueSource(strings = {"Bueno", "Muy bueno", "Excelente obra maestra de la literatura"})
        @DisplayName("Se aceptan comentarios de diferentes longitudes")
        void differentCommentLengths_areAccepted(String comment) {
            Review review = Review.builder()
                    .comment(comment)
                    .rating(4)
                    .build();

            assertThat(review.getComment())
                    .isNotBlank()
                    .isEqualTo(comment);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // @ParameterizedTest con @CsvSource
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("@CsvSource - Pares de rating y comentario")
    class CsvSourceTests {

        /**
         * {@code @CsvSource} permite pasar múltiples parámetros separados por comas.
         * Cada línea es una ejecución del test. Los parámetros se convierten
         * automáticamente al tipo declarado en el método.
         */
        @ParameterizedTest(name = "Rating {0} -> \"{1}\"")
        @CsvSource({
                "1, Muy malo, necesita mejorar mucho",
                "2, Regular, esperaba más del libro",
                "3, Aceptable, cumple lo básico",
                "4, Bueno, recomendable para los amantes del genero",
                "5, Excelente, obra maestra imprescindible"
        })
        @DisplayName("Cada rating tiene un comentario descriptivo asociable")
        void ratingWithComment_areCreatedCorrectly(int rating, String comment) {
            Review review = Review.builder()
                    .rating(rating)
                    .comment(comment)
                    .build();

            assertAll(
                    () -> assertThat(review.getRating()).isBetween(1, 5),
                    () -> assertThat(review.getComment()).isNotBlank()
            );
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // @ParameterizedTest con @MethodSource
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("@MethodSource - Datos desde un método")
    class MethodSourceTests {

        /**
         * Método que provee datos para el test parametrizado.
         * Debe ser {@code static} y devolver un {@code Stream}, {@code Iterable} o array.
         *
         * <p>@MethodSource es más flexible que @ValueSource/@CsvSource
         * porque permite crear objetos complejos como datos de entrada.</p>
         */
        static Stream<Review> reviewProvider() {
            return Stream.of(
                    Review.builder().comment("Corto").rating(1).build(),
                    Review.builder().comment("Un comentario de longitud media sobre el libro").rating(3).build(),
                    Review.builder().comment("Un comentario muy largo que describe detalladamente la opinion del lector sobre el libro, "
                            + "incluyendo aspectos como la trama, los personajes y el estilo narrativo del autor").rating(5).build()
            );
        }

        @ParameterizedTest(name = "Review con rating {0}")
        @MethodSource("reviewProvider")
        @DisplayName("Reviews creadas por @MethodSource tienen datos validos")
        void reviewFromProvider_hasValidData(Review review) {
            assertAll(
                    () -> assertThat(review.getComment()).isNotBlank(),
                    () -> assertThat(review.getRating()).isBetween(1, 5),
                    () -> assertThat(review.getCreatedAt()).isNotNull()
            );
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Relaciones con User y Book
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Relaciones con User y Book")
    class RelationTests {

        @Test
        @DisplayName("Se puede asignar un usuario a la review")
        void setUser_works() {
            User user = User.builder().id(1L).username("reviewer").email("r@t.com")
                    .password("p").role(Role.ROLE_USER).build();
            Review review = Review.builder().comment("Test").rating(4).build();

            review.setUser(user);

            assertThat(review.getUser()).isNotNull();
            assertThat(review.getUser().getUsername()).isEqualTo("reviewer");
        }

        @Test
        @DisplayName("Se puede asignar un libro a la review")
        void setBook_works() {
            Book book = Book.builder().id(1L).title("Rayuela").build();
            Review review = Review.builder().comment("Test").rating(4).build();

            review.setBook(book);

            assertThat(review.getBook()).isNotNull();
            assertThat(review.getBook().getTitle()).isEqualTo("Rayuela");
        }

        @Test
        @DisplayName("Se puede cambiar el rating de una review existente")
        void changeRating_works() {
            Review review = Review.builder().comment("Test").rating(3).build();

            review.setRating(5);

            assertThat(review.getRating()).isEqualTo(5);
        }
    }

    // ═════════════════════════���═══════════════════════════════════��═
    // equals/hashCode
    // ═══════════════════════════════════════════════════════════��═══

    @Nested
    @DisplayName("equals y hashCode")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Reviews con el mismo id son iguales")
        void reviewsWithSameId_areEqual() {
            Review r1 = Review.builder().id(1L).comment("A").rating(3).build();
            Review r2 = Review.builder().id(1L).comment("B").rating(5).build();

            assertEquals(r1, r2);
        }

        @Test
        @DisplayName("Reviews con distinto id no son iguales")
        void reviewsWithDifferentId_areNotEqual() {
            Review r1 = Review.builder().id(1L).comment("A").rating(3).build();
            Review r2 = Review.builder().id(2L).comment("A").rating(3).build();

            assertNotEquals(r1, r2);
        }
    }
}
