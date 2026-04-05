package com.certidevs.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Selenium - Pantallas administrativas")
class AdminNavigationSeleniumTest extends BaseSeleniumTest {

    @Test
    @DisplayName("El admin puede abrir la gestión de usuarios y entrar al detalle")
    void adminCanOpenUserManagementAndDetail() {
        loginAs("admin", "admin");
        click(By.cssSelector("[data-testid='nav-users']"));

        assertCurrentUrlContains("/users");
        click(By.cssSelector("table tbody tr:first-child td:nth-child(2) a"));

        assertCurrentUrlContains("/users/");
        assertThat(bodyText()).contains("Vista administrativa del usuario");
    }

    @ParameterizedTest(name = "{1}")
    @CsvSource({
            "/authors/create,Nuevo autor",
            "/books/create,Nuevo libro",
            "/categories/create,Nueva categor"
    })
    @DisplayName("El admin puede abrir los formularios CRUD protegidos")
    void adminCanOpenProtectedCrudForms(String path, String headingFragment) {
        loginAs("admin", "admin");
        visit(path);

        assertThat(waitFor(By.tagName("h1")).getText()).contains(headingFragment);
        assertThat(waitFor(By.tagName("form")).isDisplayed()).isTrue();
    }
}
