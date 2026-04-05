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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración para {@link PurchaseRepository}.
 *
 * <p>Verifica las consultas de compras por usuario, libro y orden cronológico.</p>
 *
 * @see PurchaseRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("PurchaseRepository - Tests de integración JPA")
class PurchaseRepositoryTest {

    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;

    private User buyer;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        Author author = authorRepository.save(Author.builder().name("Test Author").build());
        book1 = bookRepository.save(Book.builder().title("Libro 1").price(15.0).author(author).build());
        book2 = bookRepository.save(Book.builder().title("Libro 2").price(20.0).author(author).build());

        buyer = userRepository.save(User.builder()
                .username("buyer").email("buyer@test.com")
                .password("{bcrypt}pass").role(Role.ROLE_USER).build());

        // Compras con diferentes fechas
        purchaseRepository.save(Purchase.builder()
                .user(buyer).book(book1)
                .purchasedAt(LocalDateTime.now().minusDays(3)).build());

        purchaseRepository.save(Purchase.builder()
                .user(buyer).book(book2)
                .purchasedAt(LocalDateTime.now().minusDays(1)).build());

        purchaseRepository.save(Purchase.builder()
                .user(buyer).book(book1)  // Misma persona compra el mismo libro otra vez
                .purchasedAt(LocalDateTime.now()).build());
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserIdTests {

        @Test
        @DisplayName("Devuelve todas las compras del usuario")
        void findByUserId_returnsAllUserPurchases() {
            List<Purchase> result = purchaseRepository.findByUserId(buyer.getId());

            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("Devuelve lista vacia para un usuario sin compras")
        void findByUserId_returnsEmptyForUserWithNoPurchases() {
            User newUser = userRepository.save(User.builder()
                    .username("new").email("new@test.com")
                    .password("{bcrypt}pass").role(Role.ROLE_USER).build());

            List<Purchase> result = purchaseRepository.findByUserId(newUser.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByUserIdOrderByPurchasedAtDesc")
    class FindByUserIdOrderedTests {

        @Test
        @DisplayName("Devuelve compras ordenadas por fecha descendente")
        void findByUserIdOrdered_returnsMostRecentFirst() {
            List<Purchase> result = purchaseRepository.findByUserIdOrderByPurchasedAtDesc(buyer.getId());

            assertThat(result).hasSize(3);
            // La compra más reciente debe estar primera
            assertThat(result.getFirst().getPurchasedAt())
                    .isAfterOrEqualTo(result.get(1).getPurchasedAt());
            assertThat(result.get(1).getPurchasedAt())
                    .isAfterOrEqualTo(result.get(2).getPurchasedAt());
        }
    }

    @Nested
    @DisplayName("findByBookId")
    class FindByBookIdTests {

        @Test
        @DisplayName("Devuelve las compras de un libro específico")
        void findByBookId_returnsBookPurchases() {
            List<Purchase> result = purchaseRepository.findByBookId(book1.getId());

            // book1 fue comprado 2 veces
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Un usuario puede comprar el mismo libro varias veces")
        void sameUser_canBuySameBookMultipleTimes() {
            List<Purchase> book1Purchases = purchaseRepository.findByBookId(book1.getId());

            // Verificamos que hay 2 compras del mismo libro por el mismo usuario
            assertThat(book1Purchases)
                    .hasSize(2)
                    .allMatch(p -> p.getUser().getId().equals(buyer.getId()));
        }
    }

    @Nested
    @DisplayName("Operaciones CRUD")
    class CrudTests {

        @Test
        @DisplayName("save: persiste una compra con timestamp")
        void save_persistsPurchase() {
            Purchase newPurchase = purchaseRepository.save(Purchase.builder()
                    .user(buyer).book(book2)
                    .purchasedAt(LocalDateTime.now()).build());

            assertThat(newPurchase.getId()).isNotNull();
            assertThat(newPurchase.getPurchasedAt()).isNotNull();
        }

        @Test
        @DisplayName("count: devuelve el total de compras")
        void count_returnsTotal() {
            assertThat(purchaseRepository.count()).isEqualTo(3);
        }
    }
}
