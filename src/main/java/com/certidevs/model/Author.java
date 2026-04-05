package com.certidevs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa un autor de libros.
 *
 * <h3>Relaciones JPA:</h3>
 * <ul>
 *   <li>{@code books} - OneToMany con {@link Book}: libros escritos por este autor.
 *       Usa {@code cascade = ALL} y {@code orphanRemoval = true}, por lo que al eliminar
 *       un autor se eliminan automáticamente todos sus libros.</li>
 * </ul>
 *
 * <h3>Validación:</h3>
 * <ul>
 *   <li>{@code @NotBlank} en {@code name}: garantiza que el nombre no sea nulo ni vacío.</li>
 *   <li>{@code @Column(length = 2000)} en {@code bio}: limita la biografia a 2000 caracteres en BD.</li>
 * </ul>
 *
 * @see Book
 */
@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"books"})
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del autor. Obligatorio y no puede estar en blanco. */
    @NotBlank
    @Column(nullable = false)
    private String name;

    /** Biografia del autor (máximo 2000 caracteres). */
    @Column(length = 2000)
    private String bio;

    /** Fecha de nacimiento del autor. */
    private LocalDate birthDate;

    /** Nacionalidad del autor (ej: "Colombiana", "Argentina"). */
    private String nationality;

    /**
     * Lista de libros escritos por este autor.
     *
     * <p>{@code mappedBy = "author"} indica que {@link Book#getAuthor()} es el lado propietario.
     * {@code orphanRemoval = true} elimina libros huerfanos cuando se quitan de la lista.</p>
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Book> books = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
