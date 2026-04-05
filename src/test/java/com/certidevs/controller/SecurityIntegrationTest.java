package com.certidevs.controller;

import com.certidevs.config.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración para verificar las reglas de seguridad de la aplicación.
 *
 * <p>Comprueba que las restricciones configuradas en
 * {@link com.certidevs.config.SecurityConfig} funcionan correctamente:
 * rutas públicas, rutas que requieren autenticación y rutas que requieren rol ADMIN.</p>
 *
 * <h3>Estrategia de testing:</h3>
 * <p>Para cada regla de seguridad se prueban tres escenarios:</p>
 * <ol>
 *   <li>Sin autenticación (anonimo)</li>
 *   <li>Con usuario normal (ROLE_USER)</li>
 *   <li>Con administrador (ROLE_ADMIN)</li>
 * </ol>
 *
 * @see com.certidevs.config.SecurityConfig
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Seguridad - Tests de reglas de autorización")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // ── Rutas públicas ──────────────────────────────────────

    @Test
    @DisplayName("Página principal es pública")
    void homePage_isPublic() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Listado de autores es público")
    void authorList_isPublic() throws Exception {
        mockMvc.perform(get("/authors")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Listado de categorías es público")
    void categoryList_isPublic() throws Exception {
        mockMvc.perform(get("/categories")).andExpect(status().isOk());
    }

    // ── Rutas que requieren autenticación ────────────────────

    @Test
    @DisplayName("Perfil de usuario redirige a login sin autenticación")
    void userProfile_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Perfil de usuario accesible con autenticación")
    void userProfile_accessibleWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/user/profile").with(user(customUserDetailsService.loadUserByUsername("user"))))
                .andExpect(status().isOk());
    }

    // ── Rutas solo ADMIN ────────────────────────────────────

    @Test
    @DisplayName("Gestión de usuarios: usuario normal recibe 403")
    void userManagement_forbiddenForRegularUser() throws Exception {
        mockMvc.perform(get("/users").with(user("user").roles("USER"))).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Gestión de usuarios: admin puede acceder")
    void userManagement_allowedForAdmin() throws Exception {
        mockMvc.perform(get("/users").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Crear autor: usuario normal recibe 403")
    void createAuthor_forbiddenForUser() throws Exception {
        mockMvc.perform(get("/authors/create").with(user("user").roles("USER"))).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Crear autor: admin puede acceder")
    void createAuthor_allowedForAdmin() throws Exception {
        mockMvc.perform(get("/authors/create").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Crear categoría: usuario normal recibe 403")
    void createCategory_forbiddenForUser() throws Exception {
        mockMvc.perform(get("/categories/create").with(user("user").roles("USER"))).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Crear categoría: admin puede acceder")
    void createCategory_allowedForAdmin() throws Exception {
        mockMvc.perform(get("/categories/create").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
    }
}
