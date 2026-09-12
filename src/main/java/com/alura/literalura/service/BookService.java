package com.alura.literalura.service;

import com.alura.literalura.client.GutendexClient;
import com.alura.literalura.dto.AuthorDto;
import com.alura.literalura.dto.BookDto;
import com.alura.literalura.dto.GutenDexResponse;
import com.alura.literalura.model.Author;
import com.alura.literalura.model.Book;
import com.alura.literalura.repository.AuthorRepository;
import com.alura.literalura.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GutendexClient gutendexClient;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, GutendexClient gutendexClient) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.gutendexClient = gutendexClient;
    }

    @Transactional
    public BookImportResult fetchAndSaveBookByTitle(String title) {
        if (title == null || title.isBlank()) {
            return BookImportResult.INVALID_QUERY;
        }

        final GutenDexResponse response;
        try {
            response = gutendexClient.searchBooks(title.trim());
        } catch (RestClientException exception) {
            return BookImportResult.API_ERROR;
        }

        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            return BookImportResult.NOT_FOUND;
        }

        BookDto selected = selectBestMatch(response.getResults(), title.trim());
        if (selected.getId() == null || selected.getTitle() == null || selected.getTitle().isBlank()) {
            return BookImportResult.NOT_FOUND;
        }

        if (bookRepository.existsByGutendexId(selected.getId())) {
            return BookImportResult.DUPLICATE;
        }

        Book book = new Book();
        book.setGutendexId(selected.getId());
        book.setTitle(selected.getTitle().trim());
        book.setDownloadCount(selected.getDownloadCount() != null ? selected.getDownloadCount() : 0);
        book.setLanguages(normalizeLanguages(selected.getLanguages()));
        book.setAuthors(resolveAuthors(selected.getAuthors()));

        bookRepository.save(book);
        return BookImportResult.SAVED;
    }

    @Transactional(readOnly = true)
    public List<Book> listBooks() {
        return bookRepository.findAllByOrderByTitleAsc();
    }

    @Transactional(readOnly = true)
    public List<Author> listAuthors() {
        return authorRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Author> listAuthorsAliveInYear(int year) {
        return authorRepository.findAliveInYear(year);
    }

    @Transactional(readOnly = true)
    public List<Book> listBooksByLanguage(String language) {
        if (language == null || language.isBlank()) {
            return List.of();
        }
        return bookRepository.findByLanguageIgnoreCase(language.trim().toLowerCase(Locale.ROOT));
    }

    private BookDto selectBestMatch(List<BookDto> results, String query) {
        return results.stream()
                .filter(book -> book.getTitle() != null && book.getTitle().equalsIgnoreCase(query))
                .findFirst()
                .orElse(results.get(0));
    }

    private Set<String> normalizeLanguages(List<String> languages) {
        Set<String> normalized = new LinkedHashSet<>();
        if (languages == null) {
            return normalized;
        }

        languages.stream()
                .filter(language -> language != null && !language.isBlank())
                .map(language -> language.trim().toLowerCase(Locale.ROOT))
                .forEach(normalized::add);
        return normalized;
    }

    private Set<Author> resolveAuthors(List<AuthorDto> authorDtos) {
        Set<Author> authors = new LinkedHashSet<>();
        if (authorDtos == null) {
            return authors;
        }

        for (AuthorDto authorDto : authorDtos) {
            if (authorDto == null || authorDto.getName() == null || authorDto.getName().isBlank()) {
                continue;
            }

            String name = authorDto.getName().trim();
            Author author = authorRepository.findByNameIgnoreCase(name)
                    .map(existing -> updateMissingYears(existing, authorDto))
                    .orElseGet(() -> authorRepository.save(new Author(name, authorDto.getBirthYear(), authorDto.getDeathYear())));
            authors.add(author);
        }
        return authors;
    }

    private Author updateMissingYears(Author author, AuthorDto dto) {
        boolean changed = false;
        if (author.getBirthYear() == null && dto.getBirthYear() != null) {
            author.setBirthYear(dto.getBirthYear());
            changed = true;
        }
        if (author.getDeathYear() == null && dto.getDeathYear() != null) {
            author.setDeathYear(dto.getDeathYear());
            changed = true;
        }
        return changed ? authorRepository.save(author) : author;
    }
}
