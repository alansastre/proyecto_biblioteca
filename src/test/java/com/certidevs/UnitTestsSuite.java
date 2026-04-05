package com.certidevs;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Suite que ejecuta solo los tests UNITARIOS (sin Mockito, sin Spring, sin BD).
 *
 * <p>Estos son los tests mas rapidos del proyecto (~0.5 segundos)
 * y los primeros que se explican en el curso.</p>
 *
 * <p>En IntelliJ IDEA: pulsa el play junto al nombre de esta clase.</p>
 */
@Suite
@SelectPackages("com.certidevs.unit")
@SuiteDisplayName("Tests unitarios JUnit 6 (sin Mockito)")
public class UnitTestsSuite {
}
