package com.certidevs.unit.util;

import com.certidevs.util.IsbnValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para {@link IsbnValidator}.
 *
 * <p>Este es un test unitario "de verdad": testea logica de negocio pura
 * sin Spring, sin JPA, sin Mockito, sin base de datos. Exactamente el tipo
 * de test que se escribe en empresas para validar algoritmos y reglas.</p>
 *
 * <h3>Patrones JUnit utilizados:</h3>
 * <ul>
 *   <li>{@code @ParameterizedTest} con {@code @ValueSource}: probar multiples ISBN validos/invalidos.</li>
 *   <li>{@code @NullAndEmptySource}: probar null y "" automaticamente.</li>
 *   <li>{@code @Nested}: agrupar tests por escenario (ISBN-10 vs ISBN-13).</li>
 *   <li>{@code assertThrows}: no se usa aqui porque el validador devuelve boolean, no lanza excepciones.</li>
 *   <li>Patron de testing: happy path + edge cases + valores limite.</li>
 * </ul>
 *
 * @see IsbnValidator
 */
@DisplayName("IsbnValidator - Validacion de codigos ISBN")
class IsbnValidatorTest {

    // ═══════════════════════════════════════════════════════════════
    // Metodo generico isValid()
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("isValid - Validacion generica")
    class IsValidTests {

        @ParameterizedTest(name = "ISBN valido: {0}")
        @ValueSource(strings = {
                "0306406152",         // ISBN-10 sin guiones
                "0-306-40615-2",      // ISBN-10 con guiones
                "9783161484100",      // ISBN-13 sin guiones
                "978-3-16-148410-0",  // ISBN-13 con guiones
                "978 3 16 148410 0"   // ISBN-13 con espacios
        })
        @DisplayName("Acepta ISBNs validos en cualquier formato")
        void isValid_acceptsValidIsbns(String isbn) {
            assertTrue(IsbnValidator.isValid(isbn),
                    () -> "El ISBN '" + isbn + "' deberia ser valido");
        }

        @ParameterizedTest(name = "ISBN invalido: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "123", "abcdefghij", "1234567890123456"})
        @DisplayName("Rechaza null, vacio, y formatos incorrectos")
        void isValid_rejectsInvalidInput(String isbn) {
            assertFalse(IsbnValidator.isValid(isbn));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // ISBN-10
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("ISBN-10 - Validacion con checksum modulo 11")
    class Isbn10Tests {

        @ParameterizedTest(name = "ISBN-10 valido: {0}")
        @ValueSource(strings = {
                "0306406152",     // Checksum correcto
                "0-306-40615-2",  // Con guiones
                "080442957X",     // Ultimo digito X (=10)
                "0-8044-2957-X"   // X con guiones
        })
        @DisplayName("Acepta ISBN-10 validos (incluyendo X como ultimo digito)")
        void isValidIsbn10_acceptsValid(String isbn) {
            assertTrue(IsbnValidator.isValidIsbn10(isbn));
        }

        @Test
        @DisplayName("Rechaza ISBN-10 con checksum incorrecto")
        void isValidIsbn10_rejectsWrongChecksum() {
            // Cambiamos el ultimo digito: 0306406152 → 0306406153
            assertFalse(IsbnValidator.isValidIsbn10("0306406153"));
        }

        @Test
        @DisplayName("Rechaza ISBN-10 con longitud incorrecta")
        void isValidIsbn10_rejectsWrongLength() {
            assertFalse(IsbnValidator.isValidIsbn10("123456789"));   // 9 digitos
            assertFalse(IsbnValidator.isValidIsbn10("12345678901")); // 11 digitos
        }

        @Test
        @DisplayName("Rechaza ISBN-10 con letras (excepto X al final)")
        void isValidIsbn10_rejectsLettersInMiddle() {
            assertFalse(IsbnValidator.isValidIsbn10("03064A6152"));
        }

        @Test
        @DisplayName("Rechaza null")
        void isValidIsbn10_rejectsNull() {
            assertFalse(IsbnValidator.isValidIsbn10(null));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // ISBN-13
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("ISBN-13 - Validacion con checksum modulo 10")
    class Isbn13Tests {

        @ParameterizedTest(name = "ISBN-13 valido: {0}")
        @ValueSource(strings = {
                "9783161484100",      // Sin guiones
                "978-3-16-148410-0",  // Con guiones
                "9780553383577",      // Otro ISBN-13 valido
                "978-0-553-38357-7"   // Con guiones
        })
        @DisplayName("Acepta ISBN-13 validos con prefijo 978")
        void isValidIsbn13_acceptsValid(String isbn) {
            assertTrue(IsbnValidator.isValidIsbn13(isbn));
        }

        @Test
        @DisplayName("Rechaza ISBN-13 con checksum incorrecto")
        void isValidIsbn13_rejectsWrongChecksum() {
            // Cambiamos ultimo digito: 9783161484100 → 9783161484101
            assertFalse(IsbnValidator.isValidIsbn13("9783161484101"));
        }

        @Test
        @DisplayName("Rechaza ISBN-13 sin prefijo 978/979")
        void isValidIsbn13_rejectsWrongPrefix() {
            // Un ISBN-13 debe empezar por 978 o 979
            assertFalse(IsbnValidator.isValidIsbn13("1234567890123"));
        }

        @Test
        @DisplayName("Rechaza ISBN-13 con longitud incorrecta")
        void isValidIsbn13_rejectsWrongLength() {
            assertFalse(IsbnValidator.isValidIsbn13("978316148410"));   // 12 digitos
            assertFalse(IsbnValidator.isValidIsbn13("97831614841001")); // 14 digitos
        }

        @Test
        @DisplayName("Rechaza ISBN-13 con letras")
        void isValidIsbn13_rejectsLetters() {
            assertFalse(IsbnValidator.isValidIsbn13("978316148410A"));
        }

        @Test
        @DisplayName("Rechaza null")
        void isValidIsbn13_rejectsNull() {
            assertFalse(IsbnValidator.isValidIsbn13(null));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Formateo
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("format - Formateo con guiones")
    class FormatTests {

        @Test
        @DisplayName("Formatea ISBN-10 con guiones")
        void format_isbn10() {
            assertEquals("0-306-40615-2", IsbnValidator.format("0306406152"));
        }

        @Test
        @DisplayName("Formatea ISBN-13 con guiones")
        void format_isbn13() {
            assertEquals("978-3-16-148410-0", IsbnValidator.format("9783161484100"));
        }

        @Test
        @DisplayName("Devuelve el original si la longitud no es 10 ni 13")
        void format_returnsOriginalForInvalidLength() {
            assertEquals("12345", IsbnValidator.format("12345"));
        }

        @Test
        @DisplayName("Devuelve null si el input es null")
        void format_returnsNullForNull() {
            assertNull(IsbnValidator.format(null));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Limpieza de guiones y espacios (verificada indirectamente)
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Limpieza de formato (guiones y espacios)")
    class CleaningTests {

        @Test
        @DisplayName("isValid acepta ISBN con guiones (los limpia internamente)")
        void isValid_acceptsHyphens() {
            assertTrue(IsbnValidator.isValid("978-3-16-148410-0"));
        }

        @Test
        @DisplayName("isValid acepta ISBN con espacios (los limpia internamente)")
        void isValid_acceptsSpaces() {
            assertTrue(IsbnValidator.isValid("978 3 16 148410 0"));
        }

        @Test
        @DisplayName("isValid acepta ISBN sin separadores")
        void isValid_acceptsClean() {
            assertTrue(IsbnValidator.isValid("9783161484100"));
        }
    }
}
