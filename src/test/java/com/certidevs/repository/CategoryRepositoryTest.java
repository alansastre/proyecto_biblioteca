package com.certidevs.repository;

import com.certidevs.model.Author;
import com.certidevs.model.Book;
import com.certidevs.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración para {@link CategoryRepository}.
 *
 * <p>Verifica las consultas personalizadas del repositorio de categorías,
 * incluyendo la carga eager con {@code @EntityGraph}.</p>
 *
 * @see CategoryRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CategoryRepository - Tests de integración JPA")
class CategoryRepositoryTest {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private EntityManager entityManager;

    private Category novela;
    private Category cuento;

    @BeforeEach
    void setUp() {
        novela = categoryRepository.save(Category.builder()
                .name("Novela")
                .description("Ficción narrativa")
                .color("#3498db")
                .build());

        cuento = categoryRepository.save(Category.builder()
                .name("Cuento")
                .description("Relatos cortos")
                .color("#2ecc71")
                .build());
    }

    @Nested
    @DisplayName("findByNameContainingIgnoreCase")
    class FindByNameTests {

        @Test
        @DisplayName("Busca categorías por nombre parcial (case-insensitive)")
        void findByNameContaining_returnsMatches() {
            List<Category> result = categoryRepository.findByNameContainingIgnoreCase("novel");

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Novela");
        }

        @Test
        @DisplayName("La búsqueda ignora mayúsculas/minúsculas")
        void findByNameContaining_isCaseInsensitive() {
            List<Category> result = categoryRepository.findByNameContainingIgnoreCase("CUENTO");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Devuelve lista vacia si no hay coincidencias")
        void findByNameContaining_returnsEmptyForNoMatch() {
            List<Category> result = categoryRepository.findByNameContainingIgnoreCase("inexistente");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findDetailedById (con @EntityGraph)")
    class FindDetailedTests {

        @Test
        @DisplayName("Carga la categoría con sus libros (eager loading)")
        void findDetailedById_loadsBooksEagerly() {
            // Primero creamos un libro con esta categoría
            Author author = authorRepository.save(Author.builder().name("Test Author").build());
            bookRepository.save(Book.builder()
                    .title("Libro de novela")
                    .author(author)
                    .categories(List.of(novela))
                    .build());

            // Flush y clear para forzar que la siguiente consulta vaya a BD real
            entityManager.flush();
            entityManager.clear();

            Optional<Category> result = categoryRepository.findDetailedById(novela.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getBooks()).hasSize(1);
            assertThat(result.get().getBooks().getFirst().getTitle()).isEqualTo("Libro de novela");
        }

        @Test
        @DisplayName("Devuelve Optional vacío si la categoría no existe")
        void findDetailedById_returnsEmptyForNonExistent() {
            Optional<Category> result = categoryRepository.findDetailedById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Operaciones CRUD básicas")
    class CrudTests {

        @Test
        @DisplayName("findAll: devuelve todas las categorías")
        void findAll_returnsAll() {
            List<Category> all = categoryRepository.findAll();

            assertThat(all).hasSize(2);
        }

        @Test
        @DisplayName("save: actualiza una categoría existente")
        void save_updatesExistingCategory() {
            novela.setColor("#ff0000");
            categoryRepository.save(novela);

            Optional<Category> updated = categoryRepository.findById(novela.getId());
            assertThat(updated).isPresent();
            assertThat(updated.get().getColor()).isEqualTo("#ff0000");
        }

        @Test
        @DisplayName("deleteById: elimina la categoría")
        void deleteById_removesCategory() {
            categoryRepository.deleteById(cuento.getId());

            assertThat(categoryRepository.findAll()).hasSize(1);
        }
    }
}
