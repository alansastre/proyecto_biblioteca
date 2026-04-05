package com.certidevs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot.
 *
 * <p>{@code @SpringBootApplication} es una anotación compuesta que equivale a:</p>
 * <ul>
 *   <li>{@code @Configuration}: permite definir beans con métodos {@code @Bean}.</li>
 *   <li>{@code @EnableAutoConfiguration}: activa la autoconfiguración de Spring Boot
 *       (detecta dependencias en el classpath y configura beans automáticamente).</li>
 *   <li>{@code @ComponentScan}: escanea este paquete y subpaquetes buscando componentes
 *       ({@code @Controller}, {@code @Service}, {@code @Repository}, {@code @Component}).</li>
 * </ul>
 *
 * <h3>Orden de arranque:</h3>
 * <ol>
 *   <li>{@code SpringApplication.run()} arranca el contexto de Spring.</li>
 *   <li>Se cargan todas las configuraciones y se crean los beans.</li>
 *   <li>Se ejecutan los {@code CommandLineRunner} (como {@link com.certidevs.config.DataInitializer}).</li>
 *   <li>Se inicia el servidor web embebido (Tomcat) en el puerto 8080.</li>
 * </ol>
 *
 * @see com.certidevs.config.SecurityConfig
 * @see com.certidevs.config.DataInitializer
 */
@SpringBootApplication
public class BibliotecaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotecaApplication.class, args);
	}

}
