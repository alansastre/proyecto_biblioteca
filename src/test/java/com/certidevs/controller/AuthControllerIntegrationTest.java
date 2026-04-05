package com.certidevs.controller;

import com.certidevs.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integración para {@link AuthController}.
 *
 * <p>Verifica el flujo completo de registro de usuarios incluyendo:
 * validación del formulario, contraseñas que no coinciden,
 * duplicados de username/email, y registro exitoso con redirección.</p>
 *
 * <h3>Enfoque de testing:</h3>
 * <ul>
 *   <li>Testing de flujo completo POST → validación → redirección/error.</li>
 *   <li>Verificación del estado de la BD después de una operación (asserting side effects).</li>
 *   <li>Testing de mensajes flash en redirecciones.</li>
 * </ul>
 *
 * @see AuthController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AuthController - Tests de registro e inicio de sesión")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("GET /login - Página de login")
    class LoginPageTests {

        @Test
        @DisplayName("La página de login es pública y renderiza correctamente")
        void loginPage_isPublic() throws Exception {
            mockMvc.perform(get("/login"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/login"));
        }
    }

    @Nested
    @DisplayName("GET /register - Página de registro")
    class RegisterPageTests {

        @Test
        @DisplayName("La página de registro es pública")
        void registerPage_isPublic() throws Exception {
            mockMvc.perform(get("/register"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/register"))
                    .andExpect(model().attributeExists("user"));
        }
    }

    @Nested
    @DisplayName("POST /register - Flujo de registro")
    class RegisterFlowTests {

        @Test
        @DisplayName("Registro exitoso: redirige a login con mensaje")
        void register_success_redirectsToLogin() throws Exception {
            mockMvc.perform(post("/register")
                            .with(csrf())
                            .param("username", "nuevouser")
                            .param("email", "nuevo@test.com")
                            .param("password", "password123")
                            .param("passwordConfirm", "password123"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attributeExists("message"));

            // Verificar que el usuario se creo en la BD
            assertThat(userRepository.existsByUsername("nuevouser")).isTrue();
        }

        @Test
        @DisplayName("Username vacío: muestra error de validación")
        void register_blankUsername_showsErrors() throws Exception {
            mockMvc.perform(post("/register")
                            .with(csrf())
                            .param("username", "")
                            .param("email", "test@test.com")
                            .param("password", "pass1234")
                            .param("passwordConfirm", "pass1234"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/register"))
                    .andExpect(model().hasErrors());
        }

        @Test
        @DisplayName("Email inválido: muestra error de validación")
        void register_invalidEmail_showsErrors() throws Exception {
            mockMvc.perform(post("/register")
                            .with(csrf())
                            .param("username", "testuser2")
                            .param("email", "no-es-email")
                            .param("password", "pass1234")
                            .param("passwordConfirm", "pass1234"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/register"))
                    .andExpect(model().hasErrors());
        }

        @Test
        @DisplayName("Contraseña muy corta: muestra error de validación")
        void register_shortPassword_showsErrors() throws Exception {
            mockMvc.perform(post("/register")
                            .with(csrf())
                            .param("username", "testuser3")
                            .param("email", "t3@test.com")
                            .param("password", "ab")
                            .param("passwordConfirm", "ab"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/register"))
                    .andExpect(model().hasErrors());
        }

        @Test
        @DisplayName("Contraseñas no coinciden: muestra error")
        void register_passwordMismatch_showsError() throws Exception {
            mockMvc.perform(post("/register")
                            .with(csrf())
                            .param("username", "testuser4")
                            .param("email", "t4@test.com")
                            .param("password", "password1")
                            .param("passwordConfirm", "password2"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/register"))
                    .andExpect(model().attributeHasFieldErrors("user", "passwordConfirm"));
        }

        @Test
        @DisplayName("Username duplicado: muestra error")
        void register_duplicateUsername_showsError() throws Exception {
            // "admin" ya existe por DataInitializer
            mockMvc.perform(post("/register")
                            .with(csrf())
                            .param("username", "admin")
                            .param("email", "newadmin@test.com")
                            .param("password", "password123")
                            .param("passwordConfirm", "password123"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/register"));
        }

        @Test
        @DisplayName("Email duplicado: muestra error")
        void register_duplicateEmail_showsError() throws Exception {
            // "admin@biblioteca.local" ya existe por DataInitializer
            mockMvc.perform(post("/register")
                            .with(csrf())
                            .param("username", "otheruser")
                            .param("email", "admin@biblioteca.local")
                            .param("password", "password123")
                            .param("passwordConfirm", "password123"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("auth/register"));
        }
    }
}
