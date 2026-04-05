package com.certidevs.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Selenium - Flujo autenticado de usuario")
class AuthenticatedUserSeleniumTest extends BaseSeleniumTest {

    @Test
    @DisplayName("El usuario puede iniciar sesión y abrir su perfil")
    void userCanLoginAndOpenProfile() {
        loginAs("user", "user");
        click(By.cssSelector("[data-testid='nav-profile']"));

        assertCurrentUrlContains("/user/profile");
        assertThat(bodyText()).contains("Mi perfil");
        assertThat(waitFor(By.cssSelector("[data-testid='favorites-list']")).getText()).isNotBlank();
    }

    @Test
    @DisplayName("El usuario puede añadir un libro a favoritos y verlo en su perfil")
    void userCanAddFavoriteAndSeeItInProfile() {
        loginAs("user", "user");
        visit("/books/2");
        click(By.cssSelector("[data-testid='btn-add-favorite']"));

        assertThat(bodyText()).contains("Añadido a favoritos");

        click(By.cssSelector("[data-testid='nav-profile']"));
        assertThat(waitFor(By.cssSelector("[data-testid='favorites-list']")).getText())
                .contains("La casa de los espíritus");
    }

    @Test
    @DisplayName("El usuario puede comprar un libro y verlo en su historial")
    void userCanBuyBookAndSeePurchaseInProfile() {
        loginAs("user", "user");
        visit("/books/4");
        click(By.cssSelector("[data-testid='btn-buy']"));

        assertThat(bodyText()).contains("Compra registrada");

        click(By.cssSelector("[data-testid='nav-profile']"));
        assertThat(waitFor(By.cssSelector("[data-testid='purchases-list']")).getText())
                .contains("Rayuela");
    }

    @Test
    @DisplayName("El usuario puede crear una review desde el formulario")
    void userCanCreateReview() {
        loginAs("user", "user");
        visit("/reviews/create");

        new Select(waitFor(By.id("bookId"))).selectByValue("5");
        waitFor(By.id("rating")).sendKeys("4");
        waitFor(By.id("comment")).sendKeys("Review creada por Selenium");
        click(By.cssSelector("[data-testid='review-form'] button[type='submit']"));

        assertCurrentUrlContains("/reviews");
        assertThat(bodyText()).contains("Review creada correctamente");
        assertThat(waitFor(By.cssSelector("[data-testid='reviews-table']")).getText())
                .contains("Review creada por Selenium");
    }
}
