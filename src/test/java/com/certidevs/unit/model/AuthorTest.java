package com.certidevs.unit.model;

import com.certidevs.model.Author;
import com.certidevs.model.Book;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * NIVEL 2 - Test unitario de la entidad Author.
 *
 * <p>Prueba el autor como POJO, incluyendo la relacion OneToMany con Book.</p>
 *
 * <h3>Aspectos destacados:</h3>
 * <ul>
 *   <li>{@code @BeforeEach}: preparacion de datos para cada test.</li>
 *   <li>{@code @AfterEach}: limpieza tras cada test (concepto, aunque aqui no es necesario).</li>
 *   <li>Testing de relaciones bidireccionales (Author ↔ Book).</li>
 *   <li>AssertJ extracting: extraer propiedades de una lista para verificarlas.</li>
 * </ul>
 *
 * @see Author
 * @see Book
 */
@DisplayName("Author - Test unitario de entidad con relaciones (Nivel 2)")
class AuthorTest {

    private Author author;

    /**
     * @BeforeEach se ejecuta antes de CADA test.
     * Cada test recibe un autor limpio, garantizando aislamiento.
     */
    @BeforeEach
    void setUp() {
        author = Author.builder()
                .id(1L)
                .name("Gabriel Garcia Marquez")
                .bio("Premio Nobel de Literatura 1982")
                .birthDate(LocalDate.of(1927, 3, 6))
                .nationality("Colombiana")
                .build();
    }

    /**
     * @AfterEach se ejecuta después de CADA test.
     * Util para liberar recursos (cerrar conexiones, borrar archivos temporales, etc.).
     * En este caso no es necesario, pero se incluye como ejemplo.
     */
    @AfterEach
    void tearDown() {
        author = null; // Liberar referencia (el GC se encargaria de todas formas)
    }

    // ── Propiedades básicas ─────────────────────────────────────

    @Test
    @DisplayName("El autor tiene todos los campos asignados correctamente")
    void author_hasAllFieldsSet() {
        assertAll("Campos del autor",
                () -> assertThat(author.getId()).isEqualTo(1L),
                () -> assertThat(author.getName()).isEqualTo("Gabriel Garcia Marquez"),
                () -> assertThat(author.getBio()).contains("Nobel"),
                () -> assertThat(author.getBirthDate()).isEqualTo(LocalDate.of(1927, 3, 6)),
                () -> assertThat(author.getNationality()).isEqualTo("Colombiana")
        );
    }

    @Test
    @DisplayName("La lista de libros se inicializa vacia por defecto")
    void books_isInitializedEmpty() {
        assertThat(author.getBooks())
                .isNotNull()
                .isEmpty();
    }

    // ── Relacion OneToMany con Book ─────────────────────────────

    @Nested
    @DisplayName("Relacion OneToMany con Book")
    class BookRelationTests {

        @Test
        @DisplayName("Se puede agregar un libro al autor")
        void addBook_works() {
            Book book = Book.builder().id(1L).title("Cien años de soledad").author(author).build();

            author.getBooks().add(book);

            assertThat(author.getBooks()).hasSize(1);
            assertThat(author.getBooks().getFirst().getTitle()).isEqualTo("Cien años de soledad");
        }

        @Test
        @DisplayName("Se pueden agregar varios libros al autor")
        void addMultipleBooks_works() {
            Book book1 = Book.builder().id(1L).title("Cien años de soledad").author(author).build();
            Book book2 = Book.builder().id(2L).title("El amor en los tiempos del colera").author(author).build();

            author.getBooks().add(book1);
            author.getBooks().add(book2);

            // AssertJ extracting: extrae una propiedad de cada elemento de la lista
            assertThat(author.getBooks())
                    .hasSize(2)
                    .extracting(Book::getTitle)
                    .containsExactly("Cien años de soledad", "El amor en los tiempos del colera");
        }

        @Test
        @DisplayName("Se puede eliminar un libro de la lista del autor")
        void removeBook_works() {
            Book book1 = Book.builder().id(1L).title("Libro 1").author(author).build();
            Book book2 = Book.builder().id(2L).title("Libro 2").author(author).build();
            author.getBooks().add(book1);
            author.getBooks().add(book2);

            author.getBooks().remove(book1);

            assertThat(author.getBooks())
                    .hasSize(1)
                    .extracting(Book::getTitle)
                    .containsOnly("Libro 2");
        }

        @Test
        @DisplayName("Se puede reemplazar toda la lista de libros")
        void setBooks_replacesEntireList() {
            author.getBooks().add(Book.builder().id(1L).title("Viejo").author(author).build());

            List<Book> newBooks = new ArrayList<>();
            newBooks.add(Book.builder().id(2L).title("Nuevo").author(author).build());
            author.setBooks(newBooks);

            assertThat(author.getBooks())
                    .hasSize(1)
                    .extracting(Book::getTitle)
                    .containsOnly("Nuevo");
        }
    }

    // ── equals/hashCode ─────────────────────────────────────────

    @Nested
    @DisplayName("equals y hashCode")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Autores con el mismo id son iguales")
        void authorsWithSameId_areEqual() {
            Author other = Author.builder().id(1L).name("Otro nombre").build();

            assertThat(author).isEqualTo(other);
        }

        @Test
        @DisplayName("Autores con distinto id son distintos")
        void authorsWithDifferentId_areNotEqual() {
            Author other = Author.builder().id(2L).name("Gabriel Garcia Marquez").build();

            assertThat(author).isNotEqualTo(other);
        }

        @Test
        @DisplayName("Autores sin id (nuevos, no persistidos) son distintos entre si")
        void authorsWithoutId_areNotEqual() {
            Author a1 = Author.builder().name("Autor 1").build();
            Author a2 = Author.builder().name("Autor 2").build();

            assertThat(a1).isNotEqualTo(a2);
        }
    }

    // ── Setters ─────────────────────────────────────────────────

    @Test
    @DisplayName("Se pueden modificar los campos del autor con setters")
    void setters_modifyFields() {
        author.setName("Isabel Allende");
        author.setNationality("Chilena");
        author.setBirthDate(LocalDate.of(1942, 8, 2));

        assertAll(
                () -> assertThat(author.getName()).isEqualTo("Isabel Allende"),
                () -> assertThat(author.getNationality()).isEqualTo("Chilena"),
                () -> assertThat(author.getBirthDate()).isEqualTo(LocalDate.of(1942, 8, 2))
        );
    }
}
