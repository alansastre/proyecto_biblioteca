package com.certidevs.unit.model;

import com.certidevs.model.Book;
import com.certidevs.model.Role;
import com.certidevs.model.User;
import org.junit.jupiter.api.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * NIVEL 3 - Test unitario de User que implementa UserDetails.
 *
 * <p>Prueba tanto las propiedades básicas de la entidad como la implementación
 * de la interfaz {@link org.springframework.security.core.userdetails.UserDetails}
 * de Spring Security.</p>
 *
 * <h3>Aspectos destacados:</h3>
 * <ul>
 *   <li>{@code @BeforeAll / @AfterAll}: métodos que se ejecutan una sola vez para toda la clase.
 *       Deben ser {@code static}.</li>
 *   <li>Testing de interfaces: verificar que una clase cumple el contrato de una interfaz.</li>
 *   <li>{@code assertInstanceOf}: verifica el tipo de un objeto.</li>
 *   <li>AssertJ {@code extracting} en colecciones.</li>
 * </ul>
 *
 * <h3>UserDetails en Spring Security:</h3>
 * <p>Spring Security usa la interfaz UserDetails para obtener la información
 * del usuario autenticado. Los métodos como {@code getAuthorities()},
 * {@code isEnabled()}, etc. son invocados internamente por el framework.</p>
 *
 * @see User
 * @see org.springframework.security.core.userdetails.UserDetails
 */
@DisplayName("User - Test de entidad con UserDetails (Nivel 3: @BeforeAll, interfaces)")
class UserTest {

    private static User adminUser;
    private static User regularUser;

    /**
     * {@code @BeforeAll} se ejecuta UNA SOLA VEZ antes de todos los tests de la clase.
     * Debe ser {@code static}. Es util para crear datos compartidos que no cambian.
     *
     * <p>CUIDADO: si los tests modifican estos objetos, puede haber interferencias.
     * Solo usar @BeforeAll para datos inmutables o de solo lectura.</p>
     */
    @BeforeAll
    static void setUpAll() {
        adminUser = User.builder()
                .id(1L)
                .username("admin")
                .email("admin@test.com")
                .password("{bcrypt}hashedPassword")
                .role(Role.ROLE_ADMIN)
                .build();

        regularUser = User.builder()
                .id(2L)
                .username("user")
                .email("user@test.com")
                .password("{bcrypt}hashedPassword")
                .role(Role.ROLE_USER)
                .build();
    }

    /**
     * {@code @AfterAll} se ejecuta UNA SOLA VEZ después de todos los tests.
     * Debe ser {@code static}. Util para liberar recursos globales.
     */
    @AfterAll
    static void tearDownAll() {
        adminUser = null;
        regularUser = null;
    }

    // ═══════════════════════════════════════════════════════════════
    // Propiedades básicas de la entidad
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Propiedades básicas")
    class BasicProperties {

        @Test
        @DisplayName("El admin tiene username 'admin'")
        void adminUser_hasCorrectUsername() {
            assertEquals("admin", adminUser.getUsername());
        }

        @Test
        @DisplayName("El usuario tiene email correcto")
        void regularUser_hasCorrectEmail() {
            assertThat(regularUser.getEmail()).isEqualTo("user@test.com");
        }

