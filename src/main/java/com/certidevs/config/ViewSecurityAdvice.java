package com.certidevs.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Expone información mínima de seguridad al modelo de Thymeleaf.
 *
 * <p>Así evitamos mostrar acciones que el usuario no puede ejecutar realmente
 * y simplificamos las condiciones de renderizado en las plantillas.</p>
 */
@ControllerAdvice(annotations = Controller.class)
public class ViewSecurityAdvice {

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(Authentication authentication) {
        return !isAnonymous(authentication);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        return !isAnonymous(authentication)
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    @ModelAttribute("currentUsername")
    public String currentUsername(Authentication authentication) {
        return isAnonymous(authentication) ? null : authentication.getName();
    }

    private boolean isAnonymous(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}
