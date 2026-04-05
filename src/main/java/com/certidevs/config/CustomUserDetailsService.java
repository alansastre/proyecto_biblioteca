package com.certidevs.config;

import com.certidevs.model.User;
import com.certidevs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion personalizada de {@link UserDetailsService} para Spring Security.
 *
 * <p>Spring Security utiliza esta clase durante el proceso de autenticación para cargar
 * los datos del usuario desde la base de datos. Al recibir las credenciales del formulario
 * de login, Spring Security llama a {@link #loadUserByUsername(String)} para obtener
 * el {@link UserDetails} y comparar la contraseña.</p>
 *
 * <h3>Flujo de autenticación:</h3>
 * <ol>
 *   <li>El usuario envia username + password en el formulario de login.</li>
 *   <li>Spring Security llama a {@code loadUserByUsername(username)}.</li>
 *   <li>Este método busca el {@link User} en la BD via {@link UserRepository}.</li>
 *   <li>Como {@link User} implementa {@link UserDetails}, se devuelve directamente.</li>
 *   <li>Spring Security compara la contraseña enviada con la almacenada usando el {@code PasswordEncoder}.</li>
 *   <li>Si coinciden, el {@link User} queda almacenado en el {@code SecurityContext}
 *       y es accesible con {@code @AuthenticationPrincipal User user} en los controladores.</li>
 * </ol>
 *
 * <h3>Nota sobre @Transactional:</h3>
 * <p>{@code readOnly = true} optimiza la transaccion ya que solo se lee de BD, no se escribe.</p>
 *
 * @see User
 * @see UserRepository
 * @see org.springframework.security.core.userdetails.UserDetailsService
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga un usuario por su nombre de usuario para la autenticación de Spring Security.
     *
     * @param username nombre de usuario recibido del formulario de login
     * @return el usuario como {@link UserDetails}
     * @throws UsernameNotFoundException si no se encuentra el usuario
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}
