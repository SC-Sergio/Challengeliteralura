package com.alura.literalura.service;

import com.alura.literalura.dto.GutenDexResponse;
import com.alura.literalura.dto.BookDto;
import com.alura.literalura.model.Book;
import com.alura.literalura.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.alura.literalura.dto.Author;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Buscar y guardar libros desde Gutendex por título
    public void fetchAndSaveBookByTitle(String title) {
        String apiUrl = "https://gutendex.com/books/?search=" + title;

        RestTemplate restTemplate = new RestTemplate();

        try {
            GutenDexResponse response = restTemplate.getForObject(apiUrl, GutenDexResponse.class);

            if (response != null && response.getResults() != null) {
                List<Book> books = response.getResults().stream().map(bookDto -> {
                    Book book = new Book();
                    book.setTitle(bookDto.getTitle());
                    book.setAuthor(bookDto.getAuthors() != null
                            ? bookDto.getAuthors().stream()
                            .map(Author::getName)
                            .collect(Collectors.joining(", "))
                            : "Desconocido");
                    book.setLanguage(bookDto.getLanguages() != null
                            ? String.join(", ", bookDto.getLanguages())
                            : "Desconocido");
                    book.setDownloadCount(bookDto.getDownloadCount());
                    return book;
                }).collect(Collectors.toList());

                bookRepository.saveAll(books);
                System.out.println("Libros cargados exitosamente desde Gutendex.");
            } else {
                System.out.println("No se encontraron resultados para el título: " + title);
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con la API de Gutendex: " + e.getMessage());
        }
    }

    // Listar todos los libros
    public void listBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
        } else {
            books.forEach(book -> {
                System.out.println("Título: " + book.getTitle());
                System.out.println("Autor: " + book.getAuthor());
                System.out.println("Idioma: " + book.getLanguage());
                System.out.println("Número de descargas: " + book.getDownloadCount());
                System.out.println("-----------------------------");
            });
        }
    }

    // Listar todos los autores
    public void listAuthors() {
        List<String> authors = bookRepository.findAll()
                .stream()
                .map(Book::getAuthor)
                .distinct()
                .collect(Collectors.toList());

        if (authors.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            authors.forEach(author -> System.out.println("Autor: " + author));
        }
    }

    // Listar autores vivos en un año específico
    public void listAuthorsAliveInYear(int year) {
        // Nota: Necesitarás ajustar la lógica dependiendo de los datos de los autores (fechas de nacimiento y muerte).
        // Aquí asumimos que `Book` incluye información relevante del autor.
        List<String> authorsAlive = bookRepository.findAll()
                .stream()
                .filter(book -> {
                    // Lógica de ejemplo: ajustar según estructura real
                    return true; // Cambiar según las fechas
                })
                .map(Book::getAuthor)
                .distinct()
                .collect(Collectors.toList());

        if (authorsAlive.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + year);
        } else {
            authorsAlive.forEach(author -> System.out.println("Autor vivo en " + year + ": " + author));
        }
    }

    // Listar libros por idioma
    public void listBooksByLanguage(String language) {
        List<Book> books = bookRepository.findAll()
                .stream()
                .filter(book -> book.getLanguage().equalsIgnoreCase(language))
                .collect(Collectors.toList());

        if (books.isEmpty()) {
            System.out.println("No hay libros registrados en el idioma: " + language);
        } else {
            books.forEach(book -> {
                System.out.println("Título: " + book.getTitle());
                System.out.println("Autor: " + book.getAuthor());
                System.out.println("-----------------------------");
            });
        }
    }
}
