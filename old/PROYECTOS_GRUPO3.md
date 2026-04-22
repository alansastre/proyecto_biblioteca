# Grupo 3: Sistema de Cartelera de Cine

## Qué vas a construir

Tu grupo va a crear una **aplicación web para un cine**: los usuarios pueden consultar la cartelera, ver las sesiónes disponibles de cada película, comprar entradas y consultar sus compras. El administrador gestióna películas, salas, sesiónes y ve informes de ventas.

Es una aplicación sencilla con operaciones básicas (crear, ver, editar, eliminar) más la lógica de compra de entradas y control de aforo, que te servirá para aprender los patrones fundamentales de Spring Boot paso a paso.

**Alumnos de mañana (Spring Boot):** vosotros construís la aplicación. Crearéis las entidades de base de datos, los servicios con la lógica de disponibilidad y compra de entradas, los controladores web y las páginas HTML.

**Alumnos de tarde (Testing):** vosotros probáis la aplicación. Crearéis tests unitarios para los servicios (especialmente para la comprobación de disponibilidad), tests de integración para los controladores, y tests E2E con Selenium.

Al final de las 8-10 semanas tendréis un proyecto completo y bien testado que podréis enseñar en entrevistas de trabajo.


## Las 4 entidades de tu proyecto

### CinemaMovie (las películas)

Representa cada película que está en cartelera.

```
Campos:
- id: Long (clave primaria, se genera automáticamente)
- title: String (nombre de la película, por ejemplo "Oppenheimer")
- genre: String (género: "accion", "drama", "comedia", etc.)
- director: String (nombre del director)
- duration: Integer (duración en minutos)
- synopsis: String (sinopsis o resumen de la película)
- posterUrl: String (URL de la imagen del poster)
- releaseDate: LocalDate (fecha de estreno)

Validaciones:
- @NotBlank en title y genre
- @Min(60) en duration (una película dura al menos 60 minutos)
```

### CinemaHall (las salas)

Representa cada sala del cine.

```
Campos:
- id: Long (clave primaria)
- hallNumber: Integer (número de sala: 1, 2, 3...)
- capacity: Integer (aforo total de la sala)
- type: String (tipo de sala: "2D", "3D" o "IMAX")

Validaciones:
- @NotNull en hallNumber
- @Min(50) en capacity (la sala tiene al menos 50 butacas)
```

### CinemaSession (las sesiónes)

Representa una proyección concreta: una película, en una sala, un día y hora determinados.

```
Campos:
- id: Long (clave primaria)
- movieId: Long (que película se proyecta)
- hallId: Long (en que sala)
- sessionDate: LocalDate (que dia)
- sessionTime: LocalTime (a que hora)
- pricePerTicket: BigDecimal (precio de cada entrada)
- availableSeats: Integer (butacas libres en este momento)

Validaciones:
- @NotNull en movieId, hallId, sessionDate, sessionTime
- @Future en sessionDate (la sesión debe ser en el futuro)
- @Min(0.01) en pricePerTicket (el precio debe ser positivo)
```

### CinemaBooking (las entradas compradas)

Representa la compra de entradas de un usuario para una sesión.

```
Campos:
- id: Long (clave primaria)
- sessionId: Long (para qué sesión)
- userId: Long (quién compra)
- bookingDate: LocalDateTime (cuando se hizo la compra)
- numberOfTickets: Integer (cuántas entradas)
- totalPrice: BigDecimal (precio total = precio por entrada x cantidad)
- status: String (estado: "confirmed" o "cancelled")
- bookingCode: String (código único de la entrada, para identificarla)

Validaciones:
- @NotNull en sessionId y userId
- @Min(1) en numberOfTickets (al menos 1 entrada)
```

### Cómo se relacionan las entidades

```
CinemaMovie ---1:N---> CinemaSession ---1:N---> CinemaBooking
                           |
CinemaHall ---1:N---------+
(una sala alberga muchas sesiónes)

User ---1:N---> CinemaBooking
(un usuario puede tener muchas compras)
```

Una película puede tener muchas sesiónes (diferentes días y horas). Una sala puede albergar muchas sesiónes. Un usuario puede comprar entradas para muchas sesiónes.


## Pantallas que hay que crear

