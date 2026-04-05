package com.certidevs.controller;

import com.certidevs.model.Author;
import com.certidevs.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para la gestión de autores (CRUD completo).
 *
 * <p>Las operaciones de lectura (listar, detalle) son públicas. Las operaciones de escritura
 * (crear, editar, eliminar) estan restringidas a usuarios con rol ADMIN.</p>
 *
 * <h3>Patron PRG (Post-Redirect-Get):</h3>
 * <p>Tras una operación POST exitosa (crear, editar, eliminar), se hace una redirección
 * en lugar de devolver una vista directamente. Esto evita que el usuario reenvie
 * el formulario si pulsa F5 (refresco del navegador).</p>
 *
 * <h3>Mensajes flash:</h3>
 * <p>{@link RedirectAttributes#addFlashAttribute} envia un mensaje que sobrevive
 * a una redirección HTTP (se almacena temporalmente en la sesión). El navbar/layout
 * muestra estos mensajes automáticamente.</p>
 *
 * @see AuthorService
 * @see Author
 */
@Controller
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    /**
     * Lista autores con filtros opcionales por nombre o nacionalidad.
     *
     * @param name        filtro por nombre (parcial, case-insensitive)
     * @param nationality filtro por nacionalidad
     * @param model       modelo para la vista
     * @return plantilla {@code author/author-list.html}
     */
    @GetMapping
    public String list(@RequestParam(required = false) String name,
                      @RequestParam(required = false) String nationality,
                      Model model) {
        List<Author> authors = name != null && !name.isBlank()
                ? authorService.findByNameContaining(name)
                : (nationality != null && !nationality.isBlank()
                ? authorService.findByNationality(nationality)
                : authorService.findAll());
        model.addAttribute("authors", authors);
        return "author/author-list";
    }

    /**
     * Muestra el detalle de un autor y sus libros.
     *
     * @param id    ID del autor
     * @param model modelo para la vista
     * @return plantilla {@code author/author-detail.html}, o redirección si no existe
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        return authorService.findByIdForDetail(id)
                .map(author -> {
                    model.addAttribute("author", author);
                    return "author/author-detail";
                })
                .orElse("redirect:/authors");
    }

    /**
     * Muestra el formulario de creación de un nuevo autor (solo ADMIN).
     *
     * @param model modelo donde se añade un Author vacío para el formulario
     * @return plantilla {@code author/author-form.html}
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("author", new Author());
        model.addAttribute("formAction", "/authors/create");
        return "author/author-form";
    }

    /**
     * Procesa la creación de un nuevo autor (solo ADMIN).
     *
     * @param author             autor con los datos del formulario
     * @param result             resultado de la validación
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de autores si éxito, o vuelve al formulario si hay errores
     */
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("author") Author author, BindingResult result,
                        Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            prepareAuthorForm(model, author, "/authors/create");
            return "author/author-form";
        }
        authorService.save(author);
        redirectAttributes.addFlashAttribute("message", "Autor creado correctamente");
        return "redirect:/authors";
    }

    /**
     * Muestra el formulario de edición de un autor existente (solo ADMIN).
     *
     * @param id    ID del autor a editar
     * @param model modelo para la vista
     * @return plantilla {@code author/author-form.html}, o redirección si no existe
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return authorService.findById(id)
                .map(author -> {
                    model.addAttribute("author", author);
                    model.addAttribute("formAction", "/authors/" + id + "/edit");
                    return "author/author-form";
                })
                .orElse("redirect:/authors");
    }

    /**
     * Procesa la edición de un autor existente (solo ADMIN).
     *
     * @param id                 ID del autor a editar
     * @param author             datos actualizados del formulario
     * @param result             resultado de la validación
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al detalle del autor si éxito, o vuelve al formulario si hay errores
     */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("author") Author author, BindingResult result,
                      Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            prepareAuthorForm(model, author, "/authors/" + id + "/edit");
            return "author/author-form";
        }

        Author existingAuthor = authorService.findById(id).orElse(null);
        if (existingAuthor == null) {
            return "redirect:/authors";
        }

        existingAuthor.setName(author.getName());
        existingAuthor.setBio(author.getBio());
        existingAuthor.setBirthDate(author.getBirthDate());
        existingAuthor.setNationality(author.getNationality());

        authorService.save(existingAuthor);
        redirectAttributes.addFlashAttribute("message", "Autor actualizado correctamente");
        return "redirect:/authors/" + id;
    }

    /**
     * Elimina un autor y todos sus libros en cascada (solo ADMIN).
     *
     * @param id                 ID del autor a eliminar
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de autores
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        authorService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Autor eliminado");
        return "redirect:/authors";
    }

    private void prepareAuthorForm(Model model, Author author, String formAction) {
        model.addAttribute("author", author);
        model.addAttribute("formAction", formAction);
    }
}
