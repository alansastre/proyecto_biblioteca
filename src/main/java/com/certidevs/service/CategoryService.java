package com.certidevs.service;

import com.certidevs.model.Category;
import com.certidevs.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio con la lógica de negocio para la gestión de categorías.
 *
 * @see CategoryRepository
 * @see Category
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Obtiene todas las categorías.
     *
     * @return lista de todas las categorías
     */
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /**
     * Busca una categoría por su ID.
     *
     * @param id identificador de la categoría
     * @return Optional con la categoría si existe
     */
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Carga una categoría con sus libros para la pantalla de detalle.
     */
    @Transactional(readOnly = true)
    public Optional<Category> findByIdForDetail(Long id) {
        return categoryRepository.findDetailedById(id);
    }

    /**
     * Guarda o actualiza una categoría.
     *
     * @param category categoría a guardar
     * @return categoría persistida con ID asignado
     */
    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Elimina una categoría por su ID.
     *
     * @param id identificador de la categoría a eliminar
     */
    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    /**
     * Busca categorías cuyo nombre contenga el texto dado.
     * Si el nombre es null o vacío, devuelve todas las categorías.
     *
     * @param name texto a buscar (puede ser null o vacío)
     * @return lista de categorías que coinciden
     */
    @Transactional(readOnly = true)
    public List<Category> findByNameContaining(String name) {
        return name == null || name.isBlank() ? findAll() : categoryRepository.findByNameContainingIgnoreCase(name);
    }
}
