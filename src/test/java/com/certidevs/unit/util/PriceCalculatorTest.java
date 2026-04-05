package com.certidevs.unit.util;

import com.certidevs.model.Book;
import com.certidevs.util.PriceCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para {@link PriceCalculator}.
 *
 * <p>Testea las reglas de negocio de calculo de precios con descuentos.
 * Es un ejemplo perfecto de test unitario profesional: logica de negocio
 * pura, multiples edge cases, y reglas claras que verificar.</p>
 *
 * <h3>Patrones JUnit utilizados:</h3>
 * <ul>
 *   <li>{@code @CsvSource}: tablas de datos con multiples parametros.</li>
 *   <li>{@code assertThrows}: verificar que se lanzan excepciones.</li>
 *   <li>{@code assertAll}: agrupar verificaciones relacionadas.</li>
 *   <li>Testing de valores limite (boundary testing): 0, 1, 2, 4, 5, 9, 10.</li>
 *   <li>Testing de precision: problemas de punto flotante con doubles.</li>
 * </ul>
 *
 * @see PriceCalculator
 */
@DisplayName("PriceCalculator - Calculo de precios con descuentos")
class PriceCalculatorTest {

    // ═══════════════════════════════════════════════════════════════
    // volumeDiscount - Descuento por volumen
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("volumeDiscount - Descuento por cantidad")
    class VolumeDiscountTests {

        /**
         * Tabla de verdad completa para los descuentos por volumen.
         * Cada fila: cantidad, descuento esperado.
         */
        @ParameterizedTest(name = "{0} libros -> {1}% descuento")
        @CsvSource({
                "0,  0.0",     // 0 libros: sin descuento
                "1,  0.0",     // 1 libro: sin descuento
                "2,  0.05",    // 2 libros: 5% (limite inferior del tramo)
                "3,  0.05",    // 3 libros: 5%
                "4,  0.05",    // 4 libros: 5% (limite superior del tramo)
                "5,  0.10",    // 5 libros: 10% (limite inferior)
                "7,  0.10",    // 7 libros: 10%
                "9,  0.10",    // 9 libros: 10% (limite superior)
                "10, 0.15",    // 10 libros: 15% (limite inferior)
                "50, 0.15",    // 50 libros: 15%
                "100,0.15"     // 100 libros: 15%
        })
        @DisplayName("Descuento por tramos de cantidad")
        void volumeDiscount_byQuantity(int quantity, double expectedDiscount) {
            assertEquals(expectedDiscount, PriceCalculator.volumeDiscount(quantity));
        }

        @Test
        @DisplayName("Cantidad negativa lanza IllegalArgumentException")
        void volumeDiscount_negativeThrowsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> PriceCalculator.volumeDiscount(-1)
            );
            // Verificamos que el mensaje incluye informacion util
            assertTrue(ex.getMessage().contains("-1"));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateTotal - Precio total con descuento
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("calculateTotal - Precio total con descuento por volumen")
    class CalculateTotalTests {

        @Test
        @DisplayName("1 libro sin descuento: precio completo")
        void calculateTotal_oneBook_noDiscount() {
            List<Book> books = List.of(book(10.0));

            assertEquals(10.0, PriceCalculator.calculateTotal(books));
        }

        @Test
        @DisplayName("3 libros: 5% descuento")
        void calculateTotal_threeBooks_5percentDiscount() {
            List<Book> books = List.of(book(10.0), book(10.0), book(10.0));

            // 30.0 * 0.95 = 28.5
            assertEquals(28.5, PriceCalculator.calculateTotal(books));
        }

        @Test
        @DisplayName("5 libros: 10% descuento")
        void calculateTotal_fiveBooks_10percentDiscount() {
            List<Book> books = List.of(
                    book(10.0), book(10.0), book(10.0), book(10.0), book(10.0)
            );

            // 50.0 * 0.90 = 45.0
            assertEquals(45.0, PriceCalculator.calculateTotal(books));
        }

        @Test
        @DisplayName("10 libros: 15% descuento")
        void calculateTotal_tenBooks_15percentDiscount() {
            List<Book> books = IntStream.range(0, 10)
                    .mapToObj(i -> book(10.0))
                    .toList();

            // 100.0 * 0.85 = 85.0
            assertEquals(85.0, PriceCalculator.calculateTotal(books));
        }

        @Test
        @DisplayName("Lista vacia: devuelve 0")
        void calculateTotal_emptyList_returnsZero() {
            assertEquals(0.0, PriceCalculator.calculateTotal(List.of()));
        }

