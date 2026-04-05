package com.certidevs.controller;

import com.certidevs.service.PurchaseService;
import com.certidevs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para la gestión de usuarios (solo accesible por ADMIN).
 *
 * <p>Permite a los administradores ver la lista de todos los usuarios registrados
 * y consultar los detalles de cada uno (favoritos, compras).</p>
 *
 * <p>La restricción de acceso {@code hasRole("ADMIN")} está configurada en
 * {@link com.certidevs.config.SecurityConfig} para todas las rutas {@code /users/**}.</p>
 *
 * @see UserService
 * @see PurchaseService
 */
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserService userService;
    private final PurchaseService purchaseService;

    /**
     * Lista todos los usuarios del sistema (vista de administración).
     *
     * @param model modelo para la vista
     * @return plantilla {@code user/user-list.html}
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/user-list";
    }

    /**
     * Muestra los detalles de un usuario específico, incluyendo sus favoritos y compras.
     *
     * @param id    ID del usuario a consultar
     * @param model modelo para la vista
     * @return plantilla {@code user/user-detail.html}, o redirección a la lista si no existe
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        return userService.findById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    model.addAttribute("favorites", userService.getFavorites(user));
                    model.addAttribute("purchases", purchaseService.findByUserIdForView(id));
                    return "user/user-detail";
                })
                .orElse("redirect:/users");
    }
}
