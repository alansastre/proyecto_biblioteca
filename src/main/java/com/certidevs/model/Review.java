package com.certidevs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una reseña de un libro escrita por un usuario.
 *
 * <p>Cada reseña contiene un comentario de texto y una puntuación de 1 a 5 estrellas.
 * Solo el usuario que escribio la reseña puede editarla o eliminarla
 * (controlado en {@link com.certidevs.service.ReviewService#canModify}).</p>
 *
 * <h3>Relaciones JPA:</h3>
 * <ul>
 *   <li>{@code user} - ManyToOne con {@link User}: autor de la reseña (obligatorio).</li>
 *   <li>{@code book} - ManyToOne con {@link Book}: libro reseñado (obligatorio).</li>
 * </ul>
 *
 * <h3>Validación:</h3>
 * <ul>
 *   <li>{@code @NotBlank} en {@code comment}: el comentario no puede estar vacío.</li>
 *   <li>{@code @NotNull @Min(1) @Max(5)} en {@code rating}: puntuación obligatoria entre 1 y 5.</li>
 * </ul>
 *
 * @see User
 * @see Book
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "book"})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Texto de la reseña (máximo 2000 caracteres). Obligatorio. */
    @NotBlank
    @Column(length = 2000, nullable = false)
    private String comment;

    /** Puntuacion de 1 a 5 estrellas. Obligatoria. */
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    /** Fecha y hora de creación de la reseña. Se asigna automáticamente. */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Usuario que escribio la reseña. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Libro al que se refiere la reseña. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
