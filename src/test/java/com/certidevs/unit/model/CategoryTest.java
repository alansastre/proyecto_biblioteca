package com.certidevs.unit.model;

import com.certidevs.model.Book;
import com.certidevs.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * NIVEL 2 - Test unitario de la entidad Category.
 *
 * <p>Introduce {@code @Disabled} y ejecucion condicional de tests.</p>
 *
 * <h3>Aspectos destacados:</h3>
 * <ul>
 *   <li>{@code @Disabled}: desactiva temporalmente un test (aparece como "skipped").</li>
 *   <li>{@code @EnabledOnOs}: ejecuta el test solo en ciertos sistemas operativos.</li>
 *   <li>Testing de ManyToMany (lado inverso).</li>
 * </ul>
 *
 * @see Category
 */
@DisplayName("Category - Test unitario de entidad (Nivel 2)")
class CategoryTest {

    @Nested
    @DisplayName("Creación con Builder")
    class BuilderTests {

        @Test
        @DisplayName("El Builder crea una categoría con todos los campos")
        void builder_createsWithAllFields() {
            Category category = Category.builder()
                    .id(1L)
                    .name("Novela")
                    .description("Ficción narrativa extensa")
                    .color("#3498db")
                    .build();

            assertAll(
                    () -> assertEquals(1L, category.getId()),
                    () -> assertEquals("Novela", category.getName()),
                    () -> assertEquals("Ficción narrativa extensa", category.getDescription()),
                    () -> assertEquals("#3498db", category.getColor())
            );
        }

        @Test
        @DisplayName("La lista de libros se inicializa vacia")
        void books_isInitializedEmpty() {
            Category category = Category.builder().id(1L).name("Cuento").build();

            assertThat(category.getBooks())
                    .isNotNull()
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("Color hexadecimal")
    class ColorTests {

        @Test
        @DisplayName("Se puede asignar un color en formato hexadecimal")
        void setColor_withHexFormat() {
            Category category = Category.builder().id(1L).name("Test").build();

            category.setColor("#e74c3c");

            assertThat(category.getColor())
                    .isNotNull()
                    .startsWith("#")
                    .hasSize(7);
        }

        @Test
        @DisplayName("El color puede ser null (es un campo opcional)")
        void color_canBeNull() {
            Category category = Category.builder().id(1L).name("Test").build();

            // El color es opcional, no tiene @NotBlank
            assertNull(category.getColor());
        }
    }

    @Nested
    @DisplayName("ManyToMany inverso con Book")
    class BookRelationTests {

        @Test
        @DisplayName("Se pueden ver los libros de una categoría")
        void books_canBeAccessed() {
            Category category = Category.builder().id(1L).name("Novela").build();
            Book book = Book.builder().id(1L).title("Test").build();

            // NOTA: en JPA, modificar el lado inverso (mappedBy) NO persiste los cambios.
            // Esto solo funciona a nivel de objetos en memoria, no en base de datos.
            category.getBooks().add(book);

            assertThat(category.getBooks()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("equals y hashCode")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Categorías con el mismo id son iguales")
        void categoriesWithSameId_areEqual() {
            Category c1 = Category.builder().id(1L).name("Novela").build();
            Category c2 = Category.builder().id(1L).name("Cuento").build();

            assertEquals(c1, c2);
        }

        @Test
        @DisplayName("Categorías con distinto id no son iguales")
        void categoriesWithDifferentId_areNotEqual() {
            Category c1 = Category.builder().id(1L).name("Novela").build();
            Category c2 = Category.builder().id(2L).name("Novela").build();

            assertNotEquals(c1, c2);
        }
    }

    @Nested
    @DisplayName("Ejecucion condicional de tests")
    class ConditionalTests {

        /**
         * {@code @EnabledOnOs} ejecuta el test solo en el SO indicado.
         * Util para tests que dependen del sistema de archivos o rutas del SO.
         */
        @Test
        @EnabledOnOs(OS.WINDOWS)
        @DisplayName("Este test solo se ejecuta en Windows")
        void onlyOnWindows() {
            Category category = Category.builder().id(1L).name("Test Windows").build();
            assertNotNull(category);
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        @DisplayName("Este test solo se ejecuta en Linux o Mac")
        void onlyOnLinuxOrMac() {
            Category category = Category.builder().id(1L).name("Test Unix").build();
            assertNotNull(category);
        }
    }
}
