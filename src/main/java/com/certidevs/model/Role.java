package com.certidevs.model;

/**
 * Enum que define los roles de usuario en el sistema de seguridad.
 *
 * <p>Spring Security utiliza el prefijo {@code ROLE_} por convención para distinguir
 * roles de otras autoridades. Al usar {@code hasRole("ADMIN")} en la configuración
 * de seguridad, Spring Security busca internamente {@code ROLE_ADMIN}.</p>
 *
 * <p>Se almacena como {@code String} en base de datos gracias a
 * {@code @Enumerated(EnumType.STRING)} en la entidad {@link User}.</p>
 *
 * @see User#getRole()
 * @see org.springframework.security.core.GrantedAuthority
 */
public enum Role {

    /** Rol para usuarios registrados con permisos básicos (ver, comprar, reseñar). */
    ROLE_USER,

    /** Rol para administradores con permisos completos (CRUD de autores, libros, categorías). */
    ROLE_ADMIN
}
