package com.certidevs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa una categoría de libros (ej: Novela, Cuento, Ensayo).
 *
 * <h3>Relacion ManyToMany con {@link Book}:</h3>
 * <p>Esta es la parte <strong>inversa</strong> (mapped side) de la relacion ManyToMany.
 * El lado propietario está en {@link Book#getCategories()}, que define el {@code @JoinTable}.
 * Esto significa que para asignar categorías a un libro, se debe modificar la lista
 * {@code book.getCategories()}, no {@code category.getBooks()}.</p>
 *
 * @see Book
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"books"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre único de la categoría. Obligatorio. */
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    /** Descripción de la categoría (máximo 1000 caracteres). */
    @Column(length = 1000)
    private String description;

    /** Color en formato hexadecimal para mostrar en la interfaz (ej: "#3498db"). */
    private String color;

    /**
     * Libros que pertenecen a esta categoría (lado inverso del ManyToMany).
     *
     * <p>{@code mappedBy = "categories"} indica que {@link Book#getCategories()} es el lado propietario.
     * Los cambios en esta lista NO se persisten; hay que modificar {@code book.getCategories()}.</p>
     */
    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    private List<Book> books = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
