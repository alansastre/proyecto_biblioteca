

## Tareas en GitHub

## Entidades

1. Paquete model
2. Clase java 
3. Anotación @Entity
4. Campo id con anotación @Id y @GeneratedValue(strategy = GenerationType.IDENTITY)
5. Anotación @Table (opcional)
6. Anotaciones lombok opcional:
   * @Getter
   * @Setter
   * @NoArgsConstructor (constructor vacio)
   * @AllArgsConstructor (constructor con todos los campos)
   * @Builder (patrón builder para crear objetos de forma más legible)
   * @ToString (asociaciones con @ToString.Exclude para evitar bucles infinitos)

Cada persona hace una entidad.

Se puede esperar a hacer las asociaciones cuando cada uno haya creado su entidad.


## Repositorios

* paquete repository

* Desde la clase entidad, pulsar el botón de Crear Spring Data Repository para generar el repositorio. El repositorio es una interfaz que extiende JpaRepository, con el tipo de la entidad y el tipo del id (Long).

* Crear un Repository por cada Entity.

## Asociaciones @ManyToOne

* Cada alumno añadir en su entidad los campos que apunten a otras entidades en @ManyToOne. 


## Controllers en Java

* paquete controller
* Cada persona crea una clase java controller para su entity.
* Por ejemplo Movie, tendría un MovieController con la anotación @Controller
* Añadir métodos para peticiones GET:
  * @GetMapping("/movies") para mostrar el listado de películas
  * @GetMapping("/movies/{id}") para mostrar el detalle de una película
* Añadir métodos para peticiones POST:
  * @GetMapping("/movies/new") para mostrar el formulario de creación de película
  * @PostMapping("/movies") para procesar el formulario y guardar la película en la base de datos


## Vistas Thymeleaf

* Crear un directorio templates en src/main/resources
* Crear un directorio para los HTMLs de cada entidad
  * Por ejemplo: templates/movies
* Crear los siguientes archivos HTML para entidad:
  * movie-list.html: para mostrar el listado de películas
  * movie-detail.html: para mostrar el detalle de una película
  * movie-form.html: para mostrar el formulario de creación/edición de película

## A futuro:

* Más ejemplos de @Query en repositorios

* Inicialización de datos:
  * Crear una clase @Component que implemente CommandLineRunner para cargar datos de ejemplo al iniciar la aplicación.
  * Alternativa: desde SQL creando un archivo data.sql en src/main/resources con sentencias INSERT para cargar datos.
* Seguridad: autenticación y autorización para que solo los usuarios registrados puedan hacer reservas o escribir reseñas. Iniciar sesión y registro.
* Bootstrap CSS para mejorar el diseño de las vistas.
* Font Awesome para añadir iconos (estrellas para la puntuación, por ejemplo).

nice to have:

* Capa Service: entre el controlador y el repositorio para manejar la lógica de negocio. Por ejemplo, calcular el precio total de una reserva a partir del precio por noche y el número de noches, o validar que una película no se pueda eliminar si tiene sesiones programadas.
* Filtros en listados (para mostrar solo las películas de un género, o solo las casas en una ciudad concreta)
* Paginación y ordenación en los listados para manejar grandes cantidades de datos.
* Más asociaciones.
* Validaciones con spring validation (por ejemplo, que el precio no sea negativo, o que el email tenga formato correcto).
* Test de integración Spring.
* Base de datos externa (MySQL, PostgreSQL) en lugar de h2.
  * Instalación nativa o vía Docker
* API REST para que otras aplicaciones puedan consumir los datos de la tienda (por ejemplo, una app móvil). Esto implicaría crear controladores con @RestController y endpoints que devuelvan JSON en lugar de HTML.

* Git y GitHub: ramas y Pull Requests.