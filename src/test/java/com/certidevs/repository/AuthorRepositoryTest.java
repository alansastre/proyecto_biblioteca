package com.certidevs.repository;

import com.certidevs.model.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración para {@link AuthorRepository} usando {@code @DataJpaTest}.
 Test
 *
 * <p>{@code @DataJpaTest} es un "test slice" de Spring Boot que:</p>
 * <ul>
 *   <li>Solo carga los componentes de JPA (entidades, repositorios, EntityManager).</li>
 *   <li>NO carga controladores, servicios, ni la configuración de seguridad.</li>
 *   <li>Configura automáticamente una BD H2 en memoria para el test.</li>
 *   <li>Cada test se ejecuta en una transaccion que se revierte al final (rollback automático).</li>
 * </ul>
 *
 * <h3>AssertJ:</h3>
 * <p>Se usa AssertJ ({@code assertThat}) en lugar de JUnit assertions por su API fluida
 * y mensajes de error más descriptivos.</p>
 *
 * @see AuthorRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("AuthorRepository - Tests de integración JPA")
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("findByNameContainingIgnoreCase: busca autores por nombre parcial sin importar mayúsculas")
    void findByNameContainingIgnoreCase_returnsMatchingAuthors() {
        // Given: dos autores en la BD
        authorRepository.save(Author.builder().name("Gabriel Garcia Marquez").nationality("Colombiana").build());
        authorRepository.save(Author.builder().name("Otro Autor").nationality("Argentina").build());

        // When: buscamos por "gabriel" (minúsculas)
        List<Author> result = authorRepository.findByNameContainingIgnoreCase("gabriel");

        // Then: solo devuelve el que coincide
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).contains("Gabriel");
    }

    @Test
    @DisplayName("findByNationalityIgnoreCase: busca autores por nacionalidad exacta case-insensitive")
    void findByNationalityIgnoreCase_returnsMatchingAuthors() {
        authorRepository.save(Author.builder().name("Borges").nationality("Argentina").build());
        authorRepository.save(Author.builder().name("Allende").nationality("Chilena").build());
        authorRepository.save(Author.builder().name("Cortazar").nationality("Argentina").build());

        List<Author> result = authorRepository.findByNationalityIgnoreCase("argentina");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Author::getNationality)
                .allMatch(n -> n.equalsIgnoreCase("Argentina"));
    }

    @Test
    @DisplayName("save y findById: verifica la persistencia completa del autor")
    void save_persistsAuthorWithAllFields() {
        Author author = Author.builder()
                .name("Test Author")
                .bio("Biografia de prueba")
                .birthDate(LocalDate.of(1990, 1, 1))
                .nationality("Española")
                .build();

        Author saved = authorRepository.save(author);

        assertThat(saved.getId()).isNotNull();

        Optional<Author> found = authorRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Author");
        assertThat(found.get().getBio()).isEqualTo("Biografia de prueba");
        assertThat(found.get().getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase: devuelve lista vacia si no hay coincidencias")
    void findByNameContainingIgnoreCase_returnsEmptyWhenNoMatch() {
        authorRepository.save(Author.builder().name("Gabriel Garcia").nationality("CO").build());

        List<Author> result = authorRepository.findByNameContainingIgnoreCase("inexistente");

        assertThat(result).isEmpty();
    }
}
