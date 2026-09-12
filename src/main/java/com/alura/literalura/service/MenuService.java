package com.alura.literalura.service;

import com.alura.literalura.model.Author;
import com.alura.literalura.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final BookService bookService;

    public MenuService(BookService bookService) {
        this.bookService = bookService;
    }

    public void displayMenu() {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            System.out.println("LiterAlura - catálogo de libros con Gutendex");

            while (running) {
                printMenu();
                if (!scanner.hasNextInt()) {
                    System.out.println("Por favor, ingrese un número válido.");
                    scanner.nextLine();
                    continue;
                }

                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1 -> searchBook(scanner);
                    case 2 -> printBooks(bookService.listBooks());
                    case 3 -> printAuthors(bookService.listAuthors());
                    case 4 -> printAuthorsAliveInYear(scanner);
                    case 5 -> printBooksByLanguage(scanner);
                    case 0 -> {
                        System.out.println("Saliendo...");
                        running = false;
                    }
                    default -> System.out.println("Opción no válida. Intente nuevamente.");
                }
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("----- Menú -----");
        System.out.println("1. Buscar y registrar libro por título");
        System.out.println("2. Listar libros registrados");
        System.out.println("3. Listar autores registrados");
        System.out.println("4. Listar autores vivos en un año");
        System.out.println("5. Listar libros por idioma");
        System.out.println("0. Salir");
        System.out.print("Elija una opción: ");
    }

    private void searchBook(Scanner scanner) {
        System.out.print("Ingrese el título del libro: ");
        BookImportResult result = bookService.fetchAndSaveBookByTitle(scanner.nextLine());

        switch (result) {
            case SAVED -> System.out.println("Libro registrado correctamente desde Gutendex.");
            case NOT_FOUND -> System.out.println("No se encontraron libros para ese título.");
            case DUPLICATE -> System.out.println("Ese libro ya está registrado.");
            case INVALID_QUERY -> System.out.println("El título no puede estar vacío.");
            case API_ERROR -> System.out.println("No fue posible consultar Gutendex. Intente nuevamente.");
        }
    }

    private void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }

        books.forEach(book -> {
            String authors = book.getAuthors().stream().map(Author::getName).collect(Collectors.joining(", "));
            String languages = String.join(", ", book.getLanguages());
            System.out.println("Título: " + book.getTitle());
            System.out.println("Autor(es): " + (authors.isBlank() ? "Desconocido" : authors));
            System.out.println("Idioma(s): " + (languages.isBlank() ? "Desconocido" : languages));
            System.out.println("Descargas: " + book.getDownloadCount());
            System.out.println("Gutendex ID: " + book.getGutendexId());
            System.out.println("-----------------------------");
        });
    }

    private void printAuthors(List<Author> authors) {
        if (authors.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }
        authors.forEach(author -> {
            System.out.println("Autor: " + author.getName());
            System.out.println("Nacimiento: " + formatYear(author.getBirthYear()));
            System.out.println("Fallecimiento: " + formatYear(author.getDeathYear()));
            System.out.println("-----------------------------");
        });
    }

    private void printAuthorsAliveInYear(Scanner scanner) {
        System.out.print("Ingrese el año: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Año inválido.");
            scanner.nextLine();
            return;
        }

        int year = scanner.nextInt();
        scanner.nextLine();
        List<Author> authors = bookService.listAuthorsAliveInYear(year);

        if (authors.isEmpty()) {
            System.out.println("No se encontraron autores con datos suficientes que estuvieran vivos en " + year + ".");
            return;
        }

        authors.forEach(author -> System.out.println(
                author.getName() + " (" + formatYear(author.getBirthYear()) + " - " + formatYear(author.getDeathYear()) + ")"
        ));
    }

    private void printBooksByLanguage(Scanner scanner) {
        System.out.print("Ingrese el código de idioma (por ejemplo: es, en, fr, pt): ");
        List<Book> books = bookService.listBooksByLanguage(scanner.nextLine());
        if (books.isEmpty()) {
            System.out.println("No hay libros registrados para ese idioma.");
            return;
        }
        printBooks(books);
    }

    private String formatYear(Integer year) {
        return year == null ? "desconocido" : year.toString();
    }
}
