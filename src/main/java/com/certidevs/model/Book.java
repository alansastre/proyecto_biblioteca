package com.certidevs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa un libro en la biblioteca.
 *
 * <p>Es la entidad central del dominio, relacionada con {@link Author}, {@link Category},
 * {@link Review} y {@link Purchase}.</p>
 *
 * <h3>Relaciones JPA:</h3>
 * <ul>
 *   <li>{@code author} - ManyToOne con {@link Author}: cada libro tiene un único autor (obligatorio).</li>
 *   <li>{@code categories} - ManyToMany con {@link Category} (lado propietario): un libro puede tener varias categorías.</li>
 *   <li>{@code reviews} - OneToMany con {@link Review}: reseñas del libro.</li>
 *   <li>{@code purchases} - OneToMany con {@link Purchase}: compras del libro.</li>
 * </ul>
 *
 * <h3>Notas de implementación:</h3>
 * <ul>
 *   <li>{@code FetchType.LAZY} en {@code @ManyToOne} evita cargar el autor automáticamente en cada consulta
 *       (N+1 problem). Se carga bajo demanda cuando se accede.</li>
 *   <li>{@code @Builder.Default} en {@code available} es necesario para que el Builder de Lombok
 *       use {@code true} como valor por defecto en lugar de {@code null}.</li>
 * </ul>
 *
 * @see Author
 * @see Category
 * @see Review
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"author", "categories", "reviews", "purchases"})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Título del libro. Obligatorio y no puede estar en blanco. */
    @NotBlank
    @Column(nullable = false)
    private String title;

    /** Precio del libro en euros. */
    @PositiveOrZero
    private Double price;

    /** Indica si el libro está disponible para compra. Por defecto {@code true}. */
    @Builder.Default
    private Boolean available = true;

    /** Fecha de publicación del libro. */
    private LocalDate publishDate;

    /** Código ISBN del libro (International Standard Book Number). */
    private String isbn;

    /** Número de páginas. */
    private Integer pages;

    /** Idioma del libro (ej: "Español", "Ingles"). */
    private String language;

    /** Sinopsis o resumen del libro (máximo 5000 caracteres). */
    @Column(length = 5000)
    private String synopsis;

    /**
     * Autor del libro (relacion ManyToOne obligatoria).
     *
     * <p>{@code FetchType.LAZY}: el autor no se carga hasta que se accede a el,
     * optimizando las consultas cuando solo se necesitan datos del libro.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @NotNull
    private Author author;

    /**
     * Categorías del libro (relacion ManyToMany, lado propietario).
     *
     * <p>La tabla intermedia {@code book_categories} se genera automáticamente.
     * Este es el lado propietario porque define {@code @JoinTable}.</p>
     */
    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    /** Reseñas del libro. Se eliminan en cascada si se borra el libro. */
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    /** Compras del libro. Se eliminan en cascada si se borra el libro. */
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Purchase> purchases = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
