package com.certidevs.unit.model;

import com.certidevs.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NIVEL 1 - Test unitario básico: testing de un enum.
 *
 * <p>Este es el test más sencillo del proyecto. Demuestra los fundamentos de JUnit 6 sin dependencias externas (ni Spring, ni Mockito).</p>
 *
 * <h3>Aspectos destacados:</h3>
 * <ul>
 *   <li>{@code @Test}: marca un método como test ejecutable por JUnit.</li>
 *   <li>{@code @DisplayName}: nombre legible del test que aparece en los informes.</li>
 *   <li>{@code assertEquals(expected, actual)}: compara dos valores.</li>
 *   <li>{@code assertTrue / assertFalse}: verifica condiciones booleanas.</li>
 *   <li>{@code assertNotNull}: verifica que un valor no sea null.</li>
 *   <li>{@code @ParameterizedTest} con {@code @EnumSource}: ejecuta el test una vez por cada valor del enum.</li>
 * </ul>
 *
 * <h3>Estructura de un test (patron AAA):</h3>
 * <ol>
 *   <li><strong>Arrange</strong> (Preparar): crear los datos necesarios.</li>
 *   <li><strong>Act</strong> (Actuar): ejecutar la operación que se quiere probar.</li>
 *   <li><strong>Assert</strong> (Verificar): comprobar que el resultado es el esperado.</li>
 * </ol>
 *
 * @see Role
 */
@DisplayName("Role - Test unitario de enum (Nivel 1: básico)")
class RoleTest {

    // ── @Test + assertEquals ────────────────────────────────────

    @Test
    @DisplayName("El enum Role tiene exactamente 2 valores")
    void role_hasTwoValues() {
        // Arrange: obtenemos todos los valores del enum
        Role[] roles = Role.values();

        // Act + Assert: verificamos que hay exactamente 2
        assertEquals(2, roles.length);
    }

    @Test
    @DisplayName("ROLE_USER existe como valor del enum")
    void roleUser_exists() {
        // Act: obtenemos el valor del enum por su nombre
        Role role = Role.valueOf("ROLE_USER");

        // Assert: no es null y tiene el nombre correcto
        assertNotNull(role);
        assertEquals("ROLE_USER", role.name());
    }

    @Test
    @DisplayName("ROLE_ADMIN existe como valor del enum")
    void roleAdmin_exists() {
        Role role = Role.valueOf("ROLE_ADMIN");

        assertNotNull(role);
        assertEquals("ROLE_ADMIN", role.name());
    }

    // ── assertTrue / assertFalse ────────────────────────────────

    @Test
    @DisplayName("Los nombres de los roles empiezan por ROLE_ (convencion Spring Security)")
    void roles_followSpringSecurityConvention() {
        // Spring Security espera que los roles empiecen por "ROLE_"
        assertTrue(Role.ROLE_USER.name().startsWith("ROLE_"));
        assertTrue(Role.ROLE_ADMIN.name().startsWith("ROLE_"));
    }

    @Test
    @DisplayName("ROLE_USER y ROLE_ADMIN son distintos")
    void roleUser_isNotEqualToRoleAdmin() {
        assertFalse(Role.ROLE_USER == Role.ROLE_ADMIN);
        assertNotEquals(Role.ROLE_USER, Role.ROLE_ADMIN);
    }

    // ── assertThrows: verificar que se lanza una excepcion ──────

    @Test
    @DisplayName("valueOf lanza IllegalArgumentException si el rol no existe")
    void valueOf_throwsForInvalidRole() {
        // assertThrows verifica que el código dentro del lambda lanza la excepcion esperada
        assertThrows(IllegalArgumentException.class, () -> {
            Role.valueOf("ROLE_SUPERADMIN");
        });
    }

    // ── @ParameterizedTest con @EnumSource ──────────────────────

    @ParameterizedTest(name = "El rol {0} tiene nombre no vacío")
    @EnumSource(Role.class)
    @DisplayName("Cada rol del enum tiene un nombre no vacío")
    void eachRole_hasNonEmptyName(Role role) {
        // Este test se ejecuta DOS veces: una para ROLE_USER y otra para ROLE_ADMIN
        assertNotNull(role.name());
        assertFalse(role.name().isEmpty());
    }

    @ParameterizedTest(name = "{0} sigue la convencion ROLE_")
    @EnumSource(Role.class)
    @DisplayName("Todos los roles siguen la convencion ROLE_ de Spring Security")
    void eachRole_startsWithRolePrefix(Role role) {
        assertTrue(role.name().startsWith("ROLE_"),
                () -> "El rol " + role.name() + " no sigue la convencion ROLE_");
    }

    // ── ordinal(): posicion del enum ────────────────────────────

    @Test
    @DisplayName("ROLE_USER es el primer valor del enum (ordinal 0)")
    void roleUser_isFirstValue() {
        assertEquals(0, Role.ROLE_USER.ordinal());
    }

    @Test
    @DisplayName("ROLE_ADMIN es el segúndo valor del enum (ordinal 1)")
    void roleAdmin_isSecondValue() {
        assertEquals(1, Role.ROLE_ADMIN.ordinal());
    }
}
