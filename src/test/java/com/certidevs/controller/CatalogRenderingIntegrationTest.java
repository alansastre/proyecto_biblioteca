package com.certidevs.controller;

import com.certidevs.config.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Tests de renderizado para vistas Thymeleaf que dependen de asociaciones JPA LAZY.
 *
 * <p>Con {@code spring.jpa.open-in-view=false}, estas pantallas fallan enseguida si el controlador
 * no precarga las relaciones que usa la vista. Este test protege precisamente ese caso.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Renderizado MVC - Pantallas con asociaciones LAZY")
class CatalogRenderingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /books/{id} renderiza el detalle completo")
    void bookDetail_rendersWithoutLazyErrors() throws Exception {
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/book-detail"))
                .andExpect(model().attributeExists("book", "isFavorite"));
    }

    @Test
    @DisplayName("GET /books/{id} no muestra acciones de borrado a un usuario normal")
    void bookDetail_hidesDeleteActionForRegularUser() throws Exception {
        mockMvc.perform(get("/books/1").with(user(customUserDetailsService.loadUserByUsername("user"))))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("/books/1/delete"))));
    }

    @Test
    @DisplayName("GET /books/{id} muestra acciones de borrado a un admin")
    void bookDetail_showsDeleteActionForAdmin() throws Exception {
        mockMvc.perform(get("/books/1").with(user(customUserDetailsService.loadUserByUsername("admin"))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/books/1/delete")));
    }

    @Test
    @DisplayName("GET /authors/{id} renderiza la ficha del autor con sus libros")
    void authorDetail_rendersWithoutLazyErrors() throws Exception {
        mockMvc.perform(get("/authors/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("author/author-detail"))
                .andExpect(model().attributeExists("author"));
    }

    @Test
    @DisplayName("GET /categories/{id} renderiza la ficha de la categoría con sus libros")
    void categoryDetail_rendersWithoutLazyErrors() throws Exception {
        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("category/category-detail"))
                .andExpect(model().attributeExists("category"));
    }

    @Test
    @DisplayName("GET /reviews renderiza el listado de reseñas")
    void reviewList_rendersWithoutLazyErrors() throws Exception {
        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(view().name("review/review-list"))
                .andExpect(model().attributeExists("reviews"));
    }

    @Test
    @DisplayName("GET /user/profile renderiza favoritos y compras con un usuario real")
    void userProfile_rendersWithoutLazyErrors() throws Exception {
        mockMvc.perform(get("/user/profile").with(user(customUserDetailsService.loadUserByUsername("user"))))
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attributeExists("user", "favorites", "purchases"));
    }

    @Test
    @DisplayName("GET /users/{id} renderiza el detalle de usuario para admin")
    void adminUserDetail_rendersWithoutLazyErrors() throws Exception {
        mockMvc.perform(get("/users/1").with(user(customUserDetailsService.loadUserByUsername("admin"))))
                .andExpect(status().isOk())
                .andExpect(view().name("user/user-detail"))
                .andExpect(model().attributeExists("user", "favorites", "purchases"));
    }
}
