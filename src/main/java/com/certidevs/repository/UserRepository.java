package com.certidevs.repository;

import com.certidevs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link User}.
 *
 * <p>Extiende {@link JpaRepository} que proporciona automáticamente operaciones CRUD
 * ({@code save}, {@code findById}, {@code findAll}, {@code deleteById}, {@code count}, etc.)
 * sin necesidad de implementación manual.</p>
 *
 * <h3>Derived Query Methods (consultas derivadas del nombre del método):</h3>
 * <p>Spring Data JPA genera automáticamente la consulta SQL a partir del nombre del método.
 * Por ejemplo, {@code findByUsername} genera: {@code SELECT u FROM User u WHERE u.username = ?1}</p>
 *
 * @see JpaRepository
 * @see User
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     * Usado por {@link com.certidevs.config.CustomUserDetailsService} para la autenticación.
     *
     * @param username nombre de usuario a buscar
     * @return Optional con el usuario si existe, vacío si no
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su email.
     *
     * @param email email a buscar
     * @return Optional con el usuario si existe, vacío si no
     */
    Optional<User> findByEmail(String email);

    /**
     * Comprueba si existe un usuario con ese nombre (para validar registro).
     * Mas eficiente que {@code findByUsername} cuando solo se necesita saber si existe.
     *
     * @param username nombre de usuario a comprobar
     * @return true si ya existe un usuario con ese nombre
     */
    boolean existsByUsername(String username);

    /**
     * Comprueba si existe un usuario con ese email (para validar registro).
     *
     * @param email email a comprobar
     * @return true si ya existe un usuario con ese email
     */
    boolean existsByEmail(String email);
}
