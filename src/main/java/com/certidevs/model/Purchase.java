package com.certidevs.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la compra de un libro por un usuario.
 *
 * <p>Registra el momento exacto de la compra mediante {@code purchasedAt}.
 * Un mismo usuario puede comprar el mismo libro varias veces (no hay restricción de unicidad).</p>
 *
 * <h3>Relaciones JPA:</h3>
 * <ul>
 *   <li>{@code user} - ManyToOne con {@link User}: comprador (obligatorio).</li>
 *   <li>{@code book} - ManyToOne con {@link Book}: libro comprado (obligatorio).</li>
 * </ul>
 *
 * @see User
 * @see Book
 * @see com.certidevs.service.PurchaseService#buy(User, Book)
 */
@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "book"})
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fecha y hora en que se realizo la compra. Se asigna automáticamente. */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime purchasedAt = LocalDateTime.now();

    /** Usuario que realizo la compra. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Libro comprado. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
