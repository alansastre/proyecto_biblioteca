package com.certidevs.unit.model;

import com.certidevs.model.Author;
import com.certidevs.model.Book;
import com.certidevs.model.Category;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * NIVEL 2 - Test unitario intermedio: testing de una entidad JPA.
 *
 * <p>Prueba la clase {@link Book} como un POJO (Plain Old Java Object), sin base de datos.
 * Se centra en el Builder de Lombok, valores por defecto, equals/hashCode y colecciones.</p>
 *
 * <h3>Aspectos destacados:</h3>
 * <ul>
 *   <li>{@code @Nested}: agrupa tests relacionados dentro de una clase interna.</li>
 *   <li>{@code @BeforeEach}: método que se ejecuta ANTES de cada test (setup).</li>
 *   <li>{@code @AfterEach}: método que se ejecuta DESPUES de cada test (cleanup).</li>
 *   <li>{@code assertAll}: ejecuta TODAS las verificaciones, incluso si alguna falla.</li>
 *   <li>AssertJ ({@code assertThat}): API fluida como alternativa a las assertions de JUnit.</li>
 *   <li>Diferencia entre assertions de JUnit y AssertJ.</li>
 * </ul>
 *
 * <h3>Nota: JUnit assertions vs AssertJ</h3>
 * <p>JUnit: {@code assertEquals(expected, actual)} - parametro esperado va PRIMERO.</p>
 * <p>AssertJ: {@code assertThat(actual).isEqualTo(expected)} - más legible, autocompletado.</p>
 *
 * @see Book
 */
@DisplayName("Book - Test unitario de entidad (Nivel 2: @Nested, @BeforeEach, assertAll)")
class BookTest {

    // ═══════════════════════════════════════════════════════════════
    // GRUPO 1: Creación con Builder de Lombok
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Creación con Builder")
    class BuilderTests {

        @Test
        @DisplayName("El Builder crea un libro con todos los campos correctos")
        void builder_createsBookWithAllFields() {
            // Arrange + Act: creamos un libro con el Builder
            Author author = Author.builder().id(1L).name("Garcia Marquez").build();
            Book book = Book.builder()
                    .id(1L)
                    .title("Cien años de soledad")
                    .price(15.90)
                    .isbn("978-3-16-148410-0")
                    .pages(471)
                    .language("Español")
                    .synopsis("Historia de la familia Buendia")
                    .publishDate(LocalDate.of(1967, 5, 30))
                    .author(author)
                    .build();

            // Assert con assertAll: ejecuta TODAS las comprobaciones
            // Si alguna falla, el informe muestra TODAS las que fallaron (no se detiene en la primera)
            assertAll("Propiedades del libro",
                    () -> assertEquals(1L, book.getId()),
                    () -> assertEquals("Cien años de soledad", book.getTitle()),
                    () -> assertEquals(15.90, book.getPrice()),
                    () -> assertEquals("978-3-16-148410-0", book.getIsbn()),
                    () -> assertEquals(471, book.getPages()),
                    () -> assertEquals("Español", book.getLanguage()),
                    () -> assertEquals("Historia de la familia Buendia", book.getSynopsis()),
                    () -> assertEquals(LocalDate.of(1967, 5, 30), book.getPublishDate()),
                    () -> assertNotNull(book.getAuthor()),
                    () -> assertEquals("Garcia Marquez", book.getAuthor().getName())
            );
        }

        @Test
        @DisplayName("El Builder asigna valores por defecto: available=true, listas vacias")
        void builder_assignsDefaultValues() {
            Book book = Book.builder().title("Test").build();

            // @Builder.Default en Book hace que available sea true por defecto
            assertAll("Valores por defecto",
                    () -> assertTrue(book.getAvailable(), "available debe ser true por defecto"),
                    () -> assertNotNull(book.getCategories(), "categories no debe ser null"),
                    () -> assertTrue(book.getCategories().isEmpty(), "categories debe estar vacia"),
                    () -> assertNotNull(book.getReviews(), "reviews no debe ser null"),
                    () -> assertTrue(book.getReviews().isEmpty(), "reviews debe estar vacia"),
                    () -> assertNotNull(book.getPurchases(), "purchases no debe ser null"),
                    () -> assertTrue(book.getPurchases().isEmpty(), "purchases debe estar vacia")
            );
        }

