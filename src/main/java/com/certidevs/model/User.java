package com.certidevs.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Entidad JPA que representa un usuario del sistema.
 *
 * <p>Implementa {@link UserDetails} de Spring Security, lo que permite que JPA y Spring Security
 * compartan la misma clase. Asi, al autenticarse, el objeto {@code User} queda disponible
 * directamente como principal en el contexto de seguridad mediante
 * {@code @AuthenticationPrincipal User user} en los controladores.</p>
 *
 * <h3>Relaciones JPA:</h3>
 * <ul>
 *   <li>{@code favoriteBooks} - ManyToMany con {@link Book} (lado propietario): libros favoritos del usuario.</li>
 *   <li>{@code reviews} - OneToMany con {@link Review}: reseñas escritas por el usuario.</li>
 *   <li>{@code purchases} - OneToMany con {@link Purchase}: compras realizadas por el usuario.</li>
 * </ul>
 *
 * <h3>Notas de implementación:</h3>
 * <ul>
 *   <li>{@code @ToString.Exclude} en colecciones evita bucles infinitos al imprimir la entidad.</li>
 *   <li>{@code @Builder.Default} es necesario para que Lombok {@code @Builder} respete valores por defecto.</li>
 *   <li>{@code equals/hashCode} se sobreescriben manualmente siguiendo buenas prácticas JPA (Vlad Mihalcea).</li>
 * </ul>
 *
 * @see Role
 * @see org.springframework.security.core.userdetails.UserDetails
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"favoriteBooks", "reviews", "purchases"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de usuario único, utilizado para el login. */
    @Column(nullable = false, unique = true)
    private String username;

    /** Email único del usuario. */
    @Column(nullable = false, unique = true)
    private String email;

    /** Contraseña codificada con {@link org.springframework.security.crypto.password.DelegatingPasswordEncoder}. */
    @Column(nullable = false)
    private String password;

    /** Rol del usuario que determina sus permisos en el sistema. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Libros favoritos del usuario (relacion ManyToMany).
     *
     * <p>Este es el lado <strong>propietario</strong> de la relacion, por lo que la tabla intermedia
     * {@code user_favorites} se define aqui con {@code @JoinTable}.</p>
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @Builder.Default
    private Set<Book> favoriteBooks = new HashSet<>();

    /** Reseñas escritas por este usuario. Se eliminan en cascada si se borra el usuario. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    /** Compras realizadas por este usuario. Se eliminan en cascada si se borra el usuario. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Purchase> purchases = new ArrayList<>();

    // ──────────────────────────────────────────────
    // Implementacion de UserDetails (Spring Security)
    // ──────────────────────────────────────────────

    /**
     * Devuelve las autoridades (roles) del usuario.
     * Spring Security usa esta lista para evaluar reglas como {@code hasRole("ADMIN")}.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // ──────────────────────────────────────────────
    // equals / hashCode para JPA
    // ──────────────────────────────────────────────

    /**
     * Comparacion basada en {@code id} siguiendo el patron recomendado para entidades JPA.
     * Dos entidades son iguales si tienen el mismo {@code id} no nulo.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id != null && id.equals(other.getId());
    }

    /**
     * hashCode constante por clase para compatibilidad con {@code Set} y {@code Map}
     * tanto antes como después de persistir la entidad.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
