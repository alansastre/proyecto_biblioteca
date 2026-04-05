package com.certidevs.service;

import com.certidevs.model.Author;
import com.certidevs.repository.AuthorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario para {@link AuthorService} usando Mockito.
 *
 * <p>Los tests unitarios aislan la clase bajo prueba "mockeando" (simulando)
 * sus dependencias. Aqui se mockea {@link AuthorRepository} para probar
 * solo la lógica de {@link AuthorService}.</p>
 *
 * <h3>Anotaciones Mockito:</h3>
 * <ul>
 *   <li>{@code @ExtendWith(MockitoExtension.class)}: activa las anotaciones de Mockito.</li>
 *   <li>{@code @Mock}: crea un mock (objeto simulado) del repositorio.</li>
 *   <li>{@code @InjectMocks}: crea la instancia del servicio inyectando los mocks.</li>
 * </ul>
 *
 * <h3>Patron Given-When-Then (AAA: Arrange-Act-Assert):</h3>
 * <ol>
 *   <li><strong>Given</strong>: preparar datos y configurar el comportamiento del mock.</li>
 *   <li><strong>When</strong>: ejecutar el método bajo prueba.</li>
 *   <li><strong>Then</strong>: verificar el resultado y las interacciones con el mock.</li>
 * </ol>
 *
 * @see AuthorService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorService - Tests unitarios con Mockito")
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    @DisplayName("findAll: delega en el repositorio y devuelve todos los autores")
    void findAll_returnsAllFromRepository() {
        // Given: el repositorio devuelve una lista con un autor
        Author a = Author.builder().id(1L).name("Test Author").build();
        when(authorRepository.findAll()).thenReturn(List.of(a));

        // When: llamamos al servicio
        List<Author> result = authorService.findAll();

        // Then: el resultado es correcto y se llamo al repositorio
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Test Author");
        verify(authorRepository).findAll();
    }

    @Test
    @DisplayName("findById: devuelve Optional con el autor si existe")
    void findById_returnsAuthorWhenExists() {
        Author a = Author.builder().id(1L).name("Borges").build();
        when(authorRepository.findById(1L)).thenReturn(Optional.of(a));

        Optional<Author> result = authorService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Borges");
    }

    @Test
    @DisplayName("findById: devuelve Optional vacío si no existe")
    void findById_returnsEmptyWhenNotExists() {
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Author> result = authorService.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save: delega en el repositorio y devuelve el autor guardado")
    void save_delegatesToRepository() {
        Author a = Author.builder().name("New Author").build();
        when(authorRepository.save(any(Author.class))).thenAnswer(inv -> {
            Author saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Author result = authorService.save(a);

        assertThat(result.getId()).isEqualTo(1L);
        verify(authorRepository).save(a);
    }

    @Test
    @DisplayName("findByNameContaining: con nombre null devuelve todos")
    void findByNameContaining_withNull_returnsAll() {
        when(authorRepository.findAll()).thenReturn(List.of(Author.builder().id(1L).name("A").build()));

        List<Author> result = authorService.findByNameContaining(null);

        assertThat(result).hasSize(1);
        verify(authorRepository).findAll();
        verify(authorRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("findByNameContaining: con nombre no vacío busca por nombre")
    void findByNameContaining_withValue_searchesByName() {
        when(authorRepository.findByNameContainingIgnoreCase("test")).thenReturn(List.of());

        authorService.findByNameContaining("test");

        verify(authorRepository).findByNameContainingIgnoreCase("test");
        verify(authorRepository, never()).findAll();
    }

    @Test
    @DisplayName("deleteById: delega la eliminación en el repositorio")
    void deleteById_delegatesToRepository() {
        authorService.deleteById(1L);

        verify(authorRepository).deleteById(1L);
    }
}
