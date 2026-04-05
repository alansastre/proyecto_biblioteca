package com.certidevs.controller;

import com.certidevs.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la autenticación: login y registro de usuarios.
 *
 * <p>Gestiona las páginas de login y registro. La autenticación real (verificación
 * de credenciales) la realiza Spring Security automáticamente; este controlador
 * solo proporciona las vistas y el registro de nuevos usuarios.</p>
 *
 * <h3>Flujo de login:</h3>
 * <ol>
 *   <li>GET /login → muestra el formulario de login</li>
 *   <li>POST /login → Spring Security intercepta y autentica (no llega a este controlador)</li>
 *   <li>Si las credenciales son correctas, redirige a / (configurado en SecurityConfig)</li>
 *   <li>Si son incorrectas, redirige a /login?error</li>
 * </ol>
 *
 * <h3>Flujo de registro:</h3>
 * <ol>
 *   <li>GET /register → muestra el formulario con {@link RegisterForm}</li>
 *   <li>POST /register → valida el formulario, comprueba contraseñas, registra usuario</li>
 *   <li>Si hay errores de validación, vuelve al formulario con los errores</li>
 *   <li>Si el registro es exitoso, redirige a /login con mensaje de éxito</li>
 * </ol>
 *
 * <h3>Notas de implementación:</h3>
 * <ul>
 *   <li>{@code @Valid}: activa la validación de Bean Validation en el formulario.</li>
 *   <li>{@code BindingResult}: contiene los errores de validación (debe ir justo después del objeto validado).</li>
 *   <li>{@code RedirectAttributes}: permite enviar mensajes flash entre redirecciones.</li>
 * </ul>
 *
 * @see RegisterForm
 * @see com.certidevs.config.SecurityConfig
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Muestra la página de login.
     * Los parámetros {@code ?error} y {@code ?logout} se manejan en la plantilla Thymeleaf.
     *
     * @return nombre de la plantilla {@code auth/login.html}
     */
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    /**
     * Muestra el formulario de registro con un {@link RegisterForm} vacío.
     *
     * @param model modelo donde se añade el formulario
     * @return nombre de la plantilla {@code auth/register.html}
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegisterForm());
        return "auth/register";
    }

    /**
     * Procesa el formulario de registro.
     *
     * <p>Valida los campos, comprueba que las contraseñas coinciden
     * y delega en {@link UserService#register} para crear el usuario.</p>
     *
     * @param form               formulario con los datos del nuevo usuario
     * @param result             resultado de la validación (errores si los hay)
     * @param redirectAttributes atributos flash para mensajes entre redirecciones
     * @return redirección a /login si éxito, o vuelve al formulario si hay errores
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegisterForm form, BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "error.user", "Las contraseñas no coinciden");
            return "auth/register";
        }
        try {
            userService.register(form.getUsername(), form.getEmail(), form.getPassword());
            redirectAttributes.addFlashAttribute("message", "Registro correcto. Inicia sesión.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue("username", "error.user", e.getMessage());
            return "auth/register";
        }
    }
}
