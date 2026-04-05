package com.certidevs.service;

import com.certidevs.model.Book;
import com.certidevs.model.Purchase;
import com.certidevs.model.User;
import com.certidevs.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio con la lógica de negocio para la gestión de compras.
 *
 * @see PurchaseRepository
 * @see Purchase
 */
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    /**
     * Obtiene todas las compras del sistema.
     *
     * @return lista de todas las compras
     */
    @Transactional(readOnly = true)
    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    /**
     * Busca todas las compras realizadas por un usuario.
     *
     * @param userId ID del usuario
     * @return lista de compras del usuario
     */
    @Transactional(readOnly = true)
    public List<Purchase> findByUserId(Long userId) {
        return purchaseRepository.findByUserId(userId);
    }

    /**
     * Variante para vistas con el libro ya cargado y orden cronológico inverso.
     */
    @Transactional(readOnly = true)
    public List<Purchase> findByUserIdForView(Long userId) {
        return purchaseRepository.findByUserIdOrderByPurchasedAtDesc(userId);
    }

    /**
     * Registra una nueva compra de un libro por un usuario.
     * La fecha de compra se establece automáticamente al momento actual.
     *
     * @param user usuario comprador
     * @param book libro comprado
     * @return compra registrada y persistida
     */
    @Transactional
    public Purchase buy(User user, Book book) {
        Purchase purchase = Purchase.builder()
                .user(user)
                .book(book)
                .purchasedAt(LocalDateTime.now())
                .build();
        return purchaseRepository.save(purchase);
    }
}