        @Test
        @DisplayName("El constructor sin argumentos crea un Book con id null")
        void noArgConstructor_createsBookWithNullId() {
            Book book = new Book();

            assertNull(book.getId());
            assertNull(book.getTitle());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // GRUPO 2: Getters y Setters (generados por Lombok @Getter/@Setter)
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Getters y Setters")
    class GetterSetterTests {

        private Book book;

        /**
         * {@code @BeforeEach} se ejecuta antes de CADA test de este grupo @Nested.
         * Garantiza que cada test empieza con un objeto limpio (aislamiento de tests).
         */
        @BeforeEach
        void setUp() {
            book = new Book();
        }

        @Test
        @DisplayName("setTitle/getTitle funciona correctamente")
        void setAndGetTitle_works() {
            book.setTitle("Ficciones");

            assertEquals("Ficciones", book.getTitle());
        }

        @Test
        @DisplayName("setPrice/getPrice funciona correctamente")
        void setAndGetPrice_works() {
            book.setPrice(10.50);

            assertEquals(10.50, book.getPrice());
        }

        @Test
        @DisplayName("Se pueden modificar todos los campos básicos")
        void allBasicFields_canBeSet() {
            book.setTitle("Rayuela");
            book.setPrice(14.00);
            book.setIsbn("978-84-206-3314-8");
            book.setPages(600);
            book.setLanguage("Español");
            book.setAvailable(false);

            // Usando AssertJ como alternativa más fluida
            assertThat(book.getTitle()).isEqualTo("Rayuela");
            assertThat(book.getPrice()).isEqualTo(14.00);
            assertThat(book.getIsbn()).isEqualTo("978-84-206-3314-8");
            assertThat(book.getPages()).isEqualTo(600);
            assertThat(book.getLanguage()).isEqualTo("Español");
            assertThat(book.getAvailable()).isFalse();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // GRUPO 3: equals y hashCode (patron JPA recomendado)
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("equals y hashCode (patron JPA)")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Dos libros con el mismo id son iguales")
        void booksWithSameId_areEqual() {
            Book book1 = Book.builder().id(1L).title("Libro 1").build();
            Book book2 = Book.builder().id(1L).title("Libro 2").build();

            // Mismo id = iguales, aunque el título sea diferente
            assertEquals(book1, book2);
        }

        @Test
        @DisplayName("Dos libros con distinto id no son iguales")
        void booksWithDifferentId_areNotEqual() {
            Book book1 = Book.builder().id(1L).title("Libro").build();
            Book book2 = Book.builder().id(2L).title("Libro").build();

            // Distinto id = no iguales, aunque el título sea el mismo
            assertNotEquals(book1, book2);
        }

        @Test
        @DisplayName("Un libro con id null no es igual a otro con id null")
        void booksWithNullId_areNotEqual() {
            // Antes de persistir, las entidades JPA tienen id=null
            Book book1 = Book.builder().title("Libro").build();
            Book book2 = Book.builder().title("Libro").build();

            // Dos entidades nuevas (sin id) NO deben ser iguales
            assertNotEquals(book1, book2);
        }

        @Test
        @DisplayName("Un libro es igual a si mismo (reflexividad)")
        void book_isEqualToItself() {
            Book book = Book.builder().id(1L).title("Test").build();

            assertEquals(book, book);
        }

        @Test
        @DisplayName("Un libro no es igual a null")
        void book_isNotEqualToNull() {
            Book book = Book.builder().id(1L).title("Test").build();

            assertNotEquals(null, book);
        }

        @Test
        @DisplayName("Un libro no es igual a un objeto de otro tipo")
        void book_isNotEqualToOtherType() {
            Book book = Book.builder().id(1L).title("Test").build();

            assertNotEquals("no soy un libro", book);
        }

        @Test
        @DisplayName("hashCode es consistente (mismo valor en cada invocacion)")
        void hashCode_isConsistent() {
            Book book = Book.builder().id(1L).title("Test").build();

            int hash1 = book.hashCode();
            int hash2 = book.hashCode();

            assertEquals(hash1, hash2, "hashCode debe devolver el mismo valor siempre");
        }

        @Test
        @DisplayName("Libros iguales tienen el mismo hashCode")
        void equalBooks_haveSameHashCode() {
            Book book1 = Book.builder().id(1L).title("Libro A").build();
            Book book2 = Book.builder().id(1L).title("Libro B").build();

            assertEquals(book1.hashCode(), book2.hashCode());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // GRUPO 4: Gestión de colecciones (categorías del libro)
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Gestión de colecciones")
    class CollectionTests {

        private Book book;
        private Category novela;
        private Category cuento;

        @BeforeEach
        void setUp() {
            book = Book.builder().id(1L).title("Libro de prueba").build();
            novela = Category.builder().id(1L).name("Novela").build();
            cuento = Category.builder().id(2L).name("Cuento").build();
        }

        @Test
        @DisplayName("Se pueden agregar categorías a un libro")
        void addCategory_works() {
            book.getCategories().add(novela);

            assertThat(book.getCategories()).hasSize(1);
            assertThat(book.getCategories()).contains(novela);
        }

        @Test
        @DisplayName("Se pueden agregar múltiples categorías")
        void addMultipleCategories_works() {
            book.getCategories().add(novela);
            book.getCategories().add(cuento);

            assertThat(book.getCategories())
                    .hasSize(2)
                    .containsExactlyInAnyOrder(novela, cuento);
        }

        @Test
        @DisplayName("Se puede eliminar una categoría de un libro")
        void removeCategory_works() {
            book.getCategories().add(novela);
            book.getCategories().add(cuento);

            book.getCategories().remove(novela);

            assertThat(book.getCategories())
                    .hasSize(1)
                    .containsOnly(cuento);
        }

        @Test
        @DisplayName("Se puede reemplazar toda la lista de categorías")
        void setCategories_replacesEntireList() {
            book.getCategories().add(novela);

            List<Category> newCategories = new ArrayList<>(List.of(cuento));
            book.setCategories(newCategories);

            assertThat(book.getCategories())
                    .hasSize(1)
                    .containsOnly(cuento);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // GRUPO 5: Relacion ManyToOne con Author
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Relacion con Author")
    class AuthorRelationTests {

        @Test
        @DisplayName("Se puede asignar un autor al libro")
        void setAuthor_works() {
            Author author = Author.builder().id(1L).name("Borges").build();
            Book book = Book.builder().id(1L).title("Ficciones").author(author).build();

            assertThat(book.getAuthor()).isNotNull();
            assertThat(book.getAuthor().getName()).isEqualTo("Borges");
        }

        @Test
        @DisplayName("Se puede cambiar el autor de un libro")
        void changeAuthor_works() {
            Author author1 = Author.builder().id(1L).name("Borges").build();
            Author author2 = Author.builder().id(2L).name("Cortazar").build();

            Book book = Book.builder().id(1L).title("Libro").author(author1).build();
            book.setAuthor(author2);

            assertThat(book.getAuthor().getName()).isEqualTo("Cortazar");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // GRUPO 6: toString (excluye asociaciones para evitar bucles)
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("toString")
    class ToStringTests {

        @Test
        @DisplayName("toString incluye el título del libro")
        void toString_containsTitle() {
            Book book = Book.builder().id(1L).title("Cien años").build();

            String result = book.toString();

            assertThat(result).contains("Cien años");
        }

        @Test
        @DisplayName("toString NO incluye el autor (evita LazyInitializationException)")
        void toString_excludesAuthor() {
            // @ToString(exclude = {"author", ...}) en la entidad
            // Esto evita bucles infinitos y LazyInitializationException fuera de transacciones
            Book book = Book.builder().id(1L).title("Test").build();

            String result = book.toString();

            assertThat(result).doesNotContain("author=");
        }
    }
}
