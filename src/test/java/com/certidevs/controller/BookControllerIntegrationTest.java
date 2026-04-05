package com.certidevs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integración para {@link BookController} con seguridad.
 *
 * <p>Demuestra cómo testear controladores que tienen restricciones de seguridad
 * usando {@code @WithMockUser} para simular usuarios con diferentes roles.</p>
 *
 * <h3>@WithMockUser:</h3>
 * <p>Simula un usuario autenticado en el contexto de seguridad sin necesidad
 * de hacer login real. Se puede especificar username, password y roles.</p>
 *
 * @see BookController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("BookController - Tests de integración con seguridad")
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /books es público: devuelve 200 sin autenticación")
    void list_isPublic() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/book-list"))
                .andExpect(model().attributeExists("books", "authors", "categories"));
    }

    @Test
    @DisplayName("GET /books/create sin autenticación: redirige a login (302)")
    void createForm_requiresAuth() throws Exception {
        mockMvc.perform(get("/books/create"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("GET /books/create con ROLE_USER: devuelve 403 Forbidden")
    void createForm_forbiddenForUser() throws Exception {
        mockMvc.perform(get("/books/create").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /books/create con ROLE_ADMIN: devuelve 200 OK")
    void createForm_allowedForAdmin() throws Exception {
        mockMvc.perform(get("/books/create").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("book/book-form"))
                .andExpect(model().attributeExists("book", "authors", "categories", "formAction"));
    }

    @Test
    @DisplayName("GET /books con filtro de título: filtra correctamente")
    void list_withTitleFilter() throws Exception {
        mockMvc.perform(get("/books").param("title", "Cien"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("books"));
    }
}
