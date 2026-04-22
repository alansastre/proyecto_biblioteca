# Proyecto: Cine

Imagina que te encargan construir la aplicación que usa un cine para gestionar su día a día: qué películas tiene en cartelera, en qué salas se proyectan, a qué hora es cada sesión y quién ha comprado cada entrada. Es lo que vamos a construir entre todo el grupo.

La aplicación tiene **5 entidades principales**. Cada persona del grupo se encarga de una (si sois 4, dejad fuera la que menos os interese; si sois 5, una por persona). A continuación se explica qué representa cada una, por qué la necesitamos y qué información guarda.



## La entidad User

`User` ya existe en el proyecto base (no hay que crearla). Representa a cualquier persona que usa la aplicación:

- Un **usuario normal** (rol USER) es un cliente que compra entradas desde la web.
- Un **administrador** (rol ADMIN) es un empleado del cine que gestiona la cartelera, crea sesiones, configura salas, etc.

Cuando una de vuestras entidades necesite saber "quién hizo esto" (por ejemplo, quién compró una entrada), se vincula con `User` mediante una asociación `@ManyToOne`. No tenéis que programar nada en `User`: solo la usáis como referencia.

---

## Fase 1 — Entidades

Cada alumno crea **una sola entidad** en `model/`. Como cada alumno toca archivos distintos, no hay conflictos de Git.

### Movie

Una **película** es el contenido que el cine proyecta. Sin películas no hay cine. Piensa en la cartelera que ves al entrar: cada fila es una Movie.

Ejemplos reales: "Interstellar", "Parásitos", "Coco", "Gladiator II".

```
- id: Long               → identificador único, lo genera la base de datos
- title: String           → título de la película ("Interstellar")
- director: String        → quién la ha dirigido ("Christopher Nolan")
- durationMinutes: Integer → cuánto dura en minutos (169)
- genre: String           → género ("ciencia ficción", "drama", "animación"...)
```

### Room

Una **sala** es el espacio físico del cine donde se proyecta una película. Un cine grande puede tener 10-15 salas, cada una con un nombre, una capacidad y un tipo de pantalla.

Ejemplos reales: "Sala 1 (estándar, 200 butacas)", "Sala IMAX (400 butacas)", "Sala VIP (50 butacas)".

```
- id: Long               → identificador único
- name: String            → nombre visible de la sala ("Sala 1", "IMAX", "VIP")
- capacity: Integer       → cuántas butacas tiene (200)
- screenType: String      → tipo de pantalla ("2D", "3D", "IMAX")
```

### Session

Una **sesión** es una proyección concreta: una película determinada, en una sala determinada, a una hora determinada, con un precio. Es lo que el cliente ve cuando busca "a qué hora puedo ver Interstellar hoy". Un cine puede tener muchas sesiones al día (diferentes películas, diferentes salas, diferentes horarios).

Ejemplos reales: "Interstellar, Sala IMAX, hoy a las 18:30, 12,50 €, versión original".

```
- id: Long               → identificador único
- startTime: LocalDateTime → fecha y hora de inicio (2026-04-15T18:30)
- price: Double           → precio de la entrada para esta sesión (12.50)
- language: String        → idioma ("VO", "doblada", "VOSE")
```

### Ticket

Una **entrada** es lo que compra un cliente para asistir a una sesión. Cada entrada tiene un asiento asignado (fila y número) y un precio. Cuando el cliente paga, se genera un Ticket en la base de datos.

Ejemplos reales: "Fila D, Butaca 12, Sesión de Interstellar a las 18:30, 12,50 €".

```
- id: Long               → identificador único
- seatRow: String         → fila del asiento ("A", "B", "C"...)
- seatNumber: Integer     → número de butaca dentro de la fila (12)
- price: Double           → lo que ha pagado el cliente (12.50)
```

### Review

Una **reseña** es la opinión que un cliente deja sobre una película después de verla. Las reseñas ayudan a otros clientes a decidir qué película ver y permiten al cine saber qué películas gustan más. Piensa en las estrellas y comentarios que ves en Google o en Filmaffinity debajo de cada película.

Ejemplos reales: "★★★★★ — Una obra maestra visual. La banda sonora de Hans Zimmer es increíble."

```
- id: Long               → identificador único
- rating: Integer         → puntuación de 1 a 5 (5)
- comment: String         → texto de la opinión ("Una obra maestra visual...")
- createdAt: LocalDateTime → cuándo se escribió la reseña (2026-04-16T20:00)
```

---

## Fase 2 — Repositorios y datos

Una vez creada la entidad, cada alumno crea dos cosas más:

**El repositorio** (`repository/`) permite guardar y consultar datos en la base de datos sin escribir SQL. Spring Data JPA genera las operaciones automáticamente.

```java
public interface MovieRepository extends JpaRepository<Movie, Long> {
}
```