        @Test
        @DisplayName("Los favoritos se inicializan como Set vacío")
        void favoriteBooks_isEmptyByDefault() {
            User newUser = User.builder().id(3L).username("new").email("new@test.com")
                    .password("pass").role(Role.ROLE_USER).build();

            assertThat(newUser.getFavoriteBooks())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("Las reviews se inicializan como lista vacia")
        void reviews_isEmptyByDefault() {
            User newUser = User.builder().id(3L).username("new").email("new@test.com")
                    .password("pass").role(Role.ROLE_USER).build();

            assertThat(newUser.getReviews())
                    .isNotNull()
                    .isEmpty();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Implementacion de UserDetails (Spring Security)
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Implementacion de UserDetails (Spring Security)")
    class UserDetailsImplementation {

        @Test
        @DisplayName("User implementa la interfaz UserDetails")
        void user_implementsUserDetails() {
            // assertInstanceOf verifica el tipo y devuelve el objeto casteado
            assertInstanceOf(org.springframework.security.core.userdetails.UserDetails.class, adminUser);
        }

        @Test
        @DisplayName("getAuthorities devuelve el rol del admin como ROLE_ADMIN")
        void getAuthorities_returnsAdminRole() {
            Collection<? extends GrantedAuthority> authorities = adminUser.getAuthorities();

            // Verificamos que contiene exactamente un authority con el nombre correcto
            assertThat(authorities)
                    .hasSize(1)
                    .extracting(GrantedAuthority::getAuthority)
                    .containsExactly("ROLE_ADMIN");
        }

        @Test
        @DisplayName("getAuthorities devuelve ROLE_USER para un usuario normal")
        void getAuthorities_returnsUserRole() {
            Collection<? extends GrantedAuthority> authorities = regularUser.getAuthorities();

            assertThat(authorities)
                    .hasSize(1)
                    .extracting(GrantedAuthority::getAuthority)
                    .containsExactly("ROLE_USER");
        }

        @Test
        @DisplayName("La cuenta nunca está expirada (isAccountNonExpired = true)")
        void isAccountNonExpired_returnsTrue() {
            assertTrue(regularUser.isAccountNonExpired());
        }

        @Test
        @DisplayName("La cuenta nunca está bloqueada (isAccountNonLocked = true)")
        void isAccountNonLocked_returnsTrue() {
            assertTrue(regularUser.isAccountNonLocked());
        }

        @Test
        @DisplayName("Las credenciales nunca expiran (isCredentialsNonExpired = true)")
        void isCredentialsNonExpired_returnsTrue() {
            assertTrue(regularUser.isCredentialsNonExpired());
        }

        @Test
        @DisplayName("El usuario siempre está habilitado (isEnabled = true)")
        void isEnabled_returnsTrue() {
            assertTrue(regularUser.isEnabled());
        }

        @Test
        @DisplayName("getUsername devuelve el nombre de usuario (usado por Spring Security para el login)")
        void getUsername_returnsUsername() {
            assertEquals("user", regularUser.getUsername());
        }

        @Test
        @DisplayName("getPassword devuelve la contraseña codificada")
        void getPassword_returnsEncodedPassword() {
            assertThat(regularUser.getPassword()).startsWith("{bcrypt}");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // equals/hashCode
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("equals y hashCode")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Usuarios con el mismo id son iguales")
        void usersWithSameId_areEqual() {
            User u1 = User.builder().id(1L).username("a").email("a@test.com")
                    .password("p").role(Role.ROLE_USER).build();
            User u2 = User.builder().id(1L).username("b").email("b@test.com")
                    .password("q").role(Role.ROLE_ADMIN).build();

            assertEquals(u1, u2);
        }

        @Test
        @DisplayName("Usuarios con distinto id no son iguales")
        void usersWithDifferentId_areNotEqual() {
            assertNotEquals(adminUser, regularUser);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Gestión de favoritos (Set)
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Gestión de favoritos")
    class FavoritesTests {

        @Test
        @DisplayName("Se puede agregar un libro a favoritos")
        void addFavorite_works() {
            User user = User.builder().id(10L).username("test").email("t@t.com")
                    .password("p").role(Role.ROLE_USER).build();
            Book book = Book.builder().id(1L).title("Ficciones").build();

            user.getFavoriteBooks().add(book);

            assertThat(user.getFavoriteBooks())
                    .hasSize(1)
                    .contains(book);
        }

        @Test
        @DisplayName("Al ser un Set, no se puede agregar el mismo libro dos veces")
        void addDuplicateFavorite_isIgnored() {
            User user = User.builder().id(10L).username("test").email("t@t.com")
                    .password("p").role(Role.ROLE_USER).build();
            Book book = Book.builder().id(1L).title("Ficciones").build();

            user.getFavoriteBooks().add(book);
            user.getFavoriteBooks().add(book); // Duplicado

            // Set no permite duplicados
            assertThat(user.getFavoriteBooks()).hasSize(1);
        }

        @Test
        @DisplayName("Se puede eliminar un libro de favoritos")
        void removeFavorite_works() {
            User user = User.builder().id(10L).username("test").email("t@t.com")
                    .password("p").role(Role.ROLE_USER).build();
            Book book = Book.builder().id(1L).title("Ficciones").build();

            user.getFavoriteBooks().add(book);
            user.getFavoriteBooks().remove(book);

            assertThat(user.getFavoriteBooks()).isEmpty();
        }
    }
}
