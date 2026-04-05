package com.certidevs.controller;

import com.certidevs.model.Author;
import com.certidevs.model.Book;
import com.certidevs.model.Category;
import com.certidevs.model.User;
import com.certidevs.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controlador para la gestión de libros: CRUD, favoritos y compras.
 *
 * <p>Es el controlador más completo del proyecto, demostrando:</p>
 * <ul>
 *   <li>CRUD completo con validación de formularios</li>
 *   <li>Filtrado múltiple por diferentes criterios</li>
 *   <li>Acciones de usuario autenticado (favoritos, compras)</li>
 *   <li>Uso de {@code @AuthenticationPrincipal} para obtener el usuario actual</li>
 *   <li>Manejo de relaciones ManyToMany (categorías) en formularios</li>
 * </ul>
 *
 * <h3>Seguridad:</h3>
 * <ul>
 *   <li>Listar y ver detalle: público</li>
 *   <li>Crear, editar, eliminar: solo ADMIN</li>
 *   <li>Favoritos, compras: usuario autenticado</li>
 * </ul>
 *
 * @see BookService
 * @see AuthorService
 * @see CategoryService
 */
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final PurchaseService purchaseService;

    /**
     * Lista libros con filtros opcionales (título, autor, categoría, rango de precio).
     * Los filtros son excluyentes: se aplica el primero que tenga valor.
     *
     * @param title      filtro por título (parcial, case-insensitive)
     * @param authorId   filtro por ID del autor
     * @param categoryId filtro por ID de la categoría
     * @param minPrice   precio minimo del rango
     * @param maxPrice   precio máximo del rango
     * @param model      modelo para la vista
     * @return plantilla {@code book/book-list.html}
     */
    @GetMapping
    public String list(@RequestParam(required = false) String title,
                      @RequestParam(required = false) Long authorId,
                      @RequestParam(required = false) Long categoryId,
                      @RequestParam(required = false) Double minPrice,
                      @RequestParam(required = false) Double maxPrice,
                      Model model) {
        List<Book> books;
        if (title != null && !title.isBlank()) {
            books = bookService.findByTitleContainingForList(title);
        } else if (authorId != null) {
            books = bookService.findByAuthorIdForList(authorId);
        } else if (categoryId != null) {
            books = bookService.findByCategoryIdForList(categoryId);
        } else if (minPrice != null || maxPrice != null) {
            books = bookService.findByPriceBetweenForList(minPrice, maxPrice);
        } else {
            books = bookService.findAllForList();
        }
        model.addAttribute("books", books);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "book/book-list";
    }

    /**
     * Muestra el detalle de un libro con sus reseñas y acciones disponibles.
     *
     * @param id    ID del libro
     * @param model modelo para la vista
     * @param user  usuario autenticado (puede ser null si no está logueado)
     * @return plantilla {@code book/book-detail.html}, o redirección si no existe
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, @AuthenticationPrincipal User user) {
        return bookService.findByIdForDetail(id)
                .map(book -> {
                    model.addAttribute("book", book);
                    model.addAttribute("isFavorite", user != null && userService.hasFavorite(user, book));
                    return "book/book-detail";
                })
                .orElse("redirect:/books");
    }

    /**
     * Muestra el formulario de creación de un nuevo libro (solo ADMIN).
     * Prepara las listas de autores y categorías para los selectores del formulario.
     *
     * @param model modelo para la vista
     * @return plantilla {@code book/book-form.html}
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        Book book = new Book();
        book.setAuthor(new Author());
        prepareBookForm(model, book, List.of(), "/books/create");
        return "book/book-form";
    }

    /**
     * Procesa la creación de un nuevo libro (solo ADMIN).
     *
     * <p>Los IDs de categorías se reciben como parametro separado ({@code categoryIds})
     * porque en el formulario HTML se usa un select múltiple que envia los IDs,
     * no objetos Category completos.</p>
     *
     * @param book               libro con datos del formulario
     * @param result             resultado de la validación
     * @param categoryIds        IDs de categorías seleccionadas
     * @param model              modelo para la vista (en caso de errores)
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista si éxito, o vuelve al formulario si hay errores
     */
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("book") Book book, BindingResult result,
                        @RequestParam(required = false) List<Long> categoryIds,
                        Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            prepareBookForm(model, book, normalizeCategoryIds(categoryIds), "/books/create");
            return "book/book-form";
        }
        book.setCategories(resolveCategories(categoryIds));
        book.setAuthor(resolveAuthor(book.getAuthor()));
        bookService.save(book);
        redirectAttributes.addFlashAttribute("message", "Libro creado correctamente");
        return "redirect:/books";
    }

    /**
     * Muestra el formulario de edición de un libro existente (solo ADMIN).
     *
     * @param id    ID del libro a editar
     * @param model modelo para la vista
     * @return plantilla {@code book/book-form.html}, o redirección si no existe
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        return bookService.findByIdForForm(id)
                .map(book -> {
                    prepareBookForm(model, book, extractCategoryIds(book), "/books/" + id + "/edit");
                    return "book/book-form";
                })
                .orElse("redirect:/books");
    }

    /**
     * Procesa la edición de un libro existente (solo ADMIN).
     *
     * @param id                 ID del libro a editar
     * @param book               datos actualizados del formulario
     * @param result             resultado de la validación
     * @param categoryIds        IDs de categorías seleccionadas
     * @param model              modelo para la vista (en caso de errores)
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al detalle si éxito, o vuelve al formulario si hay errores
     */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("book") Book book, BindingResult result,
                      @RequestParam(required = false) List<Long> categoryIds,
                      Model model, RedirectAttributes redirectAttributes) {
        Book existingBook = bookService.findByIdForForm(id).orElse(null);
        if (existingBook == null) {
            return "redirect:/books";
        }

        book.setId(id);
        if (result.hasErrors()) {
            prepareBookForm(model, book, normalizeCategoryIds(categoryIds), "/books/" + id + "/edit");
            return "book/book-form";
        }

        existingBook.setTitle(book.getTitle());
        existingBook.setPrice(book.getPrice());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setPages(book.getPages());
        existingBook.setLanguage(book.getLanguage());
        existingBook.setSynopsis(book.getSynopsis());
        existingBook.setAuthor(resolveAuthor(book.getAuthor()));
        existingBook.setCategories(resolveCategories(categoryIds));

        bookService.save(existingBook);
        redirectAttributes.addFlashAttribute("message", "Libro actualizado correctamente");
        return "redirect:/books/" + id;
    }

    /**
     * Elimina un libro y sus reseñas/compras en cascada (solo ADMIN).
     *
     * @param id                 ID del libro a eliminar
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de libros
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Libro eliminado");
        return "redirect:/books";
    }

    /**
     * Añade un libro a los favoritos del usuario autenticado.
     *
     * @param id                 ID del libro
     * @param user               usuario autenticado
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al detalle del libro
     */
    @PostMapping("/{id}/favorite")
    public String addFavorite(@PathVariable Long id, @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/login";
        bookService.findById(id).ifPresent(book -> userService.addFavorite(user, book));
        redirectAttributes.addFlashAttribute("message", "Añadido a favoritos");
        return "redirect:/books/" + id;
    }

    /**
     * Quita un libro de los favoritos del usuario autenticado.
     *
     * @param id                 ID del libro
     * @param user               usuario autenticado
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al detalle del libro
     */
    @PostMapping("/{id}/favorite/remove")
    public String removeFavorite(@PathVariable Long id, @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/login";
        bookService.findById(id).ifPresent(book -> userService.removeFavorite(user, book));
        redirectAttributes.addFlashAttribute("message", "Quitado de favoritos");
        return "redirect:/books/" + id;
    }

    /**
     * Registra la compra de un libro por el usuario autenticado.
     *
     * @param id                 ID del libro
     * @param user               usuario autenticado
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección al detalle del libro
     */
    @PostMapping("/{id}/buy")
    public String buy(@PathVariable Long id, @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/login";
        bookService.findById(id).ifPresent(book -> purchaseService.buy(user, book));
        redirectAttributes.addFlashAttribute("message", "Compra registrada");
        return "redirect:/books/" + id;
    }

    private void prepareBookForm(Model model, Book book, List<Long> selectedCategoryIds, String formAction) {
        if (book.getAuthor() == null) {
            book.setAuthor(new Author());
        }
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("selectedCategoryIds", normalizeCategoryIds(selectedCategoryIds));
        model.addAttribute("formAction", formAction);
    }

    private Author resolveAuthor(Author author) {
        if (author == null || author.getId() == null) {
            return null;
        }
        return authorService.findById(author.getId()).orElse(null);
    }

    private List<Category> resolveCategories(List<Long> categoryIds) {
        return new ArrayList<>(normalizeCategoryIds(categoryIds).stream()
                .map(id -> categoryService.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .toList());
    }

    private List<Long> extractCategoryIds(Book book) {
        if (book.getCategories() == null) {
            return List.of();
        }
        return book.getCategories().stream()
                .map(Category::getId)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<Long> normalizeCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null) {
            return List.of();
        }
        return categoryIds.stream()
                .filter(Objects::nonNull)
                .toList();
    }
}
