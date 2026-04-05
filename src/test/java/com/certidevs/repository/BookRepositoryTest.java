package com.certidevs.repository;

import com.certidevs.model.Author;
import com.certidevs.model.Book;
import com.certidevs.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración para {@link BookRepository}.
 *
 * <p>Demuestra cómo testear derived queries y consultas JPQL personalizadas
 * (@Query) con relaciones ManyToOne y ManyToMany.</p>
 *
 * <h3>@BeforeEach:</h3>
 * <p>Se usa para insertar datos comunes antes de cada test.
 * Gracias al rollback automático de @DataJpaTest, cada test empieza con BD limpia.</p>
 *
 * @see BookRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BookRepository - Tests de integración JPA")
class BookRepositoryTest {

    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private CategoryRepository categoryRepository;

    private Author author;
    private Category novela;
    private Category cuento;

    @BeforeEach
    void setUp() {
        author = authorRepository.save(Author.builder().name("Test Author").nationality("ES").build());
        novela = categoryRepository.save(Category.builder().name("Novela").build());
        cuento = categoryRepository.save(Category.builder().name("Cuento").build());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase: busca libros por título parcial")
    void findByTitleContainingIgnoreCase_works() {
        bookRepository.save(Book.builder().title("Cien años de soledad").price(15.0).author(author).build());
        bookRepository.save(Book.builder().title("Ficciones").price(10.0).author(author).build());

        List<Book> result = bookRepository.findByTitleContainingIgnoreCase("cien");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Cien años de soledad");
    }

    @Test
    @DisplayName("findByAuthorId: busca libros de un autor específico")
    void findByAuthorId_returnsAuthorBooks() {
        Author otherAuthor = authorRepository.save(Author.builder().name("Otro").nationality("AR").build());
        bookRepository.save(Book.builder().title("Libro 1").author(author).build());
        bookRepository.save(Book.builder().title("Libro 2").author(author).build());
        bookRepository.save(Book.builder().title("Libro 3").author(otherAuthor).build());

        List<Book> result = bookRepository.findByAuthorId(author.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findByPriceBetween: busca libros en un rango de precio")
    void findByPriceBetween_returnsInRange() {
        bookRepository.save(Book.builder().title("Barato").price(5.0).author(author).build());
        bookRepository.save(Book.builder().title("Medio").price(15.0).author(author).build());
        bookRepository.save(Book.builder().title("Caro").price(30.0).author(author).build());

        List<Book> result = bookRepository.findByPriceBetween(10.0, 20.0);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Medio");
    }

    @Test
    @DisplayName("findByCategoryId: busca libros por categoría usando @Query JPQL")
    void findByCategoryId_usesJpqlJoin() {
        Book book1 = bookRepository.save(Book.builder().title("Novela 1").author(author)
                .categories(List.of(novela)).build());
        Book book2 = bookRepository.save(Book.builder().title("Cuento 1").author(author)
                .categories(List.of(cuento)).build());
        Book book3 = bookRepository.save(Book.builder().title("Ambas").author(author)
                .categories(List.of(novela, cuento)).build());

        List<Book> novelaBooks = bookRepository.findByCategoryId(novela.getId());

        assertThat(novelaBooks).hasSize(2);
        assertThat(novelaBooks).extracting(Book::getTitle).containsExactlyInAnyOrder("Novela 1", "Ambas");
    }
}
