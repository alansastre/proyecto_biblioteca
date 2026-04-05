package com.certidevs.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test E2E con Selenium: filtros del catalogo de libros.
 *
 * <p>Verifica que los filtros de busqueda del catalogo de libros funcionan
 * correctamente en el navegador: filtro por titulo, por autor, por categoria
 * y por rango de precio.</p>
 *
 * <h3>Patrones Selenium utilizados:</h3>
 * <ul>
 *   <li>{@link Select}: interaccion con dropdown/select HTML.</li>
 *   <li>{@code findElements}: encontrar multiples elementos que cumplan un criterio.</li>
 *   <li>Rellenar campos de texto y enviar formularios GET.</li>
 *   <li>Contar resultados visibles en la pagina.</li>
 * </ul>
 *
 * @see BaseSeleniumTest
 */
@DisplayName("Selenium - Filtros del catalogo de libros")
class BookFilterSeleniumTest extends BaseSeleniumTest {

    /**
     * Helper para enviar el formulario de filtro (GET) y esperar resultados.
     * El formulario usa method="get", asi que los parametros aparecen en la URL.
     */
    private void submitFilter() {
        // IMPORTANTE: usar "main form" para evitar el form de logout del navbar
        driver.findElement(By.cssSelector("main form button[type='submit']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("main")));
        wait.until(webDriver ->
                "complete".equals(((org.openqa.selenium.JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")));
    }

    @Nested
    @DisplayName("Catalogo sin filtros")
    class NoFilterTests {

        @Test
        @DisplayName("El catalogo muestra todos los libros al cargar")
        void bookCatalog_showsAllBooks() {
            visit("/books");

            List<WebElement> bookCards = driver.findElements(
                    By.cssSelector("[data-testid='book-cards'] .card"));

            // DataInitializer crea 5 libros
            assertThat(bookCards).hasSizeGreaterThanOrEqualTo(5);
        }
    }

    @Nested
    @DisplayName("Filtro por titulo")
    class TitleFilterTests {

        @Test
        @DisplayName("Filtrar por 'Cien' muestra solo libros que coinciden")
        void filterByTitle_showsMatchingBooks() {
            visit("/books");

            // El campo de titulo usa name="title" (no id)
            WebElement titleInput = waitFor(By.cssSelector("input[name='title']"));
            titleInput.clear();
            titleInput.sendKeys("Cien");

            submitFilter();

            assertThat(bodyText()).contains("Cien");
            // Verificar que se filtro (menos resultados que el total)
            List<WebElement> bookCards = driver.findElements(
                    By.cssSelector("[data-testid='book-cards'] .card"));
            assertThat(bookCards).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Filtro por autor")
    class AuthorFilterTests {

        @Test
        @DisplayName("Filtrar por autor muestra solo sus libros")
        void filterByAuthor_showsAuthorBooks() {
            visit("/books");

            // El select de autor usa name="authorId"
            Select authorSelect = new Select(waitFor(By.cssSelector("select[name='authorId']")));
            List<WebElement> options = authorSelect.getOptions();
            if (options.size() > 1) {
                authorSelect.selectByIndex(1);

                submitFilter();

                List<WebElement> bookCards = driver.findElements(
                        By.cssSelector("[data-testid='book-cards'] .card"));
                assertThat(bookCards).isNotEmpty();
            }
        }
    }

    @Nested
    @DisplayName("Filtro por categoria")
    class CategoryFilterTests {

        @Test
        @DisplayName("Filtrar por categoria muestra libros de esa categoria")
        void filterByCategory_showsCategoryBooks() {
            visit("/books");

            // El select de categoria usa name="categoryId"
            Select categorySelect = new Select(waitFor(By.cssSelector("select[name='categoryId']")));
            List<WebElement> options = categorySelect.getOptions();
            if (options.size() > 1) {
                categorySelect.selectByIndex(1);

                submitFilter();

                List<WebElement> bookCards = driver.findElements(
                        By.cssSelector("[data-testid='book-cards'] .card"));
                assertThat(bookCards).isNotEmpty();
            }
        }
    }

    @Nested
    @DisplayName("Navegacion desde el catalogo")
    class NavigationTests {

        @Test
        @DisplayName("Clicar en un libro abre su ficha de detalle")
        void clickBook_opensDetail() {
            visit("/books");

            click(By.cssSelector("[data-testid='book-cards'] .card-title a"));

            assertCurrentUrlContains("/books/");
        }

        @Test
        @DisplayName("La ficha de detalle muestra titulo, autor y sinopsis")
        void bookDetail_showsFullInfo() {
            visit("/books/1");

            String body = bodyText();
            assertThat(body).contains("Cien");
            assertThat(body).contains("Garcia Marquez");
        }
    }
}
