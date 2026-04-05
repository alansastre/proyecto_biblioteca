package com.certidevs.util;

import com.certidevs.model.Book;

import java.util.List;

/**
 * Calculadora de precios con descuentos y reglas de negocio.
 *
 * <p>Implementa la logica de precios de la biblioteca:</p>
 * <ul>
 *   <li>Descuento por volumen: comprar varios libros a la vez tiene descuento.</li>
 *   <li>Descuento por fidelidad: usuarios con compras previas obtienen mejor precio.</li>
 *   <li>IVA: calcula el precio con y sin impuestos.</li>
 *   <li>Validacion: precios negativos o cantidades invalidas se rechazan.</li>
 * </ul>
 *
 * <h3>Reglas de descuento por volumen:</h3>
 * <ul>
 *   <li>1 libro: sin descuento</li>
 *   <li>2-4 libros: 5% de descuento</li>
 *   <li>5-9 libros: 10% de descuento</li>
 *   <li>10+ libros: 15% de descuento</li>
 * </ul>
 */
public class PriceCalculator {

    /** IVA para libros en España (4% tipo superreducido). */
    public static final double BOOK_TAX_RATE = 0.04;

    /** Descuento de fidelidad para usuarios recurrentes. */
    public static final double LOYALTY_DISCOUNT_RATE = 0.03;

    /** Umbral de compras previas para aplicar descuento de fidelidad. */
    public static final int LOYALTY_THRESHOLD = 5;

    private PriceCalculator() {
    }

    /**
     * Calcula el descuento por volumen segun la cantidad de libros.
     *
     * @param quantity numero de libros
     * @return porcentaje de descuento (0.0, 0.05, 0.10 o 0.15)
     * @throws IllegalArgumentException si quantity es negativa
     */
    public static double volumeDiscount(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa: " + quantity);
        }
        if (quantity >= 10) return 0.15;
        if (quantity >= 5) return 0.10;
        if (quantity >= 2) return 0.05;
        return 0.0;
    }

    /**
     * Calcula el precio total de una lista de libros con descuento por volumen.
     *
     * @param books lista de libros a comprar
     * @return precio total con descuento aplicado
     * @throws IllegalArgumentException si la lista es null
     */
    public static double calculateTotal(List<Book> books) {
        if (books == null) {
            throw new IllegalArgumentException("La lista de libros no puede ser null");
        }
        if (books.isEmpty()) {
            return 0.0;
        }

        double subtotal = books.stream()
                .filter(b -> b.getPrice() != null)
                .mapToDouble(Book::getPrice)
                .sum();

        double discount = volumeDiscount(books.size());
        return round(subtotal * (1 - discount));
    }

    /**
     * Calcula el precio total con descuento de fidelidad si aplica.
     *
     * @param books             lista de libros a comprar
     * @param previousPurchases numero de compras previas del usuario
     * @return precio total con descuentos de volumen y fidelidad
     */
    public static double calculateTotalWithLoyalty(List<Book> books, int previousPurchases) {
        double total = calculateTotal(books);
        if (previousPurchases >= LOYALTY_THRESHOLD) {
            total = total * (1 - LOYALTY_DISCOUNT_RATE);
        }
        return round(total);
    }

    /**
     * Calcula el IVA de un precio.
     *
     * @param price precio base (sin IVA)
     * @return importe del IVA
     * @throws IllegalArgumentException si el precio es negativo
     */
    public static double calculateTax(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo: " + price);
        }
        return round(price * BOOK_TAX_RATE);
    }

    /**
     * Calcula el precio final con IVA incluido.
     *
     * @param price precio base (sin IVA)
     * @return precio con IVA
     */
    public static double priceWithTax(double price) {
        return round(price + calculateTax(price));
    }

    /**
     * Calcula el ahorro total respecto al precio sin descuentos.
     *
     * @param books lista de libros
     * @return cantidad ahorrada gracias al descuento por volumen
     */
    public static double savings(List<Book> books) {
        if (books == null || books.isEmpty()) return 0.0;

        double fullPrice = books.stream()
                .filter(b -> b.getPrice() != null)
                .mapToDouble(Book::getPrice)
                .sum();

        return round(fullPrice - calculateTotal(books));
    }

    /**
     * Redondea a 2 decimales.
     */
    static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
