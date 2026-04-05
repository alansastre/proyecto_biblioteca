package com.certidevs.unit.service;

import com.certidevs.model.Review;
import com.certidevs.model.Role;
import com.certidevs.model.User;
import com.certidevs.service.ReviewService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NIVEL 4 - Test de lógica de negocio SIN Mockito.
 *
 * <p>Prueba el método {@link ReviewService#canModify(Review, User)} que verifica
 * si un usuario puede editar/eliminar una reseña. Este método tiene lógica pura
 * (sin acceso a base de datos) porque recibe los objetos ya cargados.</p>
 *
 * <h3>¿Por qué testear sin Mockito?</h3>
 * <p>Cuando un método tiene lógica que se puede probar solo con objetos en memoria,
 * NO necesitamos Mockito. Los tests son más simples, rápidos y fáciles de entender.
 * Mockito se usa solo cuando necesitamos simular dependencias externas (repositorios, APIs).</p>
 *
 * <h3>Aspectos destacados:</h3>
 * <ul>
 *   <li>Instanciar un Service directamente (pasando null al constructor para dependencias no usadas).</li>
 *   <li>Testing de autorización a nivel de negocio.</li>
 *   <li>{@code @MethodSource} con {@link Arguments}: múltiples parámetros complejos.</li>
 *   <li>Tabla de verdad: probar todas las combinaciones de entrada.</li>
 * </ul>
 *
 * @see ReviewService#canModify(Review, User)
 */
@DisplayName("ReviewService.canModify - Logica pura sin Mockito (Nivel 4)")
class ReviewServiceLogicTest {

    /**
     * Instanciamos el ReviewService directamente SIN Mockito.
     *
     * <p>El método canModify(Review, User) no usa el repositorio,
     * así que podemos pasar null como dependencia.
     * Esto demuestra que no siempre hace falta un framework de mocking.</p>
     */
    private final ReviewService reviewService = new ReviewService(null);

    // ── Usuarios de prueba ──
    private final User owner = User.builder().id(1L).username("owner").email("o@t.com")
            .password("p").role(Role.ROLE_USER).build();

    private final User otherUser = User.builder().id(2L).username("other").email("ot@t.com")
            .password("p").role(Role.ROLE_USER).build();

    private final User admin = User.builder().id(3L).username("admin").email("a@t.com")
            .password("p").role(Role.ROLE_ADMIN).build();

    // ═══════════════════════════════════════════════════════════════
    // Casos positivos: el propietario puede modificar
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("El autor de la reseña puede modificarla")
    void canModify_returnsTrue_whenUserIsOwner() {
        Review review = Review.builder().id(1L).comment("Mi reseña").rating(5).user(owner).build();

        boolean result = reviewService.canModify(review, owner);

        assertTrue(result, "El autor de la reseña debe poder modificarla");
    }

    // ═══════════════════════════════════════════════════════════════
    // Casos negativos: usuarios que NO pueden modificar
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Otro usuario diferente NO puede modificar la reseña")
    void canModify_returnsFalse_whenUserIsNotOwner() {
        Review review = Review.builder().id(1L).comment("Reseña de owner").rating(4).user(owner).build();

        boolean result = reviewService.canModify(review, otherUser);

        assertFalse(result, "Solo el autor debe poder modificar su reseña");
    }

    @Test
    @DisplayName("Un admin NO puede modificar reseñas de otros (no es propietario)")
    void canModify_returnsFalse_evenForAdmin() {
        // NOTA: En esta implementación, canModify solo comprueba propiedad, no rol.
        // Un admin no puede editar las reseñas de otros (solo las suyas propias).
        Review review = Review.builder().id(1L).comment("Reseña de owner").rating(4).user(owner).build();

        boolean result = reviewService.canModify(review, admin);

        assertFalse(result, "Ni siquiera un admin puede modificar reseñas de otros");
    }

    @Test
    @DisplayName("Usuario null NO puede modificar")
    void canModify_returnsFalse_whenUserIsNull() {
        Review review = Review.builder().id(1L).comment("Test").rating(3).user(owner).build();

        boolean result = reviewService.canModify(review, null);

        assertFalse(result);
    }

    @Test
    @DisplayName("Review null NO puede ser modificada")
    void canModify_returnsFalse_whenReviewIsNull() {
        boolean result = reviewService.canModify((Review) null, owner);

        assertFalse(result);
    }

    @Test
    @DisplayName("Ambos null devuelve false")
    void canModify_returnsFalse_whenBothNull() {
        boolean result = reviewService.canModify((Review) null, null);

        assertFalse(result);
    }

    @Test
    @DisplayName("Review sin usuario asignado devuelve false")
    void canModify_returnsFalse_whenReviewHasNoUser() {
        Review review = Review.builder().id(1L).comment("Test").rating(3).build(); // user = null

        boolean result = reviewService.canModify(review, owner);

        assertFalse(result);
    }

    @Test
    @DisplayName("Review con usuario sin id devuelve false")
    void canModify_returnsFalse_whenReviewUserHasNoId() {
        User userWithoutId = User.builder().username("noId").email("n@t.com")
                .password("p").role(Role.ROLE_USER).build(); // id = null
        Review review = Review.builder().id(1L).comment("Test").rating(3).user(userWithoutId).build();

        boolean result = reviewService.canModify(review, owner);

        assertFalse(result);
    }

    // ═══════════════════════════════════════════════════════════════
    // @MethodSource con Arguments: tabla de verdad completa
    // ═══════════════════════════════════════════════════════════════

    /**
     * Proveedor de datos para la tabla de verdad.
     * Cada {@link Arguments} contiene: (descripción, review, user, resultado esperado).
     *
     * <p>Este patron es muy util cuando quieres documentar TODOS los casos posibles
     * de una función de autorización de forma exhaustiva.</p>
     */
    static Stream<Arguments> canModifyTruthTable() {
        User owner = User.builder().id(1L).username("owner").email("o@t.com")
                .password("p").role(Role.ROLE_USER).build();
        User other = User.builder().id(2L).username("other").email("ot@t.com")
                .password("p").role(Role.ROLE_USER).build();
        Review reviewByOwner = Review.builder().id(1L).comment("Test").rating(5).user(owner).build();
        Review reviewWithoutUser = Review.builder().id(2L).comment("Test").rating(3).build();

        return Stream.of(
                Arguments.of("Propietario puede modificar", reviewByOwner, owner, true),
                Arguments.of("Otro usuario no puede", reviewByOwner, other, false),
                Arguments.of("Review null", null, owner, false),
                Arguments.of("User null", reviewByOwner, null, false),
                Arguments.of("Ambos null", null, null, false),
                Arguments.of("Review sin user", reviewWithoutUser, owner, false)
        );
    }

    @ParameterizedTest(name = "{0} -> {3}")
    @MethodSource("canModifyTruthTable")
    @DisplayName("Tabla de verdad completa de canModify(Review, User)")
    void canModify_truthTable(String description, Review review, User user, boolean expected) {
        boolean result = reviewService.canModify(review, user);

        assertEquals(expected, result, description);
    }
}
