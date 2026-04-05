package com.certidevs.controller;

import com.certidevs.model.Category;
import com.certidevs.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para la gestión de categorías de libros (CRUD completo).
 *
 * <p>Las operaciones de lectura son públicas. Las de escritura requieren rol ADMIN.</p>
 *
 * @see CategoryService
 * @see Category
 */
@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Lista categorías con filtro opcional por nombre.
     *
     * @param name  filtro por nombre (parcial, case-insensitive)
     * @param model modelo para la vista
     * @return plantilla {@code category/category-list.html}
     */
    @GetMapping
    public String list(@RequestParam(required = false) String name, Model model) {
        List<Category> categories = name != null && !name.isBlank()
                ? categoryService.findByNameContaining(name)
                : categoryService.findAll();
        model.addAttribute("categories", categories);
        return "category/category-list";
    }

    /**
     * Muestra el detalle de una categoría con sus libros.
     *
     * @param id    ID de la categoría
     * @param model modelo para la vista
     * @return plantilla {@code category/category-detail.html}, o redirección si no existe
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        return categoryService.findByIdForDetail(id)
                .map(category -> {
                    model.addAttribute("category", category);
                    return "category/category-detail";
                })
                .orElse("redirect:/categories");
    }

    /**
     * Muestra el formulario de creación de una nueva categoría (solo ADMIN).
     *
     * @param model modelo para la vista
     * @return plantilla {@code category/category-form.html}
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("formAction", "/categories/create");
        return "category/category-form";
    }

    /**
     * Procesa la creación de una nueva categoría (solo ADMIN).
     *
     * @param category           categoría con datos del formulario
     * @param result             resultado de la validación
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista si éxito, o vuelve al formulario si hay errores
     */
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("category") Category category, BindingResult result,
                        Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            prepareCategoryForm(model, category, "/categories/create");
            return "category/category-form";
        }
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("message", "Categoría creada correctamente");
        return "redirect:/categories";
    }

    /**
     * Muestra el formulario de edición de una categoría existente (solo ADMIN).
     *
     * @param id    ID de la categoría a editar
     * @param model modelo para la vista
     * @return plantilla {@code category/category-form.html}, o redirección si no existe
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return categoryService.findById(id)
                .map(category -> {
                    model.addAttribute("category", category);
                    model.addAttribute("formAction", "/categories/" + id + "/edit");
                    return "category/category-form";
                })
                .orElse("redirect:/categories");
    }

    /**
     * Procesa la edición de una categoría existente (solo ADMIN).
     *
     * @param id                 ID de la categoría a editar
     * @param category           datos actualizados del formulario
     * @param result             resultado de la validación
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al detalle si éxito, o vuelve al formulario si hay errores
     */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("category") Category category, BindingResult result,
                      Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            prepareCategoryForm(model, category, "/categories/" + id + "/edit");
            return "category/category-form";
        }

        Category existingCategory = categoryService.findById(id).orElse(null);
        if (existingCategory == null) {
            return "redirect:/categories";
        }

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setColor(category.getColor());

        categoryService.save(existingCategory);
        redirectAttributes.addFlashAttribute("message", "Categoría actualizada correctamente");
        return "redirect:/categories/" + id;
    }

    /**
     * Elimina una categoría (solo ADMIN).
     *
     * @param id                 ID de la categoría a eliminar
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de categorías
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Categoría eliminada");
        return "redirect:/categories";
    }

    private void prepareCategoryForm(Model model, Category category, String formAction) {
        model.addAttribute("category", category);
        model.addAttribute("formAction", formAction);
    }
}
