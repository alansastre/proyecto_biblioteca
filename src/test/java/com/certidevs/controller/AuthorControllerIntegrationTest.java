package com.certidevs.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integración para {@link AuthorController}.
 *
 * <p>Verifica las operaciones CRUD de autores con diferentes niveles de seguridad:
 * acceso público (listado/detalle), autenticado y ADMIN (crear/editar/eliminar).</p>
 *
 * <h3>Enfoque de testing:</h3>
 * <ul>
 *   <li>MockMvc para simular peticiones HTTP POST con parámetros de formulario.</li>
 *   <li>CSRF token: Spring Security requiere un token CSRF en peticiones POST.</li>
 *   <li>Flash attributes: verificar mensajes después de redirecciones.</li>
 *   <li>Content matchers: verificar contenido HTML renderizado.</li>
 * </ul>
 *
 * @see AuthorController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AuthorController - Tests de integración CRUD")
class AuthorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ═══════════════════════════════════════════════════════════════
    // GET /authors - Listado público
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /authors - Listado")
    class ListTests {

        @Test
        @DisplayName("Listado es público: devuelve 200 sin autenticación")
        void list_isPublic() throws Exception {
            mockMvc.perform(get("/authors"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("author/author-list"))
                    .andExpect(model().attributeExists("authors"));
        }

        @Test
        @DisplayName("Filtro por nombre funciona correctamente")
        void list_withNameFilter() throws Exception {
            mockMvc.perform(get("/authors").param("name", "Garcia"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("authors"))
                    .andExpect(content().string(containsString("Garcia")));
        }

        @Test
        @DisplayName("Filtro por nacionalidad funciona correctamente")
        void list_withNationalityFilter() throws Exception {
            mockMvc.perform(get("/authors").param("nationality", "Argentina"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("authors"));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // GET /authors/{id} - Detalle público
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /authors/{id} - Detalle")
    class DetailTests {

        @Test
        @DisplayName("Detalle de autor existente: devuelve 200")
        void detail_existingAuthor_returns200() throws Exception {
            mockMvc.perform(get("/authors/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("author/author-detail"))
                    .andExpect(model().attributeExists("author"));
        }

        @Test
        @DisplayName("Detalle de autor inexistente: redirige a /authors")
        void detail_nonExistingAuthor_redirects() throws Exception {
            mockMvc.perform(get("/authors/999"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/authors"));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // GET/POST /authors/create - Solo ADMIN
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Crear autor (solo ADMIN)")
    class CreateTests {

        @Test
        @DisplayName("GET /authors/create: sin auth redirige a login")
        void createForm_withoutAuth_redirects() throws Exception {
            mockMvc.perform(get("/authors/create"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @DisplayName("GET /authors/create: ROLE_USER recibe 403")
        void createForm_asUser_isForbidden() throws Exception {
            mockMvc.perform(get("/authors/create").with(user("user").roles("USER")))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /authors/create: ADMIN ve el formulario")
        void createForm_asAdmin_returns200() throws Exception {
            mockMvc.perform(get("/authors/create").with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(view().name("author/author-form"))
                    .andExpect(model().attributeExists("author", "formAction"));
        }

        @Test
        @DisplayName("POST /authors/create: ADMIN crea un autor y redirige")
        void create_asAdmin_createsAndRedirects() throws Exception {
            mockMvc.perform(post("/authors/create")
                            .with(user("admin").roles("ADMIN"))
                            .with(csrf())
                            .param("name", "Nuevo Autor de Test")
                            .param("nationality", "Española")
                            .param("bio", "Biografia del nuevo autor"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/authors"))
                    .andExpect(flash().attributeExists("message"));
        }

        @Test
        @DisplayName("POST /authors/create: nombre vacío muestra errores de validación")
        void create_withBlankName_showsErrors() throws Exception {
            mockMvc.perform(post("/authors/create")
                            .with(user("admin").roles("ADMIN"))
                            .with(csrf())
                            .param("name", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("author/author-form"))
                    .andExpect(model().hasErrors());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // GET/POST /authors/{id}/edit - Solo ADMIN
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Editar autor (solo ADMIN)")
    class EditTests {

        @Test
        @DisplayName("GET /authors/1/edit: ADMIN ve el formulario de edición")
        void editForm_asAdmin_returns200() throws Exception {
            mockMvc.perform(get("/authors/1/edit").with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(view().name("author/author-form"))
                    .andExpect(model().attributeExists("author", "formAction"));
        }

        @Test
        @DisplayName("POST /authors/1/edit: ADMIN actualiza y redirige al detalle")
        void edit_asAdmin_updatesAndRedirects() throws Exception {
            mockMvc.perform(post("/authors/1/edit")
                            .with(user("admin").roles("ADMIN"))
                            .with(csrf())
                            .param("name", "Garcia Marquez Actualizado")
                            .param("nationality", "Colombiana"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/authors/1"))
                    .andExpect(flash().attributeExists("message"));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // POST /authors/{id}/delete - Solo ADMIN
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Eliminar autor (solo ADMIN)")
    class DeleteTests {

        @Test
        @DisplayName("POST /authors/{id}/delete: ADMIN elimina y redirige")
        void delete_asAdmin_deletesAndRedirects() throws Exception {
            mockMvc.perform(post("/authors/4/delete")
                            .with(user("admin").roles("ADMIN"))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/authors"))
                    .andExpect(flash().attributeExists("message"));
        }

        @Test
        @DisplayName("POST /authors/{id}/delete: ROLE_USER recibe 403")
        void delete_asUser_isForbidden() throws Exception {
            mockMvc.perform(post("/authors/1/delete")
                            .with(user("user").roles("USER"))
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }
}