### Pantallas para el cliente (rol USER)

**1. Cartelera (GET /cine/movies)**

Muestra todas las películas en cartel con su poster, título y género. El usuario puede filtrar por género. Al hacer clic en una película, va a su detalle.

**2. Detalle de película (GET /cine/movies/{id})**

Muestra la sinopsis, director, duración y las próximas sesiónes disponibles (tabla con sala, día, hora, precio y butacas libres). Desde aquí el usuario puede ir a comprar entradas para una sesión.

**3. Comprar entradas (GET/POST /cine/sessions/{id}/buy)**

Muestra los datos de la sesión (película, sala, hora, precio por entrada) y un campo para elegir cuántas entradas quiere. Al confirmar, se crea la compra y se descuentan las butacas disponibles. Si no hay suficientes butacas, se muestra un mensaje de error.

**4. Mis entradas (GET /cine/mis-entradas)**

Lista las entradas compradas por el usuario: película, día, hora, código de entrada y un botón para cancelar (si la sesión aún no ha empezado).

### Pantallas para el administrador (rol ADMIN)

**5. Gestión de películas (GET/POST /admin/cine/movies)**

CRUD de películas: crear, editar, eliminar. Formulario con título, género, director, duración, sinopsis.

**6. Gestión de salas (GET/POST /admin/cine/halls)**

CRUD de salas: crear, editar. Cambiar capacidad y tipo de sala.

**7. Gestión de sesiónes (GET/POST /admin/cine/sessions)**

CRUD de sesiónes: asignar película a sala, definir día, hora y precio. Ver las butacas disponibles en tiempo real.

**8. Informes de ventas (GET /admin/cine/reports)**

Muestra estadísticas: entradas vendidas por película, ingresos totales, ocupación media por sala.


## Plan semana por semana

### Semanas 1-2: Crear las entidades (la base de datos)

**Qué hacen los de mañana (Spring):**

- Crear la clase `CinemaMovie` dentro de `model/` con anotaciones `@Entity`, `@Table`, Lombok y validaciones.
- Crear `CinemaHall` con campos de número de sala, capacidad y tipo.
- Crear `CinemaSession` con relaciones `@ManyToOne` hacia CinemaMovie y CinemaHall.
- Crear `CinemaBooking` con relación hacia CinemaSession y User.
- Verificar que compila: `mvn clean compile`.

**Qué hacen los de tarde (Testing):**

- Tests unitarios básicos para las entidades.
- Familiarizarse con los tests existentes del proyecto base.

**Ejemplo de commit:**
```
feat(cine): crear entidades CinemaMovie, CinemaHall, CinemaSession, CinemaBooking

- Crear CinemaMovie con campos title, genre, director, duration, synopsis
- Crear CinemaHall con campos hallNumber, capacity, type
- Crear CinemaSession con relaciones ManyToOne a Movie y Hall
- Crear CinemaBooking con bookingCode y relación a Session y User
```

---

### Semanas 3-4: Repositorios y servicios (acceso a datos y lógica)

Esta es la parte más interesante del proyecto: la lógica de comprobación de disponibilidad y compra de entradas.

**Qué hacen los de mañana (Spring):**

- Crear `CinemaMovieRepository` con `findByGenre(String genre)` para filtrar por género.
- Crear `CinemaSessionRepository` con queries:
  - `findByMovieId(Long movieId)` para ver sesiónes de una película
  - `findBySessionDateGreaterThanEqual(LocalDate date)` para ver sesiónes futuras
- Crear `CinemaBookingRepository` con `findByUserId(Long userId)` para las entradas de un usuario.
- Crear `CinemaSessionService` con la lógica principal:
  - `checkAvailability(Long sessionId, Integer numberOfTickets)` -- comprueba si hay suficientes butacas libres. Devuelve true o false.
  - `bookSeats(CinemaSession session, User user, Integer numberOfTickets)` -- crea la compra, calcula el precio total, descuenta butacas disponibles, genera un código único.
  - `findUpcomingSessions(Long movieId)` -- devuelve las sesiónes futuras de una película.

**Qué hacen los de tarde (Testing):**

