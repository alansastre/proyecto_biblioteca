package com.certidevs.controller;

import com.certidevs.model.Book;
import com.certidevs.model.Review;
import com.certidevs.model.User;
import com.certidevs.service.BookService;
import com.certidevs.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador para la gestión de reseñas de libros.
 *
 * <p>Cualquier usuario autenticado puede crear reseñas. Solo el autor de una reseña
 * puede editarla o eliminarla (verificado por {@link ReviewService#canModify}).</p>
 *
 * <h3>Autorización a nivel de recurso:</h3>
 * <p>A diferencia de los otros controladores donde la autorización se basa solo en roles
 * (ADMIN vs USER), aqui se necesita verificar la <strong>propiedad del recurso</strong>
 * (si el usuario es el autor de la reseña). Esto se implementa como lógica de negocio
 * en el servicio, ya que Spring Security no puede evaluarlo de forma declarativa.</p>
 *
 * @see ReviewService
 * @see BookService
 */
@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final BookService bookService;

    /**
     * Lista reseñas con filtro opcional por puntuación.
     *
     * @param rating filtro por puntuación (1-5, opcional)
     * @param model  modelo para la vista
     * @return plantilla {@code review/review-list.html}
     */
    @GetMapping
    public String list(@RequestParam(required = false) Integer rating, Model model) {
        List<Review> reviews = rating != null
                ? reviewService.findByRatingForList(rating)
                : reviewService.findAllForList();
        model.addAttribute("reviews", reviews);
        return "review/review-list";
    }

    /**
     * Muestra el formulario de creación de una nueva reseña (usuario autenticado).
     *
     * @param model modelo para la vista
     * @return plantilla {@code review/review-form.html}
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        prepareReviewForm(model, new Review(), "/reviews/create", null, null);
        return "review/review-form";
    }

    /**
     * Procesa la creación de una nueva reseña.
     * Asigna automáticamente el usuario autenticado y la fecha actual.
     *
     * @param review             reseña con datos del formulario
     * @param result             resultado de la validación
     * @param user               usuario autenticado
     * @param bookId             ID del libro reseñado
     * @param model              modelo para la vista (en caso de errores)
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista si éxito, o vuelve al formulario si hay errores
     */
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("review") Review review, BindingResult result,
                        @AuthenticationPrincipal User user,
                        @RequestParam(required = false) Long bookId,
                        Model model, RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/login";

        Book selectedBook = bookId == null ? null : bookService.findById(bookId).orElse(null);
        if (selectedBook == null) {
            result.reject("book", "Debes seleccionar un libro valido");
        }

        if (result.hasErrors()) {
            prepareReviewForm(model, review, "/reviews/create", bookId, null);
            return "review/review-form";
        }
        review.setUser(user);
        review.setBook(selectedBook);
        review.setCreatedAt(LocalDateTime.now());
        reviewService.save(review);
        redirectAttributes.addFlashAttribute("message", "Review creada correctamente");
        return "redirect:/reviews";
    }

    /**
     * Muestra el formulario de edición de una reseña (solo el autor).
     *
     * @param id    ID de la reseña a editar
     * @param model modelo para la vista
     * @param user  usuario autenticado
     * @return plantilla {@code review/review-form.html}, o redirección si no puede editar
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, @AuthenticationPrincipal User user) {
        return reviewService.findByIdForView(id)
                .filter(review -> reviewService.canModify(review, user))
                .map(review -> {
                    prepareReviewForm(model, review, "/reviews/" + id + "/edit",
                            review.getBook() != null ? review.getBook().getId() : null,
                            review.getBook() != null ? review.getBook().getTitle() : null);
                    return "review/review-form";
                })
                .orElse("redirect:/reviews");
    }

    /**
     * Procesa la edición de una reseña (solo el autor).
     * Preserva el usuario, libro y fecha de creación originales.
     *
     * @param id                 ID de la reseña a editar
     * @param review             datos actualizados del formulario
     * @param result             resultado de la validación
     * @param user               usuario autenticado
     * @param model              modelo para la vista (en caso de errores)
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista si éxito, o vuelve al formulario si hay errores
     */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("review") Review review, BindingResult result,
                      @AuthenticationPrincipal User user, Model model, RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/login";

        Review existingReview = reviewService.findByIdForView(id).orElse(null);
        if (existingReview == null || !reviewService.canModify(existingReview, user)) return "redirect:/reviews";

        review.setId(id);
        review.setUser(existingReview.getUser());
        review.setBook(existingReview.getBook());
        review.setCreatedAt(existingReview.getCreatedAt());

        if (result.hasErrors()) {
            prepareReviewForm(model, review, "/reviews/" + id + "/edit",
                    existingReview.getBook() != null ? existingReview.getBook().getId() : null,
                    existingReview.getBook() != null ? existingReview.getBook().getTitle() : null);
            return "review/review-form";
        }
        reviewService.save(review);
        redirectAttributes.addFlashAttribute("message", "Review actualizada correctamente");
        return "redirect:/reviews";
    }

    /**
     * Elimina una reseña (solo el autor).
     *
     * @param id                 ID de la reseña a eliminar
     * @param user               usuario autenticado
     * @param redirectAttributes atributos flash para mensajes
     * @return redirección a la lista de reseñas
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        if (!reviewService.canModify(id, user)) return "redirect:/reviews";
        reviewService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Review eliminada");
        return "redirect:/reviews";
    }

    private void prepareReviewForm(Model model, Review review, String formAction, Long selectedBookId, String bookTitle) {
        model.addAttribute("review", review);
        model.addAttribute("books", bookService.findAllForList());
        model.addAttribute("selectedBookId", selectedBookId);
        model.addAttribute("bookTitle", bookTitle);
        model.addAttribute("formAction", formAction);
    }
}
