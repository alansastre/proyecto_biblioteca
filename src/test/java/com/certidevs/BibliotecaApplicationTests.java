package com.certidevs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test de humo (smoke test) que verifica que el contexto de Spring Boot arranca correctamente.
 *
 * <p>Si alguna configuración está mal (dependencia faltante, bean mal definido, error en
 * application.properties, etc.), este test fallara inmediatamente.</p>
 *
 * <h3>Anotaciones:</h3>
 * <ul>
 *   <li>{@code @SpringBootTest}: carga el contexto completo de Spring Boot (todos los beans).</li>
 *   <li>{@code @ActiveProfiles("test")}: usa {@code application-test.properties} en lugar de {@code application.properties}.</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
class BibliotecaApplicationTests {

	@Test
	void contextLoads() {
		// Si el contexto arranca sin excepciones, el test pasa
	}
}
