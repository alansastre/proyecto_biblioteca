package com.certidevs.service;

import com.certidevs.model.Author;
import com.certidevs.model.Book;
import com.certidevs.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test unitario para {@link BookService}.
 *
 * <p>Verifica la lógica de negocio del servicio de libros, especialmente
 * el manejo de parámetros null en los métodos de filtrado.</p>
 *
 * @see BookService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BookService - Tests unitarios con Mockito")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("findByTitleContaining: con título vacío devuelve todos los libros")
    void findByTitleContaining_withBlank_returnsAll() {
        when(bookRepository.findAll()).thenReturn(List.of(
                Book.builder().id(1L).title("Libro 1").build(),
                Book.builder().id(2L).title("Libro 2").build()
        ));

        List<Book> result = bookService.findByTitleContaining("");

        assertThat(result).hasSize(2);
        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("findByTitleContaining: con título busca en repositorio")
    void findByTitleContaining_withValue_searches() {
        when(bookRepository.findByTitleContainingIgnoreCase("cien"))
                .thenReturn(List.of(Book.builder().id(1L).title("Cien años").build()));

        List<Book> result = bookService.findByTitleContaining("cien");

        assertThat(result).hasSize(1);
        verify(bookRepository).findByTitleContainingIgnoreCase("cien");
    }

    @Test
    @DisplayName("findByPriceBetween: con ambos null devuelve todos")
    void findByPriceBetween_withBothNull_returnsAll() {
        when(bookRepository.findAll()).thenReturn(List.of());

        bookService.findByPriceBetween(null, null);

        verify(bookRepository).findAll();
        verify(bookRepository, never()).findByPriceBetween(any(), any());
    }

    @Test
    @DisplayName("findByPriceBetween: con min null usa 0.0")
    void findByPriceBetween_withNullMin_usesZero() {
        when(bookRepository.findByPriceBetween(0.0, 20.0)).thenReturn(List.of());

        bookService.findByPriceBetween(null, 20.0);

        verify(bookRepository).findByPriceBetween(0.0, 20.0);
    }

    @Test
    @DisplayName("findByPriceBetween: con max null usa Double.MAX_VALUE")
    void findByPriceBetween_withNullMax_usesMaxValue() {
        when(bookRepository.findByPriceBetween(10.0, Double.MAX_VALUE)).thenReturn(List.of());

        bookService.findByPriceBetween(10.0, null);

        verify(bookRepository).findByPriceBetween(10.0, Double.MAX_VALUE);
    }

    @Test
    @DisplayName("findById: devuelve el libro cuando existe")
    void findById_returnsBookWhenExists() {
        Book book = Book.builder().id(1L).title("Test Book").build();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Book");
    }
}