- Test con `@DataJpaTest` para `CinemaMovieRepository`: verificar que findByGenre devuelve solo películas de ese género.
- Test unitario de `CinemaSessionService`: verificar que checkAvailability devuelve false si se piden más entradas de las disponibles, y que bookSeats descuenta correctamente las butacas.

**Código de referencia (mañana):**

```java
// src/main/java/com/certidevs/service/CinemaSessionService.java
@Service
@RequiredArgsConstructor
@Transactional
public class CinemaSessionService {
    private final CinemaSessionRepository sessionRepository;
    private final CinemaBookingRepository bookingRepository;

    public boolean checkAvailability(Long sessionId, Integer numberOfTickets) {
        CinemaSession session = sessionRepository.findById(sessionId)
            .orElseThrow();
        return session.getAvailableSeats() >= numberOfTickets;
    }

    public CinemaBooking bookSeats(CinemaSession session, User user, Integer numberOfTickets) {
        if (!checkAvailability(session.getId(), numberOfTickets)) {
            throw new IllegalStateException("No hay suficientes butacas disponibles");
        }

        CinemaBooking booking = new CinemaBooking();
        booking.setSession(session);
        booking.setUser(user);
        booking.setNumberOfTickets(numberOfTickets);
        booking.setTotalPrice(session.getPricePerTicket()
            .multiply(BigDecimal.valueOf(numberOfTickets)));
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("confirmed");
        booking.setBookingCode(UUID.randomUUID().toString());

        session.setAvailableSeats(session.getAvailableSeats() - numberOfTickets);
        sessionRepository.save(session);

        return bookingRepository.save(booking);
    }

    public List<CinemaSession> findUpcomingSessions(Long movieId) {
        return sessionRepository.findByMovieIdAndSessionDateGreaterThanEqual(
            movieId, LocalDate.now());
    }
}
```

**Ejemplo de commit:**
```
feat(cine): crear repositorios y servicio de sesiónes con lógica de compra

- Crear CinemaMovieRepository con findByGenre
- Crear CinemaSessionService con checkAvailability y bookSeats
- La lógica de bookSeats descuenta butacas y genera código único
```

---

### Semanas 5-6: Controladores y vistas (la parte web)

**Qué hacen los de mañana (Spring):**

- Crear `CinemaMovieController` con rutas para cartelera (lista de películas) y detalle de película.
- Crear `CinemaSessionController` con la ruta de compra de entradas.
- Crear `CinemaBookingController` con la ruta de mis entradas.
- Crear las vistas Thymeleaf:
  - `templates/cine/cartelera.html` -- rejilla de películas con poster y filtro por género
  - `templates/cine/detail.html` -- detalle con sesiónes disponibles
  - `templates/cine/buy.html` -- formulario de compra
  - `templates/cine/my-bookings.html` -- mis entradas con código

**Qué hacen los de tarde (Testing):**

- Tests con MockMvc para los controladores: verificar que la cartelera devuelve 200, que el detalle carga correctamente, que la compra funciona.

**Vista Thymeleaf de referencia (cine/cartelera.html):**

```html
<div class="container">
    <h1>Cartelera</h1>

    <form method="get">
        <select name="genre">
            <option value="">Todos los géneros</option>
            <option th:each="g : ${genres}" th:value="${g}" th:text="${g}"></option>
        </select>
        <button type="submit">Filtrar</button>
    </form>

    <div class="row">
        <div class="col-md-3" th:each="movie : ${movies}">
            <div class="card">
                <img th:src="${movie.posterUrl}" class="card-img-top" alt="Poster">
                <div class="card-body">
                    <h5 class="card-title" th:text="${movie.title}"></h5>
                    <p class="card-text" th:text="${movie.genre}"></p>
                    <a th:href="@{/cine/movies/{id}(id=${movie.id})}"
                       class="btn btn-primary">Ver detalles</a>
                </div>
            </div>
        </div>
    </div>
</div>
```

**Ejemplo de commit:**
```
feat(cine): crear controladores y vistas de cartelera y compra

- Crear CinemaMovieController con cartelera y detalle
- Crear CinemaSessionController con flujo de compra de entradas
- Crear vistas cartelera, detail, buy y my-bookings
```

---

### Semanas 7-8: Seguridad (proteger la aplicación)

**Qué hacen los de mañana (Spring):**

