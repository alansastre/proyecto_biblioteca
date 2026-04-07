package com.certidevs.controller;

import com.certidevs.service.AuthorService;
import com.certidevs.service.BookService;
import com.certidevs.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para la página principal (dashboard) de la aplicación.
 *
 * <p>Muestra estadísticas generales: número de libros, autores y categorías.
 * Es público (accesible sin autenticación) según la configuración de
 * {@link com.certidevs.config.SecurityConfig}.</p>
 *
 * <h3>Patron MVC:</h3>
 * <ol>
 *   <li>El navegador hace GET /</li>
 *   <li>Spring MVC enruta la peticion a {@link #index(Model)}</li>
 *   <li>El controlador obtiene datos del servicio y los añade al {@link Model}</li>
 *   <li>Thymeleaf renderiza la plantilla {@code index.html} con esos datos</li>
 *   <li>Se devuelve HTML al navegador</li>
 * </ol>
 *
 * <h3>Nota: Servicios vs Repositorios en controladores</h3>
 * <p>Los controladores deben depender de la capa de servicio, no directamente de los
 * repositorios. Esto mantiene la arquitectura en capas (Controller → Service → Repository)
 * y permite que la lógica de negocio quede centralizada en los servicios.</p>
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    /**
     * Muestra la página principal con el recuento de libros, autores y categorías.
     *
     * @param model modelo de Thymeleaf donde se añaden los atributos para la vista
     * @return nombre de la plantilla Thymeleaf a renderizar ({@code index.html})
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("bookCount", bookService.findAll().size());
        model.addAttribute("authorCount", authorService.findAll().size());
        model.addAttribute("categoryCount", categoryService.findAll().size());
        return "index"; // pantalla html
    }
}
