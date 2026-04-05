package com.certidevs;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Suite que ejecuta TODOS los tests del proyecto con un solo boton de play.
 *
 * <p>En IntelliJ IDEA: abre esta clase y pulsa el boton verde de play (Run)
 * junto al nombre de la clase. Se ejecutaran todos los tests de todos los
 * paquetes bajo {@code com.certidevs}.</p>
 *
 * <h3>JUnit Platform Suite (JUnit 6):</h3>
 * <ul>
 *   <li>{@code @Suite}: marca esta clase como un test suite ejecutable.</li>
 *   <li>{@code @SelectPackages}: selecciona todos los tests dentro del paquete indicado
 *       (incluyendo subpaquetes).</li>
 *   <li>{@code @SuiteDisplayName}: nombre legible que aparece en el informe de tests.</li>
 * </ul>
 *
 * <h3>Alternativas para ejecutar todos los tests:</h3>
 * <ol>
 *   <li>Usar esta Suite: click en el play de esta clase.</li>
 *   <li>Right-click en {@code src/test/java} → Run 'All Tests'.</li>
 *   <li>Terminal: {@code ./mvnw test}</li>
 * </ol>
 */
@Suite
@SelectPackages("com.certidevs")
@SuiteDisplayName("Biblioteca - Suite completa de tests")
public class AllTestsSuite {
    // No se necesita codigo: la anotacion @Suite + @SelectPackages
    // descubre y ejecuta automaticamente todos los tests del paquete.
}