        @Test
        @DisplayName("Lista null: lanza IllegalArgumentException")
        void calculateTotal_null_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> PriceCalculator.calculateTotal(null));
        }

        @Test
        @DisplayName("Ignora libros con precio null")
        void calculateTotal_ignoresNullPrices() {
            List<Book> books = List.of(book(10.0), book(null), book(20.0));

            // Solo suma 10 + 20 = 30, con 5% descuento (3 libros) = 28.5
            assertEquals(28.5, PriceCalculator.calculateTotal(books));
        }

        @Test
        @DisplayName("Libros con precios diferentes")
        void calculateTotal_differentPrices() {
            List<Book> books = List.of(book(15.90), book(12.50));

            // (15.90 + 12.50) * 0.95 = 26.98
            double total = PriceCalculator.calculateTotal(books);
            assertEquals(26.98, total);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateTotalWithLoyalty - Con descuento de fidelidad
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("calculateTotalWithLoyalty - Descuento de fidelidad")
    class LoyaltyTests {

        @Test
        @DisplayName("Usuario nuevo (0 compras): sin descuento de fidelidad")
        void loyalty_newUser_noDiscount() {
            List<Book> books = List.of(book(100.0));

            double total = PriceCalculator.calculateTotalWithLoyalty(books, 0);

            // Sin descuento volumen (1 libro), sin fidelidad = 100.0
            assertEquals(100.0, total);
        }

        @Test
        @DisplayName("Usuario con 4 compras: aun no alcanza umbral de fidelidad")
        void loyalty_belowThreshold_noDiscount() {
            List<Book> books = List.of(book(100.0));

            double total = PriceCalculator.calculateTotalWithLoyalty(books, 4);

            assertEquals(100.0, total);
        }

        @Test
        @DisplayName("Usuario con 5+ compras: obtiene 3% adicional de descuento")
        void loyalty_aboveThreshold_getsDiscount() {
            List<Book> books = List.of(book(100.0));

            double total = PriceCalculator.calculateTotalWithLoyalty(books, 5);

            // 100.0 * (1 - 0.03) = 97.0
            assertEquals(97.0, total);
        }

        @Test
        @DisplayName("Descuento de volumen + fidelidad se acumulan")
        void loyalty_combinedWithVolumeDiscount() {
            List<Book> books = List.of(book(10.0), book(10.0), book(10.0));

            double total = PriceCalculator.calculateTotalWithLoyalty(books, 10);

            // Volumen: 30 * 0.95 = 28.5
            // Fidelidad: 28.5 * 0.97 = 27.645 → 27.65
            assertEquals(27.65, total);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // calculateTax / priceWithTax - IVA
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("IVA (4% para libros en España)")
    class TaxTests {

        @ParameterizedTest(name = "Precio {0} EUR -> IVA {1} EUR")
        @CsvSource({
                "0.0,  0.0",
                "10.0, 0.4",
                "15.90, 0.64",
                "100.0, 4.0"
        })
        @DisplayName("Calcula el IVA correctamente")
        void calculateTax_calculatesCorrectly(double price, double expectedTax) {
            assertEquals(expectedTax, PriceCalculator.calculateTax(price));
        }

        @Test
        @DisplayName("Precio negativo lanza IllegalArgumentException")
        void calculateTax_negativePrice_throwsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> PriceCalculator.calculateTax(-5.0));
        }

        @Test
        @DisplayName("priceWithTax suma precio + IVA")
        void priceWithTax_addsCorrectly() {
            // 10.0 + 10.0 * 0.04 = 10.40
            assertEquals(10.4, PriceCalculator.priceWithTax(10.0));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // savings - Ahorro respecto a precio sin descuento
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("savings - Ahorro total")
    class SavingsTests {

        @Test
        @DisplayName("Sin descuento (1 libro): ahorro = 0")
        void savings_noDiscount() {
            assertEquals(0.0, PriceCalculator.savings(List.of(book(20.0))));
        }

        @Test
        @DisplayName("Con descuento 5% (3 libros): ahorro calculado")
        void savings_withDiscount() {
            List<Book> books = List.of(book(10.0), book(10.0), book(10.0));

            // Precio completo: 30.0, con descuento: 28.5, ahorro: 1.5
            assertEquals(1.5, PriceCalculator.savings(books));
        }

        @Test
        @DisplayName("Lista vacia: ahorro = 0")
        void savings_emptyList() {
            assertEquals(0.0, PriceCalculator.savings(List.of()));
        }

        @Test
        @DisplayName("Lista null: ahorro = 0")
        void savings_nullList() {
            assertEquals(0.0, PriceCalculator.savings(null));
        }
    }

    // ── Helper para crear libros con precio ──
    private static Book book(Double price) {
        return Book.builder().price(price).build();
    }
}
