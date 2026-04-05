package com.certidevs.service;

import com.certidevs.model.Book;
import com.certidevs.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio con la lógica de negocio para la gestión de libros.
 *
 * <p>Proporciona operaciones CRUD y múltiples estrategias de búsqueda/filtrado:
 * por título, autor, categoría y rango de precios. Los parámetros null se manejan
 * devolviendo todos los libros (patron "null-safe filter").</p>
 *
 * @see BookRepository
 * @see Book
 */
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Obtiene todos los libros.
     *
     * @return lista de todos los libros
     */
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    /**
     * Obtiene los libros listos para renderizar el catálogo sin disparar cargas LAZY
     * en la plantilla.
     */
    @Transactional(readOnly = true)
    public List<Book> findAllForList() {
        return bookRepository.findAllForList();
    }

    /**
     * Busca un libro por su ID.
     *
     * @param id identificador del libro
     * @return Optional con el libro si existe
     */
    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    /**
     * Carga el detalle del libro con las asociaciones necesarias para la pantalla de detalle.
     */
    @Transactional(readOnly = true)
    public Optional<Book> findByIdForDetail(Long id) {
        return bookRepository.findDetailedById(id);
    }

    /**
     * Carga el libro con las asociaciones necesarias para el formulario de edición.
     */
    @Transactional(readOnly = true)
    public Optional<Book> findByIdForForm(Long id) {
        return bookRepository.findFormById(id);
    }

    /**
     * Guarda o actualiza un libro.
     *
     * @param book libro a guardar
     * @return libro persistido con ID asignado
     */
    @Transactional
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    /**
     * Elimina un libro por su ID. También elimina sus reseñas y compras en cascada.
     *
     * @param id identificador del libro a eliminar
     */
    @Transactional
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    /**
     * Busca libros cuyo título contenga el texto dado.
     * Si el título es null o vacío, devuelve todos los libros.
     *
     * @param title texto a buscar (puede ser null o vacío)
     * @return lista de libros que coinciden
     */
    @Transactional(readOnly = true)
    public List<Book> findByTitleContaining(String title) {
        return title == null || title.isBlank() ? findAll() : bookRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Variante para listados filtrados por título con el autor precargado.
     */
    @Transactional(readOnly = true)
    public List<Book> findByTitleContainingForList(String title) {
        return title == null || title.isBlank()
                ? findAllForList()
                : bookRepository.findByTitleContainingIgnoreCaseOrderByTitleAsc(title);
    }

    /**
     * Busca libros por el ID de su autor.
     * Si el authorId es null, devuelve todos los libros.
     *
     * @param authorId ID del autor (puede ser null)
     * @return lista de libros del autor
     */
    @Transactional(readOnly = true)
    public List<Book> findByAuthorId(Long authorId) {
        return authorId == null ? findAll() : bookRepository.findByAuthorId(authorId);
    }

    /**
     * Variante para listados filtrados por autor con el autor precargado.
     */
    @Transactional(readOnly = true)
    public List<Book> findByAuthorIdForList(Long authorId) {
        return authorId == null ? findAllForList() : bookRepository.findByAuthorIdOrderByTitleAsc(authorId);
    }

    /**
     * Busca libros que pertenezcan a una categoría especifica.
     * Si el categoryId es null, devuelve todos los libros.
     *
     * @param categoryId ID de la categoría (puede ser null)
     * @return lista de libros en esa categoría
     */
    @Transactional(readOnly = true)
    public List<Book> findByCategoryId(Long categoryId) {
        return categoryId == null ? findAll() : bookRepository.findByCategoryId(categoryId);
    }

    /**
     * Variante para listados filtrados por categoría con el autor precargado.
     */
    @Transactional(readOnly = true)
    public List<Book> findByCategoryIdForList(Long categoryId) {
        return categoryId == null ? findAllForList() : bookRepository.findByCategoryIdForList(categoryId);
    }

    /**
     * Busca libros cuyo precio este en un rango dado.
     * Si ambos limites son null, devuelve todos los libros.
     * Si solo falta un limite, se usa 0.0 o Double.MAX_VALUE respectivamente.
     *
     * @param min precio minimo (puede ser null, se usa 0.0)
     * @param max precio máximo (puede ser null, se usa Double.MAX_VALUE)
     * @return lista de libros en el rango de precio
     */
    @Transactional(readOnly = true)
    public List<Book> findByPriceBetween(Double min, Double max) {
        if (min == null && max == null) return findAll();
        if (min == null) min = 0.0;
        if (max == null) max = Double.MAX_VALUE;
        return bookRepository.findByPriceBetween(min, max);
    }

    /**
     * Variante para listados filtrados por precio con el autor precargado.
     */
    @Transactional(readOnly = true)
    public List<Book> findByPriceBetweenForList(Double min, Double max) {
        if (min == null && max == null) return findAllForList();
        if (min == null) min = 0.0;
        if (max == null) max = Double.MAX_VALUE;
        return bookRepository.findByPriceBetweenOrderByTitleAsc(min, max);
    }
}
