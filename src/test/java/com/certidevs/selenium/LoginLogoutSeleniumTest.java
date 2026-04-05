package com.certidevs.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test E2E con Selenium: flujos de autenticación (login y logout).
 *
 * <p>Verifica que el login funciona con credenciales correctas e incorrectas,
 * que el logout cierra la sesión, y que la navegación cambia según el estado
 * de autenticación del usuario.</p>
 *
 * <h3>Patrones Selenium utilizados:</h3>
 * <ul>
 *   <li>Rellenar formularios con {@code sendKeys()}.</li>
 *   <li>Enviar formularios con {@code click()} en el botón submit.</li>
 *   <li>Verificar la URL actual para confirmar redirecciones.</li>
 *   <li>Verificar contenido visible en la página con {@code bodyText()}.</li>
 * </ul>
 *
 * @see BaseSeleniumTest
 */
@DisplayName("Selenium - Flujos de login y logout")
class LoginLogoutSeleniumTest extends BaseSeleniumTest {

    @Nested
    @DisplayName("Login con credenciales correctas")
    class SuccessfulLoginTests {

        @Test
        @DisplayName("El admin puede iniciar sesión y ve su username en la navegación")
        void adminLogin_showsUsernameInNav() {
            loginAs("admin", "admin");

            assertThat(bodyText()).contains("admin");
        }

        @Test
        @DisplayName("El usuario puede iniciar sesión")
        void userLogin_succeeds() {
            loginAs("user", "user");

            assertCurrentUrlContains("/");
            assertThat(bodyText()).contains("user");
        }

        @Test
        @DisplayName("Tras el login, el admin ve el enlace de gestión de usuarios")
        void adminLogin_showsUserManagementLink() {
            loginAs("admin", "admin");

            assertThat(waitFor(By.cssSelector("[data-testid='nav-users']")).isDisplayed()).isTrue();
        }
    }

    @Nested
    @DisplayName("Login con credenciales incorrectas")
    class FailedLoginTests {

        @Test
        @DisplayName("Contraseña incorrecta mantiene al usuario en la página de login")
        void wrongPassword_staysOnLoginPage() {
            visit("/login");
            waitFor(By.id("username")).sendKeys("admin");
            waitFor(By.id("password")).sendKeys("wrong_password");
            waitFor(By.cssSelector("[data-testid='login-submit']")).click();

            // Spring Security redirige a /login?error
            assertCurrentUrlContains("/login");
        }

        @Test
        @DisplayName("Usuario inexistente mantiene al usuario en la página de login")
        void nonExistentUser_staysOnLoginPage() {
            visit("/login");
            waitFor(By.id("username")).sendKeys("inexistente");
            waitFor(By.id("password")).sendKeys("cualquiera");
            waitFor(By.cssSelector("[data-testid='login-submit']")).click();

            assertCurrentUrlContains("/login");
        }
    }

    @Nested
    @DisplayName("Logout")
    class LogoutTests {

        @Test
        @DisplayName("El usuario puede cerrar sesión desde la navegación")
        void logout_redirectsToLogin() {
            loginAs("user", "user");

            // El logout es un form POST en el navbar. Usamos JavaScript para hacer click
            // porque el boton puede estar oculto en el collapse del navbar en headless.
            org.openqa.selenium.WebElement logoutBtn = waitFor(By.cssSelector("[data-testid='logout-btn']"));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click()", logoutBtn);

            // Esperar a que se procese el logout (la pagina se recarga)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));

            // SecurityConfig redirige a "/" tras logout. Verificamos que ya no vemos el boton de logout
            // (lo que confirma que la sesion se cerro)
            assertThat(driver.findElements(By.cssSelector("[data-testid='logout-btn']"))).isEmpty();
            // Y si vemos el enlace de login, la sesion se cerro
            assertThat(waitFor(By.cssSelector("[data-testid='nav-login']")).isDisplayed()).isTrue();
        }
    }

    @Nested
    @DisplayName("Acceso protegido sin login")
    class ProtectedAccessTests {

        @Test
        @DisplayName("Acceder a /user/profile sin login redirige a login")
        void profile_withoutLogin_redirectsToLogin() {
            visit("/user/profile");

            assertCurrentUrlContains("/login");
        }

        @Test
        @DisplayName("Acceder a /reviews/create sin login redirige a login")
        void reviewCreate_withoutLogin_redirectsToLogin() {
            visit("/reviews/create");

            assertCurrentUrlContains("/login");
        }
    }
}
