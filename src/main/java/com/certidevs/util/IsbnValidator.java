package com.certidevs.util;

/**
 * Validador de codigos ISBN (International Standard Book Number).
 *
 * <p>Soporta dos formatos:</p>
 * <ul>
 *   <li><strong>ISBN-10</strong>: 10 digitos, el ultimo puede ser 'X' (que vale 10).
 *       Ejemplo: {@code 0-306-40615-2}</li>
 *   <li><strong>ISBN-13</strong>: 13 digitos, empieza por 978 o 979.
 *       Ejemplo: {@code 978-3-16-148410-0}</li>
 * </ul>
 *
 * <h3>Algoritmo de validacion:</h3>
 * <p>Ambos formatos incluyen un <strong>digito de control</strong> (checksum)
 * calculado a partir de los demas digitos. Si el checksum no coincide,
 * el ISBN es invalido (posible error de transcripcion).</p>
 *
 * <h3>Uso en la aplicacion:</h3>
 * <p>Se usa desde {@link com.certidevs.service.BookService} para validar
 * que el ISBN introducido en el formulario de creacion/edicion de libros
 * sea correcto antes de guardarlo en base de datos.</p>
 */
public class IsbnValidator {

    private IsbnValidator() {
        // Clase de utilidad: no se instancia
    }

    /**
     * Valida un ISBN en cualquier formato (10 o 13 digitos).
     * Acepta ISBNs con o sin guiones/espacios.
     *
     * @param isbn el codigo ISBN a validar (puede contener guiones o espacios)
     * @return true si el ISBN es valido
     */
    public static boolean isValid(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return false;
        }
        String cleaned = cleanIsbn(isbn);
        return switch (cleaned.length()) {
            case 10 -> isValidIsbn10(cleaned);
            case 13 -> isValidIsbn13(cleaned);
            default -> false;
        };
    }

    /**
     * Valida especificamente un ISBN-10.
     *
     * <p>Algoritmo: suma ponderada de cada digito multiplicado por su posicion (1..10).
     * El resultado debe ser divisible por 11. El ultimo digito puede ser 'X' (=10).</p>
     *
     * <p>Ejemplo: {@code 0-306-40615-2}<br/>
     * 0*1 + 3*2 + 0*3 + 6*4 + 4*5 + 0*6 + 6*7 + 1*8 + 5*9 + 2*10 = 132<br/>
     * 132 % 11 = 0 → valido</p>
     *
     * @param isbn ISBN-10 (puede contener guiones)
     * @return true si es un ISBN-10 valido
     */
    public static boolean isValidIsbn10(String isbn) {
        if (isbn == null) return false;
        String cleaned = cleanIsbn(isbn);
        if (cleaned.length() != 10) return false;

        // Verificar que los primeros 9 son digitos
        for (int i = 0; i < 9; i++) {
            if (!Character.isDigit(cleaned.charAt(i))) return false;
        }
        // El ultimo puede ser digito o 'X'
        char last = cleaned.charAt(9);
        if (!Character.isDigit(last) && last != 'X' && last != 'x') return false;

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cleaned.charAt(i) - '0') * (i + 1);
        }
        sum += (last == 'X' || last == 'x') ? 10 * 10 : (last - '0') * 10;

        return sum % 11 == 0;
    }

    /**
     * Valida especificamente un ISBN-13.
     *
     * <p>Algoritmo: suma alternada con pesos 1 y 3.
     * El resultado debe ser divisible por 10.</p>
     *
     * <p>Ejemplo: {@code 978-3-16-148410-0}<br/>
     * 9*1 + 7*3 + 8*1 + 3*3 + 1*1 + 6*3 + 1*1 + 4*3 + 8*1 + 4*3 + 1*1 + 0*3 + 0*1 = 100<br/>
     * 100 % 10 = 0 → valido</p>
     *
     * @param isbn ISBN-13 (puede contener guiones)
     * @return true si es un ISBN-13 valido
     */
    public static boolean isValidIsbn13(String isbn) {
        if (isbn == null) return false;
        String cleaned = cleanIsbn(isbn);
        if (cleaned.length() != 13) return false;

        // Verificar que todos son digitos
        for (char c : cleaned.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }

        // Verificar prefijo: debe empezar por 978 o 979
        if (!cleaned.startsWith("978") && !cleaned.startsWith("979")) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 13; i++) {
            int digit = cleaned.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        return sum % 10 == 0;
    }

    /**
     * Formatea un ISBN limpio (solo digitos) al formato estandar con guiones.
     *
     * @param isbn ISBN sin guiones
     * @return ISBN formateado con guiones, o el original si no se puede formatear
     */
    public static String format(String isbn) {
        if (isbn == null) return null;
        String cleaned = cleanIsbn(isbn);
        return switch (cleaned.length()) {
            case 10 -> cleaned.substring(0, 1) + "-" + cleaned.substring(1, 4) + "-"
                    + cleaned.substring(4, 9) + "-" + cleaned.substring(9);
            case 13 -> cleaned.substring(0, 3) + "-" + cleaned.substring(3, 4) + "-"
                    + cleaned.substring(4, 6) + "-" + cleaned.substring(6, 12) + "-" + cleaned.substring(12);
            default -> isbn;
        };
    }

    /**
     * Elimina guiones y espacios de un ISBN.
     */
    static String cleanIsbn(String isbn) {
        return isbn.replaceAll("[\\s-]", "");
    }
}
