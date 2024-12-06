# Challengeliteralura
Challenge LiterAlura Funcional con la Api Gundex 

La aplicación LiterAlura es un catálogo de libros interactivo diseñado para ejecutarse en consola. Permite a los usuarios buscar y registrar libros desde la API pública de Gutendex y gestionar información sobre libros y autores en una base de datos local. Está desarrollada con Java, Spring Boot y PostgreSQL, enfocándose en un entorno backend. A continuación, se describe su funcionamiento:

Características principales:
Buscar libro por título:

El usuario ingresa el título de un libro.
La aplicación consulta la API de Gutendex y guarda los libros encontrados en la base de datos.
Si no se encuentran resultados, informa al usuario.
Listar libros registrados:

Muestra todos los libros almacenados en la base de datos.
Incluye información detallada como título, autor, idioma y número de descargas.
Listar autores registrados:

Muestra una lista de autores cuyos libros están registrados en la base de datos.
Listar autores vivos en un año:

El usuario ingresa un año.
La aplicación verifica qué autores registrados estaban vivos en ese año y los muestra.
Listar libros por idioma:

El usuario selecciona un idioma (por ejemplo, ES, EN, FR, PT).
Se muestran los libros registrados que coinciden con el idioma seleccionado.
Salir:

Finaliza la ejecución de la aplicación.
Flujo de la aplicación:
Al iniciar, se muestra un menú interactivo donde el usuario puede elegir entre las distintas funcionalidades.
Cada opción está diseñada para manejar errores, como títulos inexistentes o entradas no válidas.
Los datos se manejan mediante consultas SQL a la base de datos configurada con Hibernate y Spring Data JPA.
Tecnologías utilizadas:
Java 17 y Spring Boot 3.4.0: Para la estructura del backend y la gestión de las dependencias.
PostgreSQL: Base de datos relacional para almacenar libros y autores.
Gutendex API: Fuente de datos externa para obtener información sobre libros y autores.
Hibernate: Framework de mapeo objeto-relacional para interactuar con la base de datos.
Desarrollador
Esta aplicación fue desarrollada por Sergio Carey
