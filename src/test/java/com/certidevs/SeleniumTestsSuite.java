package com.certidevs;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Suite que ejecuta solo los tests E2E con Selenium (navegador real).
 *
 * <p>Estos tests son los mas lentos (~2 minutos) porque levantan Spring Boot
 * con un servidor real y abren Chrome en modo headless para cada test.</p>
 *
 * <p>Requiere tener Chrome/Chromium instalado. Selenium Manager descarga
 * el ChromeDriver automaticamente.</p>
 */
@Suite
@SelectPackages("com.certidevs.selenium")
@SuiteDisplayName("Tests E2E con Selenium")
public class SeleniumTestsSuite {
}
