package com.alura.literalura.service;

import com.alura.literalura.client.GutendexClient;
import com.alura.literalura.dto.AuthorDto;
import com.alura.literalura.dto.BookDto;
import com.alura.literalura.dto.GutenDexResponse;
import com.alura.literalura.model.Author;
import com.alura.literalura.model.Book;
import com.alura.literalura.repository.AuthorRepository;
import com.alura.literalura.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookServiceTest {

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private GutendexClient gutendexClient;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        authorRepository = mock(AuthorRepository.class);
        gutendexClient = mock(GutendexClient.class);
        bookService = new BookService(bookRepository, authorRepository, gutendexClient);
    }

    @Test
    void returnsNotFoundWhenGutendexReturnsNoBooks() {
        GutenDexResponse response = new GutenDexResponse();
        response.setResults(List.of());
        when(gutendexClient.searchBooks("missing")).thenReturn(response);

        BookImportResult result = bookService.fetchAndSaveBookByTitle("missing");

        assertEquals(BookImportResult.NOT_FOUND, result);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void preventsDuplicateBooksUsingGutendexId() {
        BookDto dto = bookDto();
        GutenDexResponse response = new GutenDexResponse();
        response.setResults(List.of(dto));
        when(gutendexClient.searchBooks("Pride and Prejudice")).thenReturn(response);
        when(bookRepository.existsByGutendexId(1342L)).thenReturn(true);

        BookImportResult result = bookService.fetchAndSaveBookByTitle("Pride and Prejudice");

        assertEquals(BookImportResult.DUPLICATE, result);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void savesMappedBookAuthorsAndLanguages() {
        BookDto dto = bookDto();
        GutenDexResponse response = new GutenDexResponse();
        response.setResults(List.of(dto));
        when(gutendexClient.searchBooks("Pride and Prejudice")).thenReturn(response);
        when(bookRepository.existsByGutendexId(1342L)).thenReturn(false);
        when(authorRepository.findByNameIgnoreCase("Austen, Jane")).thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookImportResult result = bookService.fetchAndSaveBookByTitle("Pride and Prejudice");

        assertEquals(BookImportResult.SAVED, result);
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());

        Book saved = captor.getValue();
        assertEquals(1342L, saved.getGutendexId());
        assertEquals("Pride and Prejudice", saved.getTitle());
        assertEquals(12345, saved.getDownloadCount());
        assertTrue(saved.getLanguages().contains("en"));
        assertEquals(1, saved.getAuthors().size());
        assertEquals("Austen, Jane", saved.getAuthors().iterator().next().getName());
    }

    private BookDto bookDto() {
        AuthorDto author = new AuthorDto();
        author.setName("Austen, Jane");
        author.setBirthYear(1775);
        author.setDeathYear(1817);

        BookDto dto = new BookDto();
        dto.setId(1342L);
        dto.setTitle("Pride and Prejudice");
        dto.setAuthors(List.of(author));
        dto.setLanguages(List.of("en"));
        dto.setDownloadCount(12345);
        return dto;
    }
}
