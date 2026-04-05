package com.certidevs.service;

import com.certidevs.model.Author;
import com.certidevs.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio con la lógica de negocio para la gestión de autores.
 *
 * <p>Proporciona operaciones CRUD y búsquedas filtradas por nombre y nacionalidad.
 * Los métodos de búsqueda manejan parámetros null o vacíos devolviendo todos los autores.</p>
 *
 * @see AuthorRepository
 * @see Author
 */
@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    /**
     * Obtiene todos los autores.
     *
     * @return lista de todos los autores
     */
    @Transactional(readOnly = true)
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    /**
     * Busca un autor por su ID.
     *
     * @param id identificador del autor
     * @return Optional con el autor si existe
     */
    @Transactional(readOnly = true)
    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }

    /**
     * Carga un autor con sus libros para la pantalla de detalle.
     */
    @Transactional(readOnly = true)
    public Optional<Author> findByIdForDetail(Long id) {
        return authorRepository.findDetailedById(id);
    }

    /**
     * Guarda o actualiza un autor.
     *
     * @param author autor a guardar
     * @return autor persistido con ID asignado
     */
    @Transactional
    public Author save(Author author) {
        return authorRepository.save(author);
    }

    /**
     * Elimina un autor por su ID. También elimina sus libros en cascada.
     *
     * @param id identificador del autor a eliminar
     */
    @Transactional
    public void deleteById(Long id) {
        authorRepository.deleteById(id);
    }

    /**
     * Busca autores cuyo nombre contenga el texto dado.
     * Si el nombre es null o vacío, devuelve todos los autores.
     *
     * @param name texto a buscar (puede ser null o vacío)
     * @return lista de autores que coinciden
     */
    @Transactional(readOnly = true)
    public List<Author> findByNameContaining(String name) {
        return name == null || name.isBlank() ? findAll() : authorRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Busca autores por nacionalidad exacta (case-insensitive).
     * Si la nacionalidad es null o vacia, devuelve todos los autores.
     *
     * @param nationality nacionalidad a buscar (puede ser null o vacia)
     * @return lista de autores de esa nacionalidad
     */
    @Transactional(readOnly = true)
    public List<Author> findByNationality(String nationality) {
        return nationality == null || nationality.isBlank() ? findAll() : authorRepository.findByNationalityIgnoreCase(nationality);
    }
}
