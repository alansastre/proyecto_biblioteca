package com.certidevs.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para el formulario de registro de usuarios.
 *
 * <p>Se usa un DTO separado en lugar de la entidad {@link com.certidevs.model.User} para:
 * <ul>
 *   <li>Incluir el campo {@code passwordConfirm} que no existe en la entidad.</li>
 *   <li>Desacoplar la validación del formulario de la validación de la entidad JPA.</li>
 *   <li>Evitar exponer campos internos de la entidad (como {@code role}) al formulario.</li>
 * </ul>
 *
 * <h3>Validaciones (Bean Validation / JSR 380):</h3>
 * <ul>
 *   <li>{@code @NotBlank}: el campo no puede ser null, vacío, ni solo espacios.</li>
 *   <li>{@code @Size}: limita la longitud del texto.</li>
 *   <li>{@code @Email}: valida que sea un formato de email correcto.</li>
 * </ul>
 *
 * <h3>Nota sobre @Data de Lombok:</h3>
 * <p>{@code @Data} genera automáticamente getters, setters, {@code toString},
 * {@code equals} y {@code hashCode}. Es seguro en DTOs (a diferencia de entidades JPA).</p>
 *
 * @see AuthController#register
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterForm {

    /** Nombre de usuario deseado (2-50 caracteres). */
    @NotBlank(message = "Usuario obligatorio")
    @Size(min = 2, max = 50)
    private String username = "";

    /** Email del usuario. Debe tener formato valido. */
    @NotBlank(message = "Email obligatorio")
    @Email
    private String email = "";

    /** Contraseña (minimo 4 caracteres). */
    @NotBlank(message = "Contraseña obligatoria")
    @Size(min = 4, message = "Minimo 4 caracteres")
    private String password = "";

    /** Confirmacion de contraseña (se compara en el controlador). */
    private String passwordConfirm = "";
}
