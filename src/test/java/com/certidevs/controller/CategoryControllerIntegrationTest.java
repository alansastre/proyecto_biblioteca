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
 * Test de integración para {@link CategoryController}.
 *
 * <p>Cubre las operaciones CRUD completas con verificación de seguridad,
 * validación de formularios y mensajes flash.</p>
 *
 * @see CategoryController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("CategoryController - Tests de integración CRUD")
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("GET /categories - Listado público")
    class ListTests {

        @Test
        @DisplayName("Listado es público: devuelve 200")
        void list_isPublic() throws Exception {
            mockMvc.perform(get("/categories"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("category/category-list"))
                    .andExpect(model().attributeExists("categories"));
        }

        @Test
        @DisplayName("Filtro por nombre funciona")
        void list_withNameFilter() throws Exception {
            mockMvc.perform(get("/categories").param("name", "Novela"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("categories"));
        }
    }

    @Nested
    @DisplayName("GET /categories/{id} - Detalle público")
    class DetailTests {

        @Test
        @DisplayName("Detalle de categoría existente: devuelve 200")
        void detail_existingCategory_returns200() throws Exception {
            mockMvc.perform(get("/categories/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("category/category-detail"))
                    .andExpect(model().attributeExists("category"));
        }

        @Test
        @DisplayName("Detalle de categoría inexistente: redirige")
        void detail_nonExistingCategory_redirects() throws Exception {
            mockMvc.perform(get("/categories/999"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/categories"));
        }
    }

    @Nested
    @DisplayName("Crear categoría (solo ADMIN)")
    class CreateTests {

        @Test
        @DisplayName("GET /categories/create: ADMIN ve el formulario")
        void createForm_asAdmin_returns200() throws Exception {
            mockMvc.perform(get("/categories/create").with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(view().name("category/category-form"))
                    .andExpect(model().attributeExists("category", "formAction"));
        }

        @Test
        @DisplayName("POST /categories/create: ADMIN crea categoría y redirige")
        void create_asAdmin_createsAndRedirects() throws Exception {
            mockMvc.perform(post("/categories/create")
                            .with(user("admin").roles("ADMIN"))
                            .with(csrf())
                            .param("name", "Nueva Categoría Test")
                            .param("description", "Descripción de prueba")
                            .param("color", "#ff5733"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/categories"))
                    .andExpect(flash().attributeExists("message"));
        }

        @Test
        @DisplayName("POST /categories/create: nombre vacío muestra errores")
        void create_withBlankName_showsErrors() throws Exception {
            mockMvc.perform(post("/categories/create")
                            .with(user("admin").roles("ADMIN"))
                            .with(csrf())
                            .param("name", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("category/category-form"))
                    .andExpect(model().hasErrors());
        }

        @Test
        @DisplayName("ROLE_USER no puede crear categorías (403)")
        void create_asUser_isForbidden() throws Exception {
            mockMvc.perform(post("/categories/create")
                            .with(user("user").roles("USER"))
                            .with(csrf())
                            .param("name", "Test"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Editar categoría (solo ADMIN)")
    class EditTests {

        @Test
        @DisplayName("GET /categories/1/edit: ADMIN ve el formulario")
        void editForm_asAdmin_returns200() throws Exception {
            mockMvc.perform(get("/categories/1/edit").with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(view().name("category/category-form"));
        }

        @Test
        @DisplayName("POST /categories/1/edit: ADMIN actualiza y redirige")
        void edit_asAdmin_updatesAndRedirects() throws Exception {
            mockMvc.perform(post("/categories/1/edit")
                            .with(user("admin").roles("ADMIN"))
                            .with(csrf())
                            .param("name", "Novela Actualizada")
                            .param("color", "#000000"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/categories/1"))
                    .andExpect(flash().attributeExists("message"));
        }
    }

    @Nested
    @DisplayName("Eliminar categoría (solo ADMIN)")
    class DeleteTests {

        @Test
        @DisplayName("POST /categories/create + delete: ADMIN crea y elimina una categoría sin libros")
        void delete_asAdmin_deletesAndRedirects() throws Exception {
            // Primero creamos una categoría sin libros asociados
            mockMvc.perform(post("/categories/create")
                            .with(user("admin").roles("ADMIN"))
                            .with(csrf())
                            .param("name", "Categoría Para Borrar")
                            .param("description", "Se borrara en el test"))
                    .andExpect(status().is3xxRedirection());

            // Intentamos eliminar la categoría recién creada (id 5, la siguiente disponible)
            mockMvc.perform(post("/categories/5/delete")
                            .with(user("admin").roles("ADMIN"))
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/categories"))
                    .andExpect(flash().attributeExists("message"));
        }

        @Test
        @DisplayName("ROLE_USER no puede eliminar categorías (403)")
        void delete_asUser_isForbidden() throws Exception {
            mockMvc.perform(post("/categories/1/delete")
                            .with(user("user").roles("USER"))
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }
}
