package com.certidevs.unit;

import com.certidevs.controller.RegisterForm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NIVEL 4 - Test de Bean Validation (JSR 380) sin Spring.
 *
 * <p>Prueba las anotaciones de validación ({@code @NotBlank}, {@code @Email}, {@code @Size})
 * del DTO {@link RegisterForm} usando el Validator de Jakarta directamente,
 * SIN necesidad de levantar Spring Boot.</p>
 *
 * <h3>Aspectos destacados:</h3>
 * <ul>
 *   <li>Jakarta Bean Validation: validación programatica sin Spring.</li>
 *   <li>{@code Validator}: interfaz para validar objetos manualmente.</li>
 *   <li>{@code ConstraintViolation}: representa un error de validación.</li>
 *   <li>{@code @NullAndEmptySource}: combina @NullSource y @EmptySource para parametrizados.</li>
 *   <li>Testing de DTOs/formularios: verificar que las validaciones protegen correctamente.</li>
 * </ul>
 *
 * <h3>¿Por qué testear validaciones?</h3>
 * <p>Las anotaciones como @NotBlank, @Email, @Size son parte de la lógica de negocio.
 * Un cambio accidental (borrar @NotBlank) permitiría registros con datos inválidos.
 * Estos tests protegen contra regresiones.</p>
 *
 * @see RegisterForm
 */
@DisplayName("RegisterForm - Validación con Jakarta Bean Validation (Nivel 4)")
class RegisterFormValidationTest {

    /**
     * El Validator de Jakarta permite validar anotaciones como @NotBlank, @Email, @Size
     * de forma programatica, sin necesidad de un controlador ni Spring MVC.
     */
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        // Creamos el Validator una sola vez para toda la clase
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ═══════════════════════════════════════════════════════════════
    // Formulario valido (happy path)
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Un formulario con todos los campos validos no tiene errores")
    void validForm_hasNoViolations() {
        RegisterForm form = new RegisterForm("alan", "alan@test.com", "password123", "password123");

        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        assertThat(violations).isEmpty();
    }

    // ═══════════════════════════════════════════════════════════════
    // Validación del username
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Validación del campo username")
    class UsernameValidation {

        @ParameterizedTest(name = "username=\"{0}\" no es valido")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("@NotBlank: username no puede ser null, vacío ni solo espacios")
        void username_notBlank(String username) {
            RegisterForm form = new RegisterForm(username, "test@test.com", "pass1234", "pass1234");

            Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

            assertThat(violations)
                    .isNotEmpty()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("username"));
        }

        @Test
        @DisplayName("@Size(min=2): username de 1 carácter es muy corto")
        void username_tooShort() {
            RegisterForm form = new RegisterForm("a", "test@test.com", "pass1234", "pass1234");

            Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().equals("username"));
        }

        @Test
        @DisplayName("@Size(min=2): username de 2 caracteres es valido")
        void username_minimumLength() {
            RegisterForm form = new RegisterForm("ab", "test@test.com", "pass1234", "pass1234");

            Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

            // No debe haber violaciones de username
            assertThat(violations)
                    .noneMatch(v -> v.getPropertyPath().toString().equals("username"));
        }

        @Test
        @DisplayName("@Size(max=50): username de 51 caracteres es muy largo")
        void username_tooLong() {
            String longUsername = "a".repeat(51);
            RegisterForm form = new RegisterForm(longUsername, "test@test.com", "pass1234", "pass1234");

            Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().equals("username"));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Validación del email
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Validación del campo email")
    class EmailValidation {

        @Test
        @DisplayName("@Email: formato de email valido es aceptado")
        void validEmail_isAccepted() {
            RegisterForm form = new RegisterForm("user", "user@example.com", "pass1234", "pass1234");

            Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

            assertThat(violations)
                    .noneMatch(v -> v.getPropertyPath().toString().equals("email"));
        }

        @ParameterizedTest(name = "email=\"{0}\" no es valido")
        @ValueSource(strings = {"no-arroba", "falta@", "@falta-local"})
        @DisplayName("@Email: formatos de email inválidos son rechazados")
        void invalidEmail_isRejected(String email) {
            RegisterForm form = new RegisterForm("user", email, "pass1234", "pass1234");

            Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        }

        @ParameterizedTest(name = "email=\"{0}\" no es valido (blank)")
        @NullAndEmptySource
        @DisplayName("@NotBlank: email no puede ser null ni vacío")
        void blankEmail_isRejected(String email) {
            RegisterForm form = new RegisterForm("user", email, "pass1234", "pass1234");

            Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Validación del password
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Validación del campo password")
    class PasswordValidation {

        @Test
        @DisplayName("@Size(min=4): password de 4 caracteres es valido")
        void password_minimumLength() {
            RegisterForm form = new RegisterForm("user", "u@t.com", "1234", "1234");

            Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

            assertThat(violations)
                    .noneMatch(v -> v.getPropertyPath().toString().equals("password"));
        }

        @Test
        @DisplayName("@Size(min=4): password de 3 caracteres es muy corto")
        void password_tooShort() {
            RegisterForm form = new RegisterForm("user", "u@t.com", "123", "123");

            Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
        }

        @ParameterizedTest(name = "password=\"{0}\" no es valido")
        @NullAndEmptySource
        @DisplayName("@NotBlank: password no puede ser null ni vacío")
        void blankPassword_isRejected(String password) {
            RegisterForm form = new RegisterForm("user", "u@t.com", password, "confirm");

            Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password"));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // passwordConfirm NO tiene validaciones (se compara en el controlador)
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("passwordConfirm no tiene @NotBlank: puede ser cualquier valor")
    void passwordConfirm_hasNoValidation() {
        // La comparación de contraseñas se hace en AuthController, no con anotaciones
        RegisterForm form = new RegisterForm("user", "u@t.com", "pass1234", "");

        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        // No debe haber violaciones en passwordConfirm
        assertThat(violations)
                .noneMatch(v -> v.getPropertyPath().toString().equals("passwordConfirm"));
    }

    // ═══════════════════════════════════════════════════════════════
    // Multiples errores simultaneos
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Un formulario completamente vacío genera múltiples errores")
    void emptyForm_hasMultipleViolations() {
        RegisterForm form = new RegisterForm(null, null, null, null);

        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        // Debe haber al menos 3 errores: username, email y password
        assertThat(violations).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("Cada violacion tiene un mensaje descriptivo")
    void violations_haveMessages() {
        RegisterForm form = new RegisterForm(null, null, null, null);

        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        violations.forEach(v -> {
            assertThat(v.getMessage()).isNotBlank();
            // System.out.println(v.getPropertyPath() + ": " + v.getMessage());
        });
    }
}
