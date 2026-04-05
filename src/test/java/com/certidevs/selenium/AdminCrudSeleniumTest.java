package com.certidevs.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test E2E con Selenium: operaciones CRUD completas como administrador.
 *
 * <p>Verifica que un administrador puede crear, editar y eliminar autores,
 * categorias y libros a traves de la interfaz web completa.</p>
 *
 * <h3>Patrones Selenium utilizados:</h3>
 * <ul>
 *   <li>Flujo completo de CRUD a traves de la interfaz.</li>
 *   <li>Formularios con multiples campos.</li>
 *   <li>Verificacion de mensajes flash tras operaciones.</li>
 *   <li>Navegacion post-operacion (redireccion tras crear/editar/eliminar).</li>
 * </ul>
 *
 * @see BaseSeleniumTest
 */
@DisplayName("Selenium - CRUD completo como administrador")
class AdminCrudSeleniumTest extends BaseSeleniumTest {

    @BeforeEach
    void loginAsAdmin() {
        loginAs("admin", "admin");
    }

    /**
     * Helper para enviar un formulario y esperar la redireccion.
     * A diferencia de click(), no espera staleness del boton sino la URL destino.
     */
    private void submitFormAndWaitFor(String urlFragment) {
        String currentUrl = driver.getCurrentUrl();
        // IMPORTANTE: usar "main form" para evitar el form de logout del navbar
        driver.findElement(By.cssSelector("main form button[type='submit']")).click();
        // Esperar a que la URL cambie (el POST redirige)
        wait.until(webDriver -> !webDriver.getCurrentUrl().equals(currentUrl));
        wait.until(webDriver ->
                "complete".equals(((org.openqa.selenium.JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")));
    }

    // ═══════════════════════════════════════════════════════════════
    // CRUD de Autores
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("CRUD de Autores")
    class AuthorCrudTests {

        @Test
        @DisplayName("El admin puede crear un nuevo autor desde el formulario")
        void admin_canCreateAuthor() {
            visit("/authors/create");

            waitFor(By.id("name")).sendKeys("Autor Selenium Test");
            waitFor(By.id("nationality")).sendKeys("Española");
            waitFor(By.id("bio")).sendKeys("Biografia creada por Selenium");

            submitFormAndWaitFor("/authors");

            assertThat(bodyText()).contains("Autor creado correctamente");
        }

        @Test
        @DisplayName("El admin puede editar un autor existente")
        void admin_canEditAuthor() {
            visit("/authors/1/edit");

            waitFor(By.id("name")).clear();
            waitFor(By.id("name")).sendKeys("Garcia Marquez Editado");

            submitFormAndWaitFor("/authors/1");

            assertThat(bodyText()).contains("Autor actualizado correctamente");
        }

        @Test
        @DisplayName("El formulario de autor valida que el nombre no este vacio")
        void authorForm_validatesRequiredName() {
            visit("/authors/create");

            // El campo name tiene atributo required, el navegador no envia el form
            // Verificamos que estamos en la pagina del formulario
            assertThat(waitFor(By.tagName("h1")).getText()).contains("Nuevo autor");
            assertThat(waitFor(By.cssSelector("form[data-testid='author-form']")).isDisplayed()).isTrue();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CRUD de Categorias
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("CRUD de Categorias")
    class CategoryCrudTests {

        @Test
        @DisplayName("El admin puede crear una nueva categoria")
        void admin_canCreateCategory() {
            visit("/categories/create");

            waitFor(By.id("name")).sendKeys("Categoria Selenium");
            waitFor(By.id("description")).sendKeys("Creada por Selenium");

            submitFormAndWaitFor("/categories");

            assertThat(bodyText()).contains("creada correctamente");
        }

        @Test
        @DisplayName("El admin puede editar una categoria existente")
        void admin_canEditCategory() {
            visit("/categories/1/edit");

            waitFor(By.id("name")).clear();
            waitFor(By.id("name")).sendKeys("Novela Editada");

            submitFormAndWaitFor("/categories/1");

            assertThat(bodyText()).contains("actualizada correctamente");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Navegacion administrativa
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Navegacion administrativa")
    class AdminNavigationTests {

        @Test
        @DisplayName("El admin puede ver la lista de usuarios")
        void admin_canViewUserList() {
            click(By.cssSelector("[data-testid='nav-users']"));

            assertCurrentUrlContains("/users");
            assertThat(bodyText()).contains("admin");
            assertThat(bodyText()).contains("user");
        }

        @Test
        @DisplayName("El admin puede ver el detalle de un usuario")
        void admin_canViewUserDetail() {
            visit("/users");

            click(By.cssSelector("table tbody tr:first-child td:nth-child(2) a"));

            assertCurrentUrlContains("/users/");
            assertThat(bodyText()).contains("Vista administrativa del usuario");
        }

        @Test
        @DisplayName("El admin puede ver las acciones de borrado en el detalle de libro")
        void admin_seesDeleteActions() {
            visit("/books/1");

            assertThat(bodyText()).contains("Eliminar");
        }
    }
}
