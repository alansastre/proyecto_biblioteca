package com.certidevs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para la aplicación web.
 *
 * <p>Define las reglas de autorización (quien puede acceder a que URLs),
 * la configuración de login/logout y el codificador de contraseñas.</p>
 *
 * <h3>Anotaciones:</h3>
 * <ul>
 *   <li>{@code @Configuration}: marca la clase como fuente de beans de Spring.</li>
 *   <li>{@code @EnableWebSecurity}: activa la configuración de seguridad web de Spring Security.
 *       A partir de Spring Boot 4, esta anotación es necesaria para configuración personalizada.</li>
 * </ul>
 *
 * <h3>Niveles de autorización:</h3>
 * <ol>
 *   <li><strong>Público</strong> ({@code permitAll}): página principal, login, registro, recursos estáticos, listados.</li>
 *   <li><strong>Autenticado</strong> ({@code authenticated}): perfil, favoritos, compras, reseñas.</li>
 *   <li><strong>Solo ADMIN</strong> ({@code hasRole("ADMIN")}): gestión de usuarios, CRUD de autores/libros/categorías.</li>
 * </ol>
 *
 * @see CustomUserDetailsService
 * @see org.springframework.security.web.SecurityFilterChain
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     *
     * <p>El {@link SecurityFilterChain} es el componente central de Spring Security
     * que intercepta cada peticion HTTP y aplica las reglas de seguridad.</p>
     *
     * @param http builder para configurar la seguridad HTTP
     * @return cadena de filtros configurada
     * @throws Exception si hay un error en la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactivar CSRF para la consola H2 (usa iframes y formularios internos)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                // Permitir iframes de la consola H2 (usa frameset)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                // Reglas de autorización: se evaluan en orden, la primera que coincide se aplica
                .authorizeHttpRequests(auth -> auth
                        // 0. Consola H2: accesible sin restricciones (solo desarrollo)
                        .requestMatchers("/h2-console/**").permitAll()
                        // 1. Recursos públicos: accesibles sin autenticación
                        .requestMatchers("/", "/login", "/register", "/css/**", "/webjars/**", "/js/**").permitAll()

                        // 2. Gestión de usuarios: solo administradores
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // 3. CRUD de autores, libros y categorías: solo administradores
                        .requestMatchers("/authors/create", "/authors/*/edit", "/authors/*/delete",
                                "/books/create", "/books/*/edit", "/books/*/delete",
                                "/categories/create", "/categories/*/edit", "/categories/*/delete").hasRole("ADMIN")

                        // 4. Acciones de usuario autenticado: perfil, favoritos, compras
                        .requestMatchers("/user/profile", "/books/*/favorite", "/books/*/favorite/remove", "/books/*/buy").authenticated()

                        // 5. Reseñas: crear/editar/eliminar requiere autenticación
                        .requestMatchers("/reviews/create", "/reviews/*/edit", "/reviews/*/delete").authenticated()

                        // 6. Listados y detalles públicos: autores, libros, categorías, reseñas
                        .requestMatchers("/authors/**", "/books/**", "/categories/**", "/reviews/**").permitAll()

                        // 7. Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                // Configuración del formulario de login
                .formLogin(form -> form
                        .loginPage("/login")              // URL de la página de login personalizada
                        .defaultSuccessUrl("/", true)     // Redirigir a la home tras login exitoso
                        .permitAll()                      // La página de login es pública
                )
                // Configuración del logout
                .logout(logout -> logout
                        .logoutUrl("/logout")             // URL para cerrar sesión (POST)
                        .logoutSuccessUrl("/")            // Redirigir a la home tras logout
                        .invalidateHttpSession(true)      // Invalidar la sesión HTTP
                        .clearAuthentication(true)        // Limpiar la autenticación
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Configura el codificador de contraseñas.
     *
     * <p>{@code DelegatingPasswordEncoder} es el codificador recomendado por Spring Security.
     * Soporta múltiples algoritmos y almacena el algoritmo usado como prefijo
     * (ej: {@code {bcrypt}$2a$10$...}). Por defecto utiliza bcrypt.</p>
     *
     * <p>Ventaja: permite migrar entre algoritmos sin invalidar contraseñas existentes.</p>
     *
     * @return codificador de contraseñas delegado
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
