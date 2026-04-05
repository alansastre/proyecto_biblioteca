package com.certidevs.controller;

import com.certidevs.model.User;
import com.certidevs.service.PurchaseService;
import com.certidevs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para el perfil del usuario autenticado.
 *
 * <p>Accesible solo por usuarios autenticados (configurado en {@link com.certidevs.config.SecurityConfig}).
 * Muestra los libros favoritos y el historial de compras del usuario.</p>
 *
 * <h3>Nota sobre @AuthenticationPrincipal:</h3>
 * <p>Esta anotación de Spring Security inyecta directamente el objeto {@link User}
 * que está autenticado en la sesión actual. Funciona porque nuestra entidad {@link User}
 * implementa {@link org.springframework.security.core.userdetails.UserDetails}.</p>
 *
 * @see UserService
 * @see PurchaseService
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PurchaseService purchaseService;

    /**
     * Muestra el perfil del usuario autenticado con sus favoritos y compras.
     *
     * @param user  usuario autenticado inyectado por Spring Security
     * @param model modelo para la vista
     * @return plantilla {@code user/profile.html}, o redirección a login si no hay usuario
     */
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("favorites", userService.getFavorites(user));
        model.addAttribute("purchases", purchaseService.findByUserIdForView(user.getId()));
        return "user/profile";
    }
}
