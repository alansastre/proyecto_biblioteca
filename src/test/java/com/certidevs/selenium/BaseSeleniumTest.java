package com.certidevs.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base comun para tests E2E con Selenium.
 *
 * <p>Con Selenium 4 no hace falta gestiónar manualmente el binario de ChromeDriver:
 * Selenium Manager resuelve el driver automáticamente al instanciar {@link ChromeDriver}.
 * Solo es necesario tener un navegador Chrome/Chromium disponible en la maquina o en CI.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class BaseSeleniumTest {

    @LocalServerPort
    protected int port;

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--disable-gpu",
                "--disable-dev-shm-usage",
                "--no-sandbox",
                "--window-size=1440,1200"
        );

        driver = new ChromeDriver(options);
        driver.manage().window().setSize(new Dimension(1440, 1200));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void visit(String path) {
        driver.get(baseUrl() + path);
        waitForPageReady();
    }

    protected void loginAs(String username, String password) {
        visit("/login");
        waitFor(By.id("username")).sendKeys(username);
        waitFor(By.id("password")).sendKeys(password);
        click(By.cssSelector("[data-testid='login-submit']"));
        wait.until(ExpectedConditions.urlToBe(baseUrl() + "/"));
        waitForPageReady();
    }

    protected void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        String currentUrl = driver.getCurrentUrl();
        element.click();

        try {
            wait.until(ExpectedConditions.stalenessOf(element));
        } catch (TimeoutException ignored) {
            wait.until(webDriver -> !webDriver.getCurrentUrl().equals(currentUrl));
        }

        waitForPageReady();
    }

    protected WebElement waitFor(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void assertCurrentUrlContains(String fragment) {
        assertThat(driver.getCurrentUrl()).contains(fragment);
    }

    protected String bodyText() {
        return waitFor(By.tagName("body")).getText();
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private void waitForPageReady() {
        wait.until(webDriver ->
                "complete".equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
    }
}
