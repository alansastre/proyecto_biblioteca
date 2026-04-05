package com.certidevs.util;

import com.certidevs.model.Book;
import com.certidevs.model.Review;

import java.util.*;

/**
 * Calculadora de estadisticas para libros y reseñas.
 *
 * <p>Proporciona metodos puros (sin estado, sin dependencias) para calcular
 * metricas como rating medio, distribucion de puntuaciones, estadisticas de precio, etc.</p>
 *
 * <h3>Uso en la aplicacion:</h3>
 * <ul>
 *   <li>Dashboard (HomeController): estadisticas generales del catalogo.</li>
 *   <li>Detalle de libro (BookController): rating medio y distribucion.</li>
 *   <li>Detalle de autor (AuthorController): estadisticas de sus libros.</li>
 * </ul>
 *
 * <h3>Patron: clase de utilidad sin estado</h3>
 * <p>Todos los metodos son {@code static}. No hay campos de instancia.
 * Esto hace que la clase sea trivial de testear unitariamente sin mocks.</p>
 */
public class BookStatistics {

    private BookStatistics() {
        // Clase de utilidad: no se instancia
    }

    /**
     * Calcula el rating medio de una lista de reseñas.
     *
     * @param reviews lista de reseñas (puede ser null o vacia)
     * @return rating medio redondeado a 1 decimal, o 0.0 si no hay reseñas
     */
    public static double averageRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        double avg = reviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        return Math.round(avg * 10.0) / 10.0;
    }

    /**
     * Calcula la distribucion de ratings (cuantas reseñas hay de cada puntuacion).
     *
     * @param reviews lista de reseñas
     * @return mapa con clave 1..5 y valor = numero de reseñas con esa puntuacion
     */
    public static Map<Integer, Long> ratingDistribution(List<Review> reviews) {
        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }
        if (reviews == null || reviews.isEmpty()) {
            return distribution;
        }
        reviews.stream()
                .filter(r -> r.getRating() != null && r.getRating() >= 1 && r.getRating() <= 5)
                .forEach(r -> distribution.merge(r.getRating(), 1L, Long::sum));
        return distribution;
    }

    /**
     * Calcula el precio medio de una lista de libros.
     *
     * @param books lista de libros (puede ser null o vacia)
     * @return precio medio redondeado a 2 decimales, o 0.0 si no hay libros con precio
     */
    public static double averagePrice(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return 0.0;
        }
        double avg = books.stream()
                .filter(b -> b.getPrice() != null)
                .mapToDouble(Book::getPrice)
                .average()
                .orElse(0.0);
        return Math.round(avg * 100.0) / 100.0;
    }

    /**
     * Encuentra el libro mas caro de una lista.
     *
     * @param books lista de libros
     * @return Optional con el libro mas caro, o vacio si la lista es null/vacia
     */
    public static Optional<Book> mostExpensive(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return Optional.empty();
        }
        return books.stream()
                .filter(b -> b.getPrice() != null)
                .max(Comparator.comparingDouble(Book::getPrice));
    }

    /**
     * Encuentra el libro mas barato de una lista.
     *
     * @param books lista de libros
     * @return Optional con el libro mas barato, o vacio si la lista es null/vacia
     */
    public static Optional<Book> cheapest(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return Optional.empty();
        }
        return books.stream()
                .filter(b -> b.getPrice() != null)
                .min(Comparator.comparingDouble(Book::getPrice));
    }

    /**
     * Cuenta cuantos libros estan disponibles para compra.
     *
     * @param books lista de libros
     * @return numero de libros con available = true
     */
    public static long countAvailable(List<Book> books) {
        if (books == null) {
            return 0;
        }
        return books.stream()
                .filter(b -> Boolean.TRUE.equals(b.getAvailable()))
                .count();
    }

    /**
     * Calcula el rango de precios (min y max) de una lista de libros.
     *
     * @param books lista de libros
     * @return array de 2 elementos [precioMinimo, precioMaximo], o [0.0, 0.0] si no hay datos
     */
    public static double[] priceRange(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return new double[]{0.0, 0.0};
        }
        DoubleSummaryStatistics stats = books.stream()
                .filter(b -> b.getPrice() != null)
                .mapToDouble(Book::getPrice)
                .summaryStatistics();
        if (stats.getCount() == 0) {
            return new double[]{0.0, 0.0};
        }
        return new double[]{stats.getMin(), stats.getMax()};
    }

    /**
     * Calcula el precio total de una lista de libros (suma de precios).
     *
     * @param books lista de libros
     * @return suma de todos los precios
     */
    public static double totalPrice(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return 0.0;
        }
        return books.stream()
                .filter(b -> b.getPrice() != null)
                .mapToDouble(Book::getPrice)
                .sum();
    }
}
