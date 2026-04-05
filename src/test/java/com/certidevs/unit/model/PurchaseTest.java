package com.certidevs.unit.model;

import com.certidevs.model.Book;
import com.certidevs.model.Purchase;
import com.certidevs.model.Role;
import com.certidevs.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * NIVEL 3 - Test unitario de la entidad Purchase.
 *
 * <h3>Aspectos destacados:</h3>
 * <ul>
 *   <li>{@code @RepeatedTest}: repite el mismo test N veces.
 *       Util para verificar que operaciones no deterministas (como LocalDateTime.now())
 *       siempre producen resultados validos.</li>
 *   <li>Testing de timestamps automáticos.</li>
 * </ul>
 *
 * @see Purchase
 */
@DisplayName("Purchase - Test unitario con @RepeatedTest (Nivel 3)")
class PurchaseTest {

    @Nested
    @DisplayName("Creación con Builder")
    class BuilderTests {

        @Test
        @DisplayName("El Builder crea una compra con usuario y libro")
        void builder_createsWithUserAndBook() {
            User user = User.builder().id(1L).username("buyer").email("b@t.com")
                    .password("p").role(Role.ROLE_USER).build();
            Book book = Book.builder().id(1L).title("Rayuela").price(14.00).build();

            Purchase purchase = Purchase.builder()
                    .id(1L)
                    .user(user)
                    .book(book)
                    .build();

            assertAll(
                    () -> assertThat(purchase.getId()).isEqualTo(1L),
                    () -> assertThat(purchase.getUser().getUsername()).isEqualTo("buyer"),
                    () -> assertThat(purchase.getBook().getTitle()).isEqualTo("Rayuela")
            );
        }

        @Test
        @DisplayName("purchasedAt se asigna automáticamente con @Builder.Default")
        void purchasedAt_isAutoAssigned() {
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            Purchase purchase = Purchase.builder()
                    .id(1L)
                    .build();

            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(purchase.getPurchasedAt())
                    .isNotNull()
                    .isAfter(before)
                    .isBefore(after);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // @RepeatedTest: verificar comportamiento consistente
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("@RepeatedTest - Consistencia temporal")
    class RepeatedTests {

        /**
         * {@code @RepeatedTest(5)} ejecuta este test 5 veces.
         *
         * <p>Es util para verificar que un comportamiento no-determinista
         * (como generar un timestamp) siempre produce un resultado valido.</p>
         *
         * <p>También se usa en test de concurrencia o para detectar
         * errores intermitentes (flaky tests).</p>
         */
        @RepeatedTest(value = 5, name = "Repeticion {currentRepetition} de {totalRepetitions}")
        @DisplayName("purchasedAt siempre se genera correctamente")
        void purchasedAt_isAlwaysGenerated() {
            Purchase purchase = Purchase.builder().build();

            assertNotNull(purchase.getPurchasedAt(),
                    "purchasedAt no debe ser null en ninguna repetición");
            assertThat(purchase.getPurchasedAt())
                    .isBefore(LocalDateTime.now().plusSeconds(1));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Relaciones
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Relaciones ManyToOne")
    class RelationTests {

        @Test
        @DisplayName("Se puede cambiar el usuario de una compra")
        void changeUser_works() {
            User user1 = User.builder().id(1L).username("user1").email("u1@t.com")
                    .password("p").role(Role.ROLE_USER).build();
            User user2 = User.builder().id(2L).username("user2").email("u2@t.com")
                    .password("p").role(Role.ROLE_USER).build();

            Purchase purchase = Purchase.builder().id(1L).user(user1).build();
            purchase.setUser(user2);

            assertThat(purchase.getUser().getUsername()).isEqualTo("user2");
        }

        @Test
        @DisplayName("Se puede asignar un libro a la compra después de crearla")
        void setBook_afterCreation() {
            Purchase purchase = Purchase.builder().id(1L).build();
            Book book = Book.builder().id(1L).title("Ficciones").build();

            purchase.setBook(book);

            assertThat(purchase.getBook()).isNotNull();
            assertThat(purchase.getBook().getTitle()).isEqualTo("Ficciones");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // equals/hashCode
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("equals y hashCode")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Compras con el mismo id son iguales")
        void purchasesWithSameId_areEqual() {
            Purchase p1 = Purchase.builder().id(1L).build();
            Purchase p2 = Purchase.builder().id(1L).build();

            assertEquals(p1, p2);
        }

        @Test
        @DisplayName("Compras con distinto id no son iguales")
        void purchasesWithDifferentId_areNotEqual() {
            Purchase p1 = Purchase.builder().id(1L).build();
            Purchase p2 = Purchase.builder().id(2L).build();

            assertNotEquals(p1, p2);
        }
    }
}
