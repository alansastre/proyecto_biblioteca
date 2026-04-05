package com.certidevs.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke tests de interfaz para la página principal y la navegación pública.
 */
@DisplayName("Selenium - Tests de interfaz de usuario (E2E)")
class HomeSeleniumTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Página principal carga y muestra 'Biblioteca'")
    void homePage_loadsAndShowsBiblioteca() {
        visit("/");
        assertThat(bodyText()).contains("Biblioteca");
    }

    @Test
    @DisplayName("Página principal muestra enlaces de navegación")
    void homePage_hasNavigationLinks() {
        visit("/");
        assertThat(waitFor(By.cssSelector("[data-testid='nav-books']")).getText()).contains("Libros");
        assertThat(waitFor(By.cssSelector("[data-testid='nav-authors']")).getText()).contains("Autores");
    }

    @Test
    @DisplayName("Listado de libros es accesible sin login")
    void booksPage_isAccessibleWithoutLogin() {
        visit("/books");
        assertThat(waitFor(By.tagName("h1")).getText()).contains("Libros");
    }
}
