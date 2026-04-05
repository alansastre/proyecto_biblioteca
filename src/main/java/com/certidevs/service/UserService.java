package com.certidevs.service;

import com.certidevs.model.Book;
import com.certidevs.model.Role;
import com.certidevs.model.User;
import com.certidevs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio con la lógica de negocio para la gestión de usuarios.
 *
 * <p>Gestiona operaciones CRUD, registro de nuevos usuarios, codificación de contraseñas
 * y gestión de libros favoritos.</p>
 *
 * <h3>Patrones utilizados:</h3>
 * <ul>
 *   <li>{@code @Service}: marca la clase como componente de servicio en Spring (capa de negocio).</li>
 *   <li>{@code @RequiredArgsConstructor}: Lombok genera un constructor con los campos {@code final},
 *       que Spring usa para inyeccion de dependencias por constructor.</li>
 *   <li>{@code @Transactional}: cada método se ejecuta dentro de una transaccion de base de datos.
 *       Con {@code readOnly = true} se optimizan las consultas de solo lectura.</li>
 * </ul>
 *
 * @see UserRepository
 * @see User
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtiene todos los usuarios del sistema.
     *
     * @return lista de todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id identificador del usuario
     * @return Optional con el usuario si existe
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario a buscar
     * @return Optional con el usuario si existe
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Guarda o actualiza un usuario. Si la contraseña no está codificada
     * (no empieza por "{"), la codifica automáticamente con el {@link PasswordEncoder}.
     *
     * <p>El {@code DelegatingPasswordEncoder} añade un prefijo como {@code {bcrypt}} a las
     * contraseñas codificadas, por lo que comprobamos si empieza por "{" para evitar
     * codificar una contraseña que ya está codificada.</p>
     *
     * @param user usuario a guardar
     * @return usuario persistido con ID asignado
     */
    @Transactional
    public User save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("{")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id identificador del usuario a eliminar
     */
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Registra un nuevo usuario con rol {@code ROLE_USER}.
     *
     * <p>Valida que el nombre de usuario y el email no esten ya en uso.
     * La contraseña se codifica automáticamente en el método {@link #save(User)}.</p>
     *
     * @param username    nombre de usuario deseado
     * @param email       email del usuario
     * @param rawPassword contraseña sin codificar
     * @return usuario registrado y persistido
     * @throws IllegalArgumentException si el username o email ya existen
     */
    @Transactional
    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Usuario ya existe: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email ya registrado: " + email);
        }
        User user = User.builder()
                .username(username)
                .email(email)
                .password(rawPassword)
                .role(Role.ROLE_USER)
                .build();
        return save(user);
    }

    /**
     * Obtiene los libros favoritos del usuario, cargando la colección LAZY dentro de la transaccion.
     *
     * <p>Sin {@code @Transactional}, acceder a {@code user.getFavoriteBooks()} fuera de la sesión
     * de Hibernate lanzaria {@code LazyInitializationException}. Este método resuelve ese problema
     * cargando los favoritos dentro de una transaccion activa.</p>
     *
     * @param user usuario autenticado
     * @return lista de libros favoritos (vacia si el usuario es null o no existe)
     */
    @Transactional(readOnly = true)
    public List<Book> getFavorites(User user) {
        if (user == null) return List.of();
        User managed = userRepository.findById(user.getId()).orElse(null);
        if (managed == null) return List.of();
        return new ArrayList<>(managed.getFavoriteBooks());
    }

    /**
     * Añade un libro a los favoritos del usuario.
     *
     * @param user usuario autenticado
     * @param book libro a añadir como favorito
     */
    @Transactional
    public void addFavorite(User user, Book book) {
        User managed = userRepository.findById(user.getId()).orElseThrow();
        managed.getFavoriteBooks().add(book);
        userRepository.save(managed);
    }

    /**
     * Elimina un libro de los favoritos del usuario.
     *
     * @param user usuario autenticado
     * @param book libro a quitar de favoritos
     */
    @Transactional
    public void removeFavorite(User user, Book book) {
        User managed = userRepository.findById(user.getId()).orElseThrow();
        managed.getFavoriteBooks().removeIf(b -> b.getId().equals(book.getId()));
        userRepository.save(managed);
    }

    /**
     * Comprueba si un libro está en los favoritos del usuario.
     *
     * @param user usuario autenticado (puede ser null)
     * @param book libro a comprobar (puede ser null)
     * @return true si el libro es favorito del usuario
     */
    @Transactional(readOnly = true)
    public boolean hasFavorite(User user, Book book) {
        if (user == null || book == null) return false;
        User managed = userRepository.findById(user.getId()).orElse(null);
        return managed != null && managed.getFavoriteBooks().stream()
                .anyMatch(b -> b.getId().equals(book.getId()));
    }
}
