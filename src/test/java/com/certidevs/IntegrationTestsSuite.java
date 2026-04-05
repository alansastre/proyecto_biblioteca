package com.certidevs;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Suite que ejecuta los tests de INTEGRACION (repositorios + controladores + servicios).
 *
 * <p>Estos tests levantan Spring Boot y/o JPA para verificar que las capas
 * funcionan correctamente juntas. Son mas lentos que los unitarios (~20 segundos).</p>
 *
 * <p>Incluye: @DataJpaTest, MockMvc con @SpringBootTest, y tests con Mockito.</p>
 */
@Suite
@SelectPackages({"com.certidevs.repository", "com.certidevs.controller", "com.certidevs.service"})
@SuiteDisplayName("Tests de integracion (Repository + Controller + Service)")
public class IntegrationTestsSuite {
}