- Modificar `SecurityConfig`:
  - `/cine/movies` y `/cine/movies/{id}` -- acceso público (cualquiera puede ver la cartelera)
  - `/cine/sessions/*/buy` y `/cine/mis-entradas` -- solo USER
  - `/admin/cine/**` -- solo ADMIN
- Mostrar solo sesiónes futuras (no sesiónes ya pasadas)
- Mostrar el botón "Comprar" solo si el usuario está logueado

**Qué hacen los de tarde (Testing):**

- Tests de seguridad: verificar que solo USER puede comprar, que solo ADMIN puede crear películas, que sin login se redirige a la página de acceso.

**Ejemplo de commit:**
```
feat(cine): proteger rutas por rol y filtrar sesiónes futuras

- Cartelera pública, compra solo para USER, admin para ADMIN
- Mostrar solo sesiónes con fecha futura
- Tests de autorización con @WithMockUser
```

---

### Semanas 9-10: Calidad, tests E2E y finalización

**Qué hacen los de mañana (Spring):**

- Revisar y limpiar código, mejorar vistas, reutilizar fragments.

**Qué hacen los de tarde (Testing):**

- Test E2E: buscar película por género -> ver detalle -> comprar entrada -> ver mis entradas
- Test E2E admin: crear película -> crear sala -> crear sesión
- Alcanzar cobertura del 80% o más

**Ejemplo de commit:**
```
test(cine): crear tests E2E de flujo de compra con Selenium

- Test de flujo completo de usuario: cartelera, detalle, compra, mis entradas
- Test de flujo de admin: crear película, sala y sesión
- Cobertura de tests al 80%
```


## Checklist semanal

```
SEMANA ___ -- GRUPO 3 CINE

DESARROLLO (mañana)
[ ] Entidades creadas: CinemaMovie, CinemaHall, CinemaSession, CinemaBooking
[ ] Repositorios con queries de películas y sesiónes disponibles
[ ] Servicios con checkAvailability y bookSeats
[ ] Controladores para cartelera, detalle, compra y mis entradas
[ ] Vistas funcionales en el navegador
[ ] Cada persona tiene al menos 1 commit esta semana
[ ] El proyecto compila: mvn clean compile

TESTING (tarde)
[ ] Tests unitarios de checkAvailability con Mockito
[ ] Tests de que no se venden más entradas de las disponibles
[ ] Tests MockMvc para los endpoints de compra
[ ] Tests de autorización (solo USER compra, solo ADMIN gestióna)
[ ] Cobertura al menos del 60%
[ ] Todos los tests pasan: mvn test
[ ] Cada persona tiene al menos 1 commit esta semana

GENERAL
[ ] Sin conflictos de Git pendientes
[ ] Presentación al grupo (5 minutos)
```


## Usuarios de prueba

```
Usuario: admin / Contraseña: admin / Rol: ADMIN
Que puede hacer: gestiónar películas, salas, sesiónes, ver informes de ventas

Usuario: user / Contraseña: user / Rol: USER
Que puede hacer: ver cartelera, comprar entradas, ver mis entradas
```

Entra en `http://localhost:8080/login` para probar.


## Comandos básicos para el día a día

```bash
./mvnw clean compile          # Compilar
./mvnw spring-boot:run        # Arrancar el servidor
./mvnw test                   # Ejecutar todos los tests
./mvnw test -Dtest="*ServiceTest"   # Solo tests de servicios
./mvnw test jacoco:report     # Generar informe de cobertura
```


## Preguntas frecuentes

**Qué pasa si un usuario intenta comprar más entradas de las disponibles?**
El servicio comprueba la disponibilidad antes de vender. Si no hay suficientes butacas, se lanza una excepción y el controlador muestra un mensaje de error al usuario.

**¿Cómo genero el código único de la entrada?**
Con `UUID.randomUUID().toString()`. Esto genera un identificador único cada vez. En una aplicación real se usaria para generar un código QR.

**Puedo añadir más géneros de películas?**
Sí, los géneros son simplemente Strings. Puedes usar los que quieras. Si prefieres, puedes usar un enum para limitar los valores posibles.

**Qué hago si no me da tiempo a terminar una semana?**
No pasa nada. Avisa al profesor. Lo importante es que entiendas lo que estás haciendo, no que vayas rápido.
