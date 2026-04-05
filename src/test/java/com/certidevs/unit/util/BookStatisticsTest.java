package com.certidevs.unit.util;

import com.certidevs.model.Book;
import com.certidevs.model.Review;
import com.certidevs.util.BookStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para {@link BookStatistics}.
 *
 * <p>Testea las funciones estadisticas sobre libros y reseñas.
 * Es un test unitario real: logica pura sin Spring ni mocks.</p>
 *
 * <h3>Patrones JUnit utilizados:</h3>
 * <ul>
 *   <li>{@code @BeforeEach}: preparar datos de prueba reutilizables.</li>
 *   <li>{@code assertAll}: verificar multiples propiedades de un resultado.</li>
 *   <li>Testing de Optional: {@code isPresent}, {@code isEmpty}.</li>
 *   <li>Testing de colecciones: verificar tamaño, contenido, orden.</li>
 *   <li>Edge cases: listas null, vacias, un solo elemento.</li>
 * </ul>
 *
 * @see BookStatistics
 */
@DisplayName("BookStatistics - Estadisticas de libros y reseñas")
class BookStatisticsTest {

    private List<Review> reviews;
    private List<Book> books;

    @BeforeEach
    void setUp() {
        reviews = List.of(
                Review.builder().rating(5).comment("Excelente").build(),
                Review.builder().rating(4).comment("Bueno").build(),
                Review.builder().rating(4).comment("Muy bueno").build(),
                Review.builder().rating(3).comment("Regular").build(),
                Review.builder().rating(5).comment("Obra maestra").build()
        );

        books = List.of(
                Book.builder().id(1L).title("Libro 1").price(10.0).available(true).build(),
                Book.builder().id(2L).title("Libro 2").price(20.0).available(true).build(),
                Book.builder().id(3L).title("Libro 3").price(15.0).available(false).build(),
                Book.builder().id(4L).title("Libro 4").price(30.0).available(true).build()
        );
    }

    // ═══════════════════════════════════════════════════════════════
    // averageRating
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("averageRating")
    class AverageRatingTests {

        @Test
        @DisplayName("Calcula el rating medio correctamente")
        void averageRating_calculatesCorrectly() {
            // (5 + 4 + 4 + 3 + 5) / 5 = 4.2
            assertEquals(4.2, BookStatistics.averageRating(reviews));
        }

        @Test
        @DisplayName("Devuelve 0.0 para lista vacia")
        void averageRating_returnsZeroForEmptyList() {
            assertEquals(0.0, BookStatistics.averageRating(List.of()));
        }

        @Test
        @DisplayName("Devuelve 0.0 para lista null")
        void averageRating_returnsZeroForNull() {
            assertEquals(0.0, BookStatistics.averageRating(null));
        }

        @Test
        @DisplayName("Con una sola reseña devuelve ese rating")
        void averageRating_singleReview() {
            List<Review> single = List.of(Review.builder().rating(3).comment("Ok").build());
            assertEquals(3.0, BookStatistics.averageRating(single));
        }