**El DataInitializer** (`config/`) carga datos de ejemplo automáticamente al arrancar la aplicación para que la base de datos no esté vacía. Así al abrir h2-console ya se ven filas con datos reales.

```java
@Component
@Profile("!test")
public class MovieDataInitializer implements CommandLineRunner {
    private final MovieRepository repository;

    public MovieDataInitializer(MovieRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;
        repository.save(new Movie("Interstellar", "C. Nolan", 169, "ciencia ficción"));
        repository.save(new Movie("Parásitos", "Bong Joon-ho", 132, "drama"));
        repository.save(new Movie("Coco", "Lee Unkrich", 105, "animación"));
    }
}
```

- `@Profile("!test")` hace que estos datos **no se carguen cuando se ejecutan los tests**, para no interferir con las aserciones.
- `if (repository.count() > 0) return;` evita duplicar datos si reinicias la aplicación.

Verificar en h2-console (`localhost:8080/h2-console`) que cada tabla tiene datos: `SELECT * FROM movies;`

---

## Fase 3 — Asociaciones @ManyToOne

Hasta ahora cada entidad vive aislada. En la realidad están conectadas: una sesión proyecta una película concreta, en una sala concreta, y un ticket se vende para una sesión concreta a un cliente concreto. Estas conexiones se representan con `@ManyToOne`.

- **Session → Movie**: cada sesión proyecta **una** película (pero una película puede tener muchas sesiones a lo largo de la semana)
- **Session → Room**: cada sesión ocurre en **una** sala (pero una sala alberga muchas sesiones a lo largo del día)
- **Ticket → Session**: cada entrada es para **una** sesión concreta (pero una sesión puede tener muchas entradas vendidas)
- **Ticket → User**: cada entrada la compra **un** cliente (pero un cliente puede comprar muchas entradas)
- **Review → Movie**: cada reseña es sobre **una** película (pero una película puede tener muchas reseñas)
- **Review → User**: cada reseña la escribe **un** cliente (pero un cliente puede escribir muchas reseñas)

```
Movie ←── Session ──→ Room
  ↑         ↑
  │         │
Review    Ticket ──→ User
  │                   ↑
  └─────────────────┘
```

Después de añadir las asociaciones, hay que actualizar los DataInitializers para que creen datos relacionados (por ejemplo, crear primero las Movies y Rooms, luego las Sessions que apunten a ellas).

---

## Fase 4 — Controladores y HTML

Cada alumno crea un **controlador** y una **vista Thymeleaf** para que su entidad se pueda ver en el navegador, no solo en h2-console.

- `GET /movies` → listado de películas con título, director, duración y género
- `GET /rooms` → listado de salas con nombre, capacidad y tipo de pantalla
- `GET /sessions` → listado de sesiones mostrando qué película, en qué sala y a qué hora
- `GET /tickets` → listado de entradas mostrando sesión, asiento y cliente
- `GET /reviews` → reseñas mostrando película, puntuación (estrellas), comentario y cliente

Más adelante: formularios de creación (`GET /movies/new`, `POST /movies`), detalle (`GET /movies/{id}`), edición y borrado.

---

## Fase 5 — Ampliar el modelo

Cuando todo lo anterior funcione, se puede ampliar el proyecto con más campos, más entidades y más relaciones:

### Campos adicionales
- `Movie`: description, releaseYear, imageUrl, rating
- `Room`: hasDolby (Boolean), hasVip (Boolean)
- `Session`: hasSubtitles (Boolean), endTime (calculado a partir de startTime + durationMinutes de la película)
- `Ticket`: purchaseDate (LocalDateTime, cuándo se compró la entrada)

### Entidades nuevas
- **Snack**: productos de la confitería del cine (palomitas, refrescos, nachos). Campos: name, price, size.
- **Director**: si queremos que el director sea una entidad propia en vez de un simple texto en Movie. Campos: firstName, lastName, nationality.
- **Promotion**: descuentos aplicables a sesiones (día del espectador, tarifa estudiante). Campos: name, discountPercent, validFrom, validUntil.

### Asociaciones nuevas
- `Movie → Director` (`@ManyToOne`): cada película tiene un director
- `Session → Promotion` (`@ManyToOne`): cada sesión puede tener una promoción activa

### Queries derivadas
```java
List<Movie> findByGenre(String genre);
List<Session> findByStartTimeAfter(LocalDateTime dateTime);
List<Ticket> findByUserId(Long userId);
List<Room> findByCapacityGreaterThan(Integer capacity);
List<Review> findByMovieId(Long movieId);
List<Review> findByRatingGreaterThanEqual(Integer rating);
```



# Ejemplo

* Movie (@Entity)
* MovieRepository (vacío, no necesita consultas personalizadas, se pueden agregar más adelante)
* Datos demo: o se crean en el main o se crea un MovieDataInitializer que cargue 3-4 películas al arrancar la aplicación
* MovieController. Un método para localhost:8080/movies findAll()
* movies.html: tabla que muestra título, director, duración y género de cada película