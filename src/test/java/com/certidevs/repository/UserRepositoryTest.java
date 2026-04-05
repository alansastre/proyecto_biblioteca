package com.certidevs.repository;

import com.certidevs.model.Role;
import com.certidevs.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración para {@link UserRepository} con {@code @DataJpaTest}.
 *
 * <p>Verifica los métodos de consulta personalizados del repositorio de usuarios,
 * incluyendo búsqueda por username/email y existencia.</p>
 *
 * @see UserRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository - Tests de integración JPA")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("{bcrypt}encodedPassword")
                .role(Role.ROLE_USER)
                .build());
    }

    @Nested
    @DisplayName("findByUsername")
    class FindByUsernameTests {

        @Test
        @DisplayName("Encuentra un usuario existente por username")
        void findByUsername_returnsUser() {
            Optional<User> result = userRepository.findByUsername("testuser");

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("Devuelve Optional vacío si el username no existe")
        void findByUsername_returnsEmptyForNonExistent() {
            Optional<User> result = userRepository.findByUsername("inexistente");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByEmail")
    class FindByEmailTests {

        @Test
        @DisplayName("Encuentra un usuario existente por email")
        void findByEmail_returnsUser() {
            Optional<User> result = userRepository.findByEmail("test@example.com");

            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Devuelve Optional vacío si el email no existe")
        void findByEmail_returnsEmptyForNonExistent() {
            Optional<User> result = userRepository.findByEmail("noexiste@test.com");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByUsername / existsByEmail")
    class ExistsTests {

        @Test
        @DisplayName("existsByUsername: devuelve true para un username existente")
        void existsByUsername_returnsTrue() {
            assertThat(userRepository.existsByUsername("testuser")).isTrue();
        }

        @Test
        @DisplayName("existsByUsername: devuelve false para un username inexistente")
        void existsByUsername_returnsFalse() {
            assertThat(userRepository.existsByUsername("noexiste")).isFalse();
        }

        @Test
        @DisplayName("existsByEmail: devuelve true para un email existente")
        void existsByEmail_returnsTrue() {
            assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        }

        @Test
        @DisplayName("existsByEmail: devuelve false para un email inexistente")
        void existsByEmail_returnsFalse() {
            assertThat(userRepository.existsByEmail("noexiste@test.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("Operaciones CRUD básicas")
    class CrudTests {

        @Test
        @DisplayName("save: persiste el usuario con un ID generado")
        void save_persistsWithGeneratedId() {
            assertThat(savedUser.getId()).isNotNull();
        }

        @Test
        @DisplayName("findById: recupera el usuario con todos sus campos")
        void findById_returnsFullUser() {
            Optional<User> found = userRepository.findById(savedUser.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo("testuser");
            assertThat(found.get().getRole()).isEqualTo(Role.ROLE_USER);
        }

        @Test
        @DisplayName("delete: elimina el usuario de la BD")
        void delete_removesUser() {
            userRepository.delete(savedUser);

            assertThat(userRepository.findById(savedUser.getId())).isEmpty();
        }

        @Test
        @DisplayName("Se pueden guardar usuarios con diferentes roles")
        void save_differentRoles() {
            User admin = userRepository.save(User.builder()
                    .username("admin2")
                    .email("admin2@test.com")
                    .password("{bcrypt}pass")
                    .role(Role.ROLE_ADMIN)
                    .build());

            assertThat(admin.getRole()).isEqualTo(Role.ROLE_ADMIN);
            assertThat(userRepository.findAll()).hasSize(2);
        }
    }
}