        @Test
        @DisplayName("Ignora reseñas con rating null")
        void averageRating_ignoresNullRatings() {
            List<Review> withNull = List.of(
                    Review.builder().rating(4).comment("A").build(),
                    Review.builder().rating(null).comment("B").build(),
                    Review.builder().rating(2).comment("C").build()
            );
            // (4 + 2) / 2 = 3.0
            assertEquals(3.0, BookStatistics.averageRating(withNull));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // ratingDistribution
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("ratingDistribution")
    class RatingDistributionTests {

        @Test
        @DisplayName("Calcula la distribucion correctamente")
        void ratingDistribution_calculatesCorrectly() {
            Map<Integer, Long> distribution = BookStatistics.ratingDistribution(reviews);

            assertAll(
                    () -> assertEquals(0L, distribution.get(1), "1 estrella"),
                    () -> assertEquals(0L, distribution.get(2), "2 estrellas"),
                    () -> assertEquals(1L, distribution.get(3), "3 estrellas"),
                    () -> assertEquals(2L, distribution.get(4), "4 estrellas"),
                    () -> assertEquals(2L, distribution.get(5), "5 estrellas")
            );
        }

        @Test
        @DisplayName("Devuelve mapa con todos los ratings a 0 para lista vacia")
        void ratingDistribution_returnsZerosForEmpty() {
            Map<Integer, Long> distribution = BookStatistics.ratingDistribution(List.of());

            assertThat(distribution).hasSize(5);
            assertThat(distribution.values()).allMatch(count -> count == 0L);
        }

        @Test
        @DisplayName("El mapa siempre contiene las claves 1 a 5")
        void ratingDistribution_alwaysHasKeys1To5() {
            Map<Integer, Long> distribution = BookStatistics.ratingDistribution(null);

            assertThat(distribution).containsKeys(1, 2, 3, 4, 5);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // averagePrice
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("averagePrice")
    class AveragePriceTests {

        @Test
        @DisplayName("Calcula el precio medio correctamente")
        void averagePrice_calculatesCorrectly() {
            // (10 + 20 + 15 + 30) / 4 = 18.75
            assertEquals(18.75, BookStatistics.averagePrice(books));
        }

        @Test
        @DisplayName("Devuelve 0.0 para lista null")
        void averagePrice_returnsZeroForNull() {
            assertEquals(0.0, BookStatistics.averagePrice(null));
        }

        @Test
        @DisplayName("Ignora libros con precio null")
        void averagePrice_ignoresNullPrices() {
            List<Book> withNull = List.of(
                    Book.builder().price(10.0).build(),
                    Book.builder().price(null).build(),
                    Book.builder().price(20.0).build()
            );
            assertEquals(15.0, BookStatistics.averagePrice(withNull));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // mostExpensive / cheapest
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("mostExpensive y cheapest")
    class MinMaxTests {

        @Test
        @DisplayName("mostExpensive devuelve el libro mas caro")
        void mostExpensive_returnsCorrectBook() {
            Optional<Book> result = BookStatistics.mostExpensive(books);

            assertThat(result).isPresent();
            assertThat(result.get().getPrice()).isEqualTo(30.0);
            assertThat(result.get().getTitle()).isEqualTo("Libro 4");
        }

        @Test
        @DisplayName("cheapest devuelve el libro mas barato")
        void cheapest_returnsCorrectBook() {
            Optional<Book> result = BookStatistics.cheapest(books);

            assertThat(result).isPresent();
            assertThat(result.get().getPrice()).isEqualTo(10.0);
        }

        @Test
        @DisplayName("mostExpensive devuelve Optional vacio para lista vacia")
        void mostExpensive_returnsEmptyForEmptyList() {
            assertThat(BookStatistics.mostExpensive(List.of())).isEmpty();
        }

        @Test
        @DisplayName("cheapest devuelve Optional vacio para null")
        void cheapest_returnsEmptyForNull() {
            assertThat(BookStatistics.cheapest(null)).isEmpty();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // countAvailable
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("countAvailable")
    class CountAvailableTests {

        @Test
        @DisplayName("Cuenta solo los libros disponibles")
        void countAvailable_countsCorrectly() {
            // 3 de 4 libros tienen available=true
            assertEquals(3, BookStatistics.countAvailable(books));
        }

        @Test
        @DisplayName("Devuelve 0 para lista null")
        void countAvailable_returnsZeroForNull() {
            assertEquals(0, BookStatistics.countAvailable(null));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // priceRange
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("priceRange")
    class PriceRangeTests {

        @Test
        @DisplayName("Devuelve [min, max] correctamente")
        void priceRange_calculatesCorrectly() {
            double[] range = BookStatistics.priceRange(books);

            assertAll(
                    () -> assertEquals(10.0, range[0], "precio minimo"),
                    () -> assertEquals(30.0, range[1], "precio maximo")
            );
        }

        @Test
        @DisplayName("Devuelve [0, 0] para lista vacia")
        void priceRange_returnsZerosForEmpty() {
            double[] range = BookStatistics.priceRange(List.of());

            assertArrayEquals(new double[]{0.0, 0.0}, range);
        }

        @Test
        @DisplayName("Con un solo libro, min == max")
        void priceRange_sameForSingleBook() {
            List<Book> single = List.of(Book.builder().price(15.0).build());
            double[] range = BookStatistics.priceRange(single);

            assertEquals(range[0], range[1]);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // totalPrice
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("totalPrice")
    class TotalPriceTests {

        @Test
        @DisplayName("Suma todos los precios")
        void totalPrice_sumsCorrectly() {
            // 10 + 20 + 15 + 30 = 75
            assertEquals(75.0, BookStatistics.totalPrice(books));
        }

        @Test
        @DisplayName("Devuelve 0 para lista null")
        void totalPrice_returnsZeroForNull() {
            assertEquals(0.0, BookStatistics.totalPrice(null));
        }
    }
}
