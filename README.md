# LiterAlura

Aplicación de consola desarrollada con **Java 17 y Spring Boot** para consultar libros en la API pública de **Gutendex**, almacenarlos en **PostgreSQL** y explorar el catálogo por libro, autor, año e idioma.

Este repositorio corresponde al Challenge LiterAlura de Alura Latam y fue refactorizado para que la implementación documentada coincida con el comportamiento real del código.

## Funcionalidades

- buscar libros por título en Gutendex;
- registrar el mejor resultado encontrado;
- evitar duplicados mediante el ID de Project Gutenberg;
- persistir libros, autores, años de nacimiento/fallecimiento e idiomas;
- listar libros registrados;
- listar autores registrados;
- listar autores que estaban vivos en un año determinado;
- filtrar libros registrados por código de idioma;
- manejar búsquedas vacías, resultados inexistentes y errores de la API.

## Arquitectura

```text
Console Menu
    |
    v
MenuService
    |
    v
BookService
   / \
  /   \
 v     v
GutendexClient       Spring Data JPA
    |                /            \
    v               v              v
Gutendex API   BookRepository  AuthorRepository
                         \       /
                          v     v
                         PostgreSQL
```

La aplicación se ejecuta únicamente como **CLI**. No expone endpoints REST propios.

## Integración con Gutendex

Gutendex entrega metadata de Project Gutenberg. La aplicación utiliza, entre otros, `id`, `title`, `authors[].name`, `authors[].birth_year`, `authors[].death_year`, `languages[]` y `download_count`.

Los campos `snake_case` se mapean de forma explícita a DTO Java. Si Gutendex devuelve varios resultados se prioriza una coincidencia exacta de título; de lo contrario se usa el primer resultado ordenado por la API.

Documentación: https://gutendex.com/

## Modelo de datos

### Book

- `id`: identificador interno PostgreSQL;
- `gutendexId`: ID de Project Gutenberg usado para evitar duplicados;
- `title`;
- `downloadCount`;
- `languages`: colección de códigos de idioma;
- `authors`: relación con autores persistidos.

### Author

- `id`;
- `name`;
- `birthYear`;
- `deathYear`.

Los autores se reutilizan por nombre para evitar crear una fila nueva cada vez que aparece el mismo autor en otro libro.

## Stack

- Java 17
- Spring Boot 3.4.0
- Spring Data JPA
- Hibernate
- PostgreSQL
- Spring `RestClient`
- Jackson
- Maven / Maven Wrapper
- JUnit 5
- Mockito

> Spring Boot se mantiene deliberadamente en `3.4.0` durante este polish. Una actualización mayor se deja para una iteración independiente.

## Configuración

Variables obligatorias:

```text
DB_USER
DB_PASSWORD
```

Variable opcional:

```text
DB_URL
```

Si `DB_URL` no está definida se usa `jdbc:postgresql://localhost:5432/literalura_db`. También puede habilitarse el SQL de Hibernate con `SHOW_SQL=true`.

### PowerShell

```powershell
$env:DB_USER="postgres"
$env:DB_PASSWORD="tu_password"
$env:DB_URL="jdbc:postgresql://localhost:5432/literalura_db"
```

### Linux / macOS

```bash
export DB_USER="postgres"
export DB_PASSWORD="tu_password"
export DB_URL="jdbc:postgresql://localhost:5432/literalura_db"
```

No se almacenan credenciales reales dentro del repositorio.

## Base de datos

```sql
CREATE DATABASE literalura_db;
```

El proyecto utiliza `spring.jpa.hibernate.ddl-auto=update` por tratarse de una aplicación educativa. En producción sería preferible administrar el esquema con migraciones versionadas.

> Si ya utilizaste una versión anterior del proyecto, se recomienda probar este refactor inicialmente sobre una base `literalura_db` limpia, ya que el modelo de persistencia ahora representa autores e idiomas de forma estructurada.

## Ejecución

Requisitos: JDK 17, PostgreSQL y conexión a Internet para consultar Gutendex.

El Maven Wrapper quedó configurado dentro de `.mvn/wrapper`.

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

## Menú

```text
1. Buscar y registrar libro por título
2. Listar libros registrados
3. Listar autores registrados
4. Listar autores vivos en un año
5. Listar libros por idioma
0. Salir
```

Para idioma se utilizan códigos como `es`, `en`, `fr` y `pt`.

## Autores vivos en un año

La consulta considera a un autor vivo si `birthYear <= año` y `deathYear` es nulo o `deathYear >= año`. Los autores cuyo nacimiento es desconocido se excluyen porque no hay datos suficientes para determinar si estaban vivos.

## Prevención de duplicados

Cada libro conserva el `id` de Project Gutenberg como `gutendexId`. Antes de guardar se consulta `existsByGutendexId(...)`; si ya existe, no se inserta nuevamente.

## Tests

La suite cubre los puntos más sensibles del refactor:

- mapeo JSON de `download_count`, `birth_year` y `death_year`;
- búsquedas Gutendex sin resultados;
- prevención de libros duplicados;
- persistencia del libro con autores e idiomas correctamente mapeados.

```bash
./mvnw test
```

En Windows:

```powershell
.\mvnw.cmd test
```

## Integración continua

El repositorio incluye un workflow de **GitHub Actions** que valida cada Pull Request hacia `main` y cada push a `main` con **Java 17 (Temurin)** y el **Maven Wrapper**. El job ejecuta `mvn verify`, por lo que compila el proyecto y ejecuta la suite de tests automáticamente.

Las actions de terceros utilizadas por el workflow están fijadas a SHAs completos y el token del workflow se limita a permisos de lectura sobre el contenido del repositorio.

## Estructura principal

```text
src/main/java/com/alura/literalura/
|-- client/GutendexClient.java
|-- config/HttpClientConfig.java
|-- dto/
|   |-- AuthorDto.java
|   |-- BookDto.java
|   `-- GutenDexResponse.java
|-- model/
|   |-- Author.java
|   `-- Book.java
|-- repository/
|   |-- AuthorRepository.java
|   `-- BookRepository.java
|-- service/
|   |-- BookImportResult.java
|   |-- BookService.java
|   `-- MenuService.java
`-- LiterAluraApplication.java
```

Los artefactos generados por Maven (`target/`) están excluidos mediante `.gitignore`.

## Decisiones de diseño

- **CLI único:** se eliminó el controlador REST que duplicaba caminos de escritura.
- **Constructor injection:** las dependencias son explícitas e inmutables.
- **Cliente Gutendex separado:** el acceso HTTP no vive dentro de la lógica de negocio.
- **Autores normalizados:** nacimiento y fallecimiento se almacenan como datos estructurados.
- **Idiomas como colección:** un libro multilingüe puede encontrarse por cualquiera de sus códigos.
- **Configuración externa:** PostgreSQL se configura mediante variables de entorno.

## Limitaciones y posibles mejoras

- migrar Spring Boot en un PR separado;
- reemplazar `ddl-auto=update` por Flyway o Liquibase;
- agregar tests de integración con PostgreSQL/Testcontainers;
- manejar paginación de Gutendex;
- mejorar la selección de coincidencias por título;
- añadir logging estructurado.

## Autor

Desarrollado por **Sergio Carey** como parte del programa de formación de **Alura Latam**.
