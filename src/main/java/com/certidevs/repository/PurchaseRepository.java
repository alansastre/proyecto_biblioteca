package com.certidevs.repository;

import com.certidevs.model.Purchase;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Purchase}.
 *
 * @see JpaRepository
 * @see Purchase
 */
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    /**
     * Busca todas las compras realizadas por un usuario.
     *
     * @param userId ID del usuario
     * @return lista de compras del usuario
     */
    List<Purchase> findByUserId(Long userId);

    /**
     * Variante para vistas: precarga el libro y ordena por fecha descendente.
     */
    @EntityGraph(attributePaths = "book")
    List<Purchase> findByUserIdOrderByPurchasedAtDesc(Long userId);

    /**
     * Busca todas las compras de un libro específico.
     *
     * @param bookId ID del libro
     * @return lista de compras de ese libro
     */
    List<Purchase> findByBookId(Long bookId);
}
