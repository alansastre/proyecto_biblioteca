package com.certidevs.config;

import com.certidevs.model.*;
import com.certidevs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Inicializador de datos de ejemplo para desarrollo y demostración.
 *
 * <p>Implementa {@link CommandLineRunner}, lo que hace que Spring Boot ejecute el método
 * {@link #run(String...)} automáticamente al arrancar la aplicación.</p>
 *
 * <p>Solo inserta datos si la base de datos está vacía ({@code authorRepository.count() > 0}).
 * Esto evita duplicados si la aplicación se reinicia.</p>
 *
 * <h3>Datos de ejemplo creados:</h3>
 * <ul>
 *   <li><strong>4 autores</strong>: Garcia Marquez, Allende, Borges, Cortazar</li>
 *   <li><strong>4 categorías</strong>: Novela, Realismo mágico, Cuento, Ensayo</li>
 *   <li><strong>5 libros</strong>: con relaciones a autores y categorías</li>
 *   <li><strong>2 usuarios</strong>: admin/admin (ROLE_ADMIN) y user/user (ROLE_USER)</li>
 *   <li><strong>5 reseñas</strong>: de ejemplo con puntuaciones variadas</li>
 *   <li><strong>3 compras</strong>: de ejemplo</li>
 *   <li><strong>2 favoritos</strong>: libros favoritos del usuario "user"</li>
 * </ul>
 *
 * <h3>Nota:</h3>
 * <p>Con {@code spring.jpa.hibernate.ddl-auto=create-drop} y BD en memoria (H2),
 * los datos se recrean cada vez que se arranca la aplicación. En producción se usaría
 * una BD persistente y migraciones con Flyway o Liquibase.</p>
 *
 * @see CommandLineRunner
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PurchaseRepository purchaseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Evitar duplicados si ya hay datos
        if (authorRepository.count() > 0) return;

        // ── Autores ──
        Author garciaMarquez = authorRepository.save(Author.builder()
                .name("Gabriel García Márquez").nationality("Colombiana")
                .birthDate(LocalDate.of(1927, 3, 6))
                .bio("Escritor y periodista colombiano, premio Nobel de Literatura en 1982. Máximo exponente del realismo mágico latinoamericano.")
                .build());

        Author allende = authorRepository.save(Author.builder()
                .name("Isabel Allende").nationality("Chilena")
                .birthDate(LocalDate.of(1942, 8, 2))
                .bio("Escritora chilena, una de las autoras más leídas en lengua española.")
                .build());

        Author borges = authorRepository.save(Author.builder()
                .name("Jorge Luis Borges").nationality("Argentina")
                .birthDate(LocalDate.of(1899, 8, 24))
                .bio("Escritor y poeta argentino, considerado uno de los autores más destacados de la literatura del siglo XX.")
                .build());

        Author cortazar = authorRepository.save(Author.builder()
                .name("Julio Cortázar").nationality("Argentina")
                .birthDate(LocalDate.of(1914, 8, 26))
                .bio("Escritor e intelectual argentino, maestro del relato corto y la prosa poética.")
                .build());

        // ── Categorías ──
        Category novela = categoryRepository.save(Category.builder()
                .name("Novela").description("Novelas y ficción narrativa extensa").color("#3498db").build());

        Category realismo = categoryRepository.save(Category.builder()
                .name("Realismo mágico").description("Corriente literaria del realismo mágico latinoamericano").color("#e74c3c").build());

        Category cuento = categoryRepository.save(Category.builder()
                .name("Cuento").description("Relatos cortos y colecciones de cuentos").color("#2ecc71").build());

        Category ensayo = categoryRepository.save(Category.builder()
                .name("Ensayo").description("Ensayos y no ficción").color("#9b59b6").build());

        // ── Libros (con relaciones ManyToOne y ManyToMany) ──
        Book cienAnios = bookRepository.save(Book.builder()
                .title("Cien años de soledad").price(15.90).available(true)
                .publishDate(LocalDate.of(1967, 5, 30))
                .isbn("978-3-16-148410-0").pages(471).language("Español")
                .synopsis("La historia de siete generaciones de la familia Buendía en el pueblo ficticio de Macondo.")
                .author(garciaMarquez).categories(List.of(novela, realismo)).build());

        Book casaEspiritus = bookRepository.save(Book.builder()
                .title("La casa de los espíritus").price(12.50).available(true)
                .publishDate(LocalDate.of(1982, 1, 1))
                .isbn("978-0-553-38357-1").pages(433).language("Español")
                .synopsis("Saga familiar que recorre cuatro generaciones en Chile.")
                .author(allende).categories(List.of(novela, realismo)).build());

        Book ficciones = bookRepository.save(Book.builder()
                .title("Ficciones").price(10.00).available(true)
                .publishDate(LocalDate.of(1944, 1, 1))
                .isbn("978-84-206-3313-1").pages(174).language("Español")
                .synopsis("Colección de relatos que exploran laberintos, espejos e infinitos.")
                .author(borges).categories(List.of(cuento, ensayo)).build());

        Book rayuela = bookRepository.save(Book.builder()
                .title("Rayuela").price(14.00).available(true)
                .publishDate(LocalDate.of(1963, 6, 28))
                .isbn("978-84-206-3314-8").pages(600).language("Español")
                .synopsis("Novela experimental que puede leerse en múltiples órdenes.")
                .author(cortazar).categories(List.of(novela)).build());

        Book amorColera = bookRepository.save(Book.builder()
                .title("El amor en los tiempos del colera").price(11.90).available(true)
                .publishDate(LocalDate.of(1985, 1, 1))
                .isbn("978-0-14-024488-2").pages(348).language("Español")
                .synopsis("Historia de amor que abarca más de cincuenta años entre Florentino Ariza y Fermina Daza.")
                .author(garciaMarquez).categories(List.of(novela, realismo)).build());

        // ── Usuarios (contraseñas codificadas con DelegatingPasswordEncoder) ──
        User admin = userRepository.save(User.builder()
                .username("admin").email("admin@biblioteca.local")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ROLE_ADMIN).build());

        User user = userRepository.save(User.builder()
                .username("user").email("user@biblioteca.local")
                .password(passwordEncoder.encode("user"))
                .role(Role.ROLE_USER).build());

        // ── Reseñas de ejemplo ──
        reviewRepository.save(Review.builder()
                .comment("Obra maestra absoluta de la literatura universal.").rating(5)
                .createdAt(LocalDateTime.now()).user(admin).book(cienAnios).build());

        reviewRepository.save(Review.builder()
                .comment("Muy recomendable, narrativa envolvente.").rating(4)
                .createdAt(LocalDateTime.now()).user(user).book(cienAnios).build());

        reviewRepository.save(Review.builder()
                .comment("Increíble saga familiar, no puedes dejar de leer.").rating(5)
                .createdAt(LocalDateTime.now()).user(user).book(casaEspiritus).build());

        reviewRepository.save(Review.builder()
                .comment("Complejo pero brillante, cada cuento es una joya.").rating(4)
                .createdAt(LocalDateTime.now()).user(admin).book(ficciones).build());

        reviewRepository.save(Review.builder()
                .comment("Una experiencia de lectura única e irrepetible. Cortázar es un genio.").rating(5)
                .createdAt(LocalDateTime.now()).user(user).book(rayuela).build());

        // ── Compras de ejemplo ──
        purchaseRepository.save(Purchase.builder()
                .user(user).book(cienAnios).purchasedAt(LocalDateTime.now()).build());

        purchaseRepository.save(Purchase.builder()
                .user(user).book(casaEspiritus).purchasedAt(LocalDateTime.now()).build());

        purchaseRepository.save(Purchase.builder()
                .user(admin).book(ficciones).purchasedAt(LocalDateTime.now()).build());

        // ── Favoritos (el lado propietario del ManyToMany es User) ──
        User managedUser = userRepository.findById(user.getId()).orElseThrow();
        managedUser.getFavoriteBooks().add(cienAnios);
        managedUser.getFavoriteBooks().add(ficciones);
        userRepository.save(managedUser);
    }
}
