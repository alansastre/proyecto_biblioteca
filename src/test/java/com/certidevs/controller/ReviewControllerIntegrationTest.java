package com.certidevs.controller;

import com.certidevs.config.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integración para {@link ReviewController}.
 *
 * <p>Verifica el flujo de creación, edición y eliminación de reseñas,
 * con especial atencion a la autorización a nivel de recurso (solo el autor
 * de la reseña puede editarla/eliminarla).</p>
 *
 * <h3>Diferencia con otros controladores:</h3>
 * <p>Mientras que Author/Category usan autorización por ROL (solo ADMIN),
 * las reseñas usan autorización por PROPIEDAD (solo el creador).</p>
 *
 * @see ReviewController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ReviewController - Tests de integración con autorización por recurso")
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Nested
    @DisplayName("GET /reviews - Listado público")
    class ListTests {

        @Test
        @DisplayName("Listado de reseñas es público")
        void list_isPublic() throws Exception {
            mockMvc.perform(get("/reviews"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("review/review-list"))
                    .andExpect(model().attributeExists("reviews"));
        }

        @Test
        @DisplayName("Filtro por rating funciona")
        void list_withRatingFilter() throws Exception {
            mockMvc.perform(get("/reviews").param("rating", "5"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("reviews"));
        }
    }

    @Nested
    @DisplayName("Crear reseña (usuario autenticado)")
    class CreateTests {

        @Test
        @DisplayName("GET /reviews/create: sin auth redirige a login")
        void createForm_withoutAuth_redirects() throws Exception {
            mockMvc.perform(get("/reviews/create"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @DisplayName("GET /reviews/create: usuario autenticado ve el formulario")
        void createForm_asUser_returns200() throws Exception {
            mockMvc.perform(get("/reviews/create")
                            .with(user(customUserDetailsService.loadUserByUsername("user"))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("review/review-form"))
                    .andExpect(model().attributeExists("review", "books", "formAction"));
        }

        @Test
        @DisplayName("POST /reviews/create: crea review y redirige")
        void create_asUser_createsAndRedirects() throws Exception {
            mockMvc.perform(post("/reviews/create")
                            .with(user(customUserDetailsService.loadUserByUsername("user")))
                            .with(csrf())
                            .param("comment", "Review de integración test")
                            .param("rating", "4")
                            .param("bookId", "5"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/reviews"))
                    .andExpect(flash().attributeExists("message"));
        }

        @Test
        @DisplayName("POST /reviews/create: sin libro seleccionado muestra error")
        void create_withoutBook_showsError() throws Exception {
            mockMvc.perform(post("/reviews/create")
                            .with(user(customUserDetailsService.loadUserByUsername("user")))
                            .with(csrf())
                            .param("comment", "Test")
                            .param("rating", "4"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("review/review-form"));
        }

        @Test
        @DisplayName("POST /reviews/create: comentario vacío muestra error de validación")
        void create_withBlankComment_showsErrors() throws Exception {
            mockMvc.perform(post("/reviews/create")
                            .with(user(customUserDetailsService.loadUserByUsername("user")))
                            .with(csrf())
                            .param("comment", "")
                            .param("rating", "4")
                            .param("bookId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("review/review-form"))
                    .andExpect(model().hasErrors());
        }
    }

    @Nested
    @DisplayName("Editar reseña (solo el autor)")
    class EditTests {

        @Test
        @DisplayName("GET /reviews/1/edit: el autor de la reseña ve el formulario")
        void editForm_asOwner_returns200() throws Exception {
            // La review con id=1 fue creada por admin en DataInitializer
            mockMvc.perform(get("/reviews/1/edit")
                            .with(user(customUserDetailsService.loadUserByUsername("admin"))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("review/review-form"));
        }

        @Test
        @DisplayName("GET /reviews/1/edit: otro usuario es redirigido")
        void editForm_asOtherUser_redirects() throws Exception {
            // La review 1 es del admin, el user no deberia poder editarla
            mockMvc.perform(get("/reviews/1/edit")
                            .with(user(customUserDetailsService.loadUserByUsername("user"))))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/reviews"));
        }
    }

    @Nested
    @DisplayName("Eliminar reseña (solo el autor)")
    class DeleteTests {

        @Test
        @DisplayName("POST /reviews/{id}/delete: otro usuario no puede eliminar")
        void delete_asOtherUser_redirectsWithoutDeleting() throws Exception {
            // La review 1 es del admin, el user no puede eliminarla
            mockMvc.perform(post("/reviews/1/delete")
                            .with(user(customUserDetailsService.loadUserByUsername("user")))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/reviews"));
        }
    }
}
