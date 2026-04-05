package com.certidevs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integración para {@link HomeController} usando MockMvc.
 *
 * <p>{@code MockMvc} simula peticiones HTTP sin levantar un servidor real.
 * Es más rápido que un test con servidor completo y permite verificar
 * status codes, vistas renderizadas y contenido del modelo.</p>
 *
 * <h3>Anotaciones:</h3>
 * <ul>
 *   <li>{@code @SpringBootTest}: carga el contexto completo (incluye seguridad, BD, etc.).</li>
 *   <li>{@code @AutoConfigureMockMvc}: configura MockMvc automáticamente.</li>
 * </ul>
 *
 * @see HomeController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("HomeController - Tests de integración con MockMvc")
class HomeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET / devuelve status 200 y renderiza la vista index")
    void index_returnsOkAndIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("bookCount", "authorCount", "categoryCount"));
    }

    @Test
    @DisplayName("GET /login devuelve status 200 (página pública)")
    void login_returnsOk() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("GET /register devuelve status 200 (página pública)")
    void register_returnsOk() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }
}
