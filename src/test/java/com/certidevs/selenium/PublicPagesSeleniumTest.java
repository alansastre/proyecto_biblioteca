package com.certidevs.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Selenium - Pantallas públicas")
class PublicPagesSeleniumTest extends BaseSeleniumTest {

    @ParameterizedTest(name = "{1}")
    @CsvSource({
            "/authors,Autores",
            "/categories,Categor",
            "/reviews,Reviews",
            "/login,Iniciar",
            "/register,Registrarse"
    })
    @DisplayName("Las pantallas públicas principales cargan correctamente")
    void publicScreens_load(String path, String headingFragment) {
        visit(path);
        assertThat(waitFor(By.tagName("h1")).getText()).contains(headingFragment);
    }

    @Test
    @DisplayName("Desde el catálogo se puede abrir la ficha de un libro")
    void bookDetail_isReachableFromCatalog() {
        visit("/books");
        click(By.cssSelector("[data-testid='book-cards'] .card-title a"));

        assertCurrentUrlContains("/books/");
        assertThat(bodyText()).contains("Ficha completa del libro");
    }

    @Test
    @DisplayName("Desde el listado de autores se puede abrir una ficha de autor")
    void authorDetail_isReachableFromList() {
        visit("/authors");
        click(By.cssSelector("table tbody tr:first-child td:nth-child(2) a"));

        assertCurrentUrlContains("/authors/");
        assertThat(bodyText()).contains("Consulta la ficha del autor");
    }

    @Test
    @DisplayName("Desde el listado de categorías se puede abrir una ficha de categoría")
    void categoryDetail_isReachableFromList() {
        visit("/categories");
        click(By.cssSelector("table tbody tr:first-child td:nth-child(2) a"));

        assertCurrentUrlContains("/categories/");
        assertThat(bodyText()).contains("Libros");
    }
}
