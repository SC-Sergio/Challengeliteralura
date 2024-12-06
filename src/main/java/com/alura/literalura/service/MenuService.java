package com.alura.literalura.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Scanner;

@Service
public class MenuService {

    @Autowired
    private BookService bookService;

    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        // Mensaje al inicio de la aplicación
        System.out.println("Aplicación desarrollada por Sergio Carey");

        try {
            while (running) {
                System.out.println("----- Menú -----");
                System.out.println("1. Buscar libro por título");
                System.out.println("2. Listar libros registrados");
                System.out.println("3. Listar autores registrados");
                System.out.println("4. Listar autores vivos en un año");
                System.out.println("5. Listar libros por idioma");
                System.out.println("0. Salir");
                System.out.print("Elija una opción: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Por favor, ingrese un número válido.");
                    scanner.nextLine(); // Limpiar el buffer
                    continue;
                }

                int option = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea

                switch (option) {
                    case 1:
                        System.out.print("Ingrese el título del libro: ");
                        String title = scanner.nextLine();
                        bookService.fetchAndSaveBookByTitle(title);
                        break;
                    case 2:
                        bookService.listBooks();
                        break;
                    case 3:
                        bookService.listAuthors();
                        break;
                    case 4:
                        System.out.print("Ingrese el año: ");
                        if (!scanner.hasNextInt()) {
                            System.out.println("Año inválido. Intente nuevamente.");
                            scanner.nextLine();
                            break;
                        }
                        int year = scanner.nextInt();
                        scanner.nextLine(); // Consumir el salto de línea
                        bookService.listAuthorsAliveInYear(year);
                        break;
                    case 5:
                        System.out.print("Ingrese el idioma (ES, EN, FR, PT): ");
                        String language = scanner.nextLine();
                        bookService.listBooksByLanguage(language);
                        break;
                    case 0:
                        System.out.println("Saliendo...");
                        running = false;
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Ocurrió un error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        // Mensaje al final de la aplicación
        System.out.println("Aplicación desarrollada por Sergio Carey");
    }
}
