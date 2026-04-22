# Grupo 5: Plataforma de Venta de Entradas a Eventos

## Qué vas a construir

Tu grupo va a crear una **plataforma de venta de entradas para eventos** similar a Eventbrite, pero en versión simplificada. Los usuarios pueden ver próximos eventos (conciertos, conferencias, talleres), comprar entradas de distintos tipos (general, VIP, estudiante) y dejar reseñas después de asistir. El administrador gestióna eventos, tipos de entrada y ve informes de ventas.

Es una aplicación sencilla con operaciones básicas (crear, ver, editar, eliminar) más la lógica de venta de entradas y control de disponibilidad, que te servirá para aprender los patrones fundamentales de Spring Boot paso a paso.

**Alumnos de mañana (Spring Boot):** vosotros construís la aplicación. Crearéis las entidades, los servicios con la lógica de compra y disponibilidad, los controladores web y las páginas HTML.

**Alumnos de tarde (Testing):** vosotros probáis la aplicación. Crearéis tests unitarios para los servicios (especialmente para la comprobación de disponibilidad y la validación de reseñas), tests de integración y tests E2E con Selenium.

Al final de las 8-10 semanas tendréis un proyecto completo y bien testado que podréis enseñar en entrevistas de trabajo.


## Las 4 entidades de tu proyecto

### Event (los eventos)

Representa cada evento que se organiza.

```
Campos:
- id: Long (clave primaria, se genera automáticamente)
- title: String (nombre del evento, por ejemplo "Concierto de Rock")
- description: String (descripción del evento)
- eventDate: LocalDate (que dia se celebra)
- eventTime: LocalTime (a que hora empieza)
- location: String (lugar fisico, por ejemplo "Palacio de Deportes")
- category: String (categoría: "concierto", "conferencia", "taller")
- status: String (estado: "upcoming" si aún no ha ocurrido, "ongoing" si está en curso, "finished" si ya terminó)

Validaciones:
- @NotBlank en title y description
- @Future en eventDate (el evento debe ser en el futuro, al crearlo)
```

### TicketType (tipos de entrada)

Representa cada tipo de entrada disponible para un evento. Un mismo evento puede tener varios tipos (general, VIP, estudiante) con precios y cantidades diferentes.

```
Campos:
- id: Long (clave primaria)
- eventId: Long (a que evento pertenece)
- name: String (nombre del tipo: "general", "VIP", "estudiante")
- price: BigDecimal (precio de este tipo de entrada)
- totalAvailable: Integer (cuántas entradas de este tipo hay en total)
- soldCount: Integer (cuántas se han vendido ya)

Validaciones:
- @NotBlank en name
- @Min(0.01) en price (el precio debe ser positivo)
- @Min(1) en totalAvailable (al menos 1 entrada disponible)
```

La cantidad de entradas disponibles en cada momento es: `totalAvailable - soldCount`. Por ejemplo, si hay 200 entradas VIP y se han vendido 150, quedan 50 disponibles.

### EventTicket (las entradas compradas)

Representa cada entrada individual que un usuario ha comprado.

```
Campos:
- id: Long (clave primaria)
- ticketTypeId: Long (de que tipo es esta entrada)
- userId: Long (quién la compro)
- purchaseDate: LocalDateTime (cuando se compró)
- ticketCode: String (código único de la entrada, para verificar en la puerta)
- status: String (estado: "valid" si es válida, "used" si ya se usó, "cancelled" si se canceló)

Validaciones:
- @NotNull en ticketTypeId y userId
```

### EventReview (las reseñas)

Representa la opinión de un asistente después de un evento.

```
Campos:
- id: Long (clave primaria)
- eventId: Long (de que evento es la reseña)
- userId: Long (quién la escribió)
- rating: Integer (puntuación de 1 a 5 estrellas)
- comment: String (comentario)
- reviewDate: LocalDateTime (cuando se escribió)

Validaciones:
- @Min(1) y @Max(5) en rating
- Solo puede dejar reseña un usuario que tenga una entrada para ese evento
```

### Cómo se relacionan las entidades

```
Event ---1:N---> TicketType
(un evento tiene varios tipos de entrada)

TicketType ---1:N---> EventTicket
(un tipo de entrada puede tener muchas entradas vendidas)

Event ---1:N---> EventReview
(un evento puede tener muchas reseñas)

User ---1:N---> EventTicket
(un usuario puede comprar muchas entradas)

User ---1:N---> EventReview
(un usuario puede escribir muchas reseñas, una por evento)
```


## Pantallas que hay que crear

### Pantallas para el usuario (rol USER)

**1. Próximos eventos (GET /eventos)**

Muestra los eventos próximos. El usuario puede filtrar por categoría (concierto, conferencia, taller). De cada evento se ve: título, fecha, lugar y rango de precios.

**2. Detalle de un evento (GET /eventos/{id})**

Muestra la descripción completa, fecha, hora, lugar y una tabla con los tipos de entrada disponibles (nombre, precio, cuántas quedan). También muestra las reseñas de asistentes anteriores con la puntuación media. Si el usuario está logueado, puede ir a comprar.

**3. Comprar entradas (GET/POST /eventos/{id}/buy)**

Muestra el tipo de entrada selecciónado, un campo para elegir cantidad, el precio total calculado y un botón para confirmar la compra. Si no hay suficientes entradas, se muestra un mensaje de error.

**4. Mis entradas (GET /mis-entradas)**

Lista las entradas que el usuario ha comprado: nombre del evento, fecha, tipo de entrada, código único. Si el evento ya paso, aparece un botón "Dejar reseña".

**5. Dejar reseña (GET/POST /eventos/{id}/review)**

Formulario con un selector de puntuación (1 a 5 estrellas) y un campo de texto para el comentario. Solo aparece para eventos que ya han terminado y en los que el usuario tiene entrada.

### Pantallas para el administrador (rol ADMIN)

**6. Gestión de eventos (GET/POST /admin/eventos)**

CRUD de eventos: crear, editar, eliminar. Ver estadísticas: entradas vendidas, ingresos totales, puntuación media.

**7. Gestión de tipos de entrada (GET/POST /admin/eventos/{id}/tickets)**

CRUD de tipos de entrada para cada evento: definir nombre, precio, cantidad total. Ver cuántas se han vendido de cada tipo.

**8. Informe de compras (GET /admin/compras)**

Lista todas las compras realizadas. Filtrar por evento. Ver ingresos totales.


## Plan semana por semana

### Semanas 1-2: Crear las entidades (la base de datos)

**Qué hacen los de mañana (Spring):**

- Crear la clase `Event` dentro de `model/` con anotaciones `@Entity`, `@Table`, Lombok y validaciones.
- Crear `TicketType` con relación `@ManyToOne` hacia Event y campos de precio y disponibilidad.
- Crear `EventTicket` con relaciones hacia TicketType y User, y el campo ticketCode.
- Crear `EventReview` con relaciones hacia Event y User, y validación de rating (1-5).
- Verificar que compila: `mvn clean compile`.

**Qué hacen los de tarde (Testing):**

- Tests unitarios básicos para las entidades.
- Familiarizarse con los tests del proyecto base.

**Ejemplo de commit:**
```
feat(eventos): crear entidades Event, TicketType, EventTicket, EventReview

- Crear Event con campos title, eventDate, eventTime, location, category
- Crear TicketType con totalAvailable y soldCount para control de stock
- Crear EventTicket con ticketCode único para verificación
- Crear EventReview con rating (1-5) y relación a Event y User
```

---

### Semanas 3-4: Repositorios y servicios (acceso a datos y lógica)

La lógica más interesante de este proyecto es el sistema de compra de entradas con control de disponibilidad.

**Qué hacen los de mañana (Spring):**

- Crear `EventRepository` con queries:
  - `findByCategory(String category)` para filtrar por tipo de evento
  - `findByEventDateGreaterThanEqual(LocalDate date)` para ver solo eventos futuros
- Crear `TicketTypeRepository` con `findByEventId(Long eventId)` para ver los tipos de entrada de un evento.
- Crear `EventTicketRepository` con:
  - `findByUserId(Long userId)` para ver las entradas de un usuario
  - `findByEventId(Long eventId)` para ver todas las entradas vendidas de un evento
- Crear `EventTicketService` con la lógica principal:
  - `checkAvailability(Long ticketTypeId, Integer quantity)` -- comprueba si quedan suficientes entradas de ese tipo. Calcula `totalAvailable - soldCount` y compara con la cantidad pedida.
  - `purchaseTickets(TicketType type, User user, Integer quantity)` -- crea las entradas individuales (una por cada unidad comprada), actualiza el contador de vendidas, genera un código único para cada entrada.
  - `generateTicketCode()` -- genera un UUID único para cada entrada.

**Qué hacen los de tarde (Testing):**

- Test con `@DataJpaTest` para `EventRepository`: verificar que findByCategory filtra correctamente.
- Test unitario con Mockito para `EventTicketService`: verificar que checkAvailability devuelve false si se piden más entradas de las disponibles, y que purchaseTickets crea el número correcto de entradas y actualiza soldCount.

**Código de referencia (mañana):**

```java
// src/main/java/com/certidevs/service/EventTicketService.java
@Service
@RequiredArgsConstructor
@Transactional
public class EventTicketService {
    private final EventTicketRepository ticketRepository;
    private final TicketTypeRepository typeRepository;

    public boolean checkAvailability(Long ticketTypeId, Integer quantity) {
        TicketType type = typeRepository.findById(ticketTypeId)
            .orElseThrow();
        int remaining = type.getTotalAvailable() - type.getSoldCount();
        return remaining >= quantity;
    }

    public List<EventTicket> purchaseTickets(TicketType type, User user, Integer quantity) {
        if (!checkAvailability(type.getId(), quantity)) {
            throw new IllegalStateException("No hay suficientes entradas disponibles");
        }

        List<EventTicket> tickets = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            EventTicket ticket = new EventTicket();
            ticket.setTicketType(type);
            ticket.setUser(user);
            ticket.setPurchaseDate(LocalDateTime.now());
            ticket.setTicketCode(UUID.randomUUID().toString());
            ticket.setStatus("valid");
            tickets.add(ticket);
        }

        type.setSoldCount(type.getSoldCount() + quantity);
        typeRepository.save(type);

        return ticketRepository.saveAll(tickets);
    }

    public List<EventTicket> findUserTickets(Long userId) {
        return ticketRepository.findByUserId(userId);
    }
}
```

**Ejemplo de commit:**
```
feat(eventos): crear repositorios y servicio de compra de entradas

- Crear EventRepository con findByCategory y findByEventDateGreaterThanEqual
- Crear EventTicketService con checkAvailability y purchaseTickets
- purchaseTickets crea entradas individuales y actualiza soldCount
```

---

### Semanas 5-6: Controladores y vistas (la parte web)

**Qué hacen los de mañana (Spring):**

- Crear `EventController` con rutas para catálogo de eventos y detalle.
- Crear `EventTicketController` con rutas para comprar entradas y ver mis entradas.
- Crear `EventReviewController` con rutas para dejar reseña y ver reseñas.
- Crear las vistas Thymeleaf:
  - `templates/eventos/catálogo.html` -- lista de eventos con filtro por categoría
  - `templates/eventos/detail.html` -- detalle con tipos de entrada y reseñas
  - `templates/eventos/buy.html` -- formulario de compra
  - `templates/eventos/my-tickets.html` -- mis entradas con códigos
  - `templates/eventos/review-form.html` -- formulario de reseña

**Qué hacen los de tarde (Testing):**

- Tests con MockMvc para los controladores: catálogo, detalle, compra, reseñas.

**Vista Thymeleaf de referencia (eventos/detail.html):**

```html
<div class="container">
    <h1 th:text="${event.title}"></h1>
    <p th:text="${event.description}"></p>
    <p>
        <strong>Fecha:</strong> <span th:text="${#temporals.format(event.eventDate, 'dd/MM/yyyy')}"></span>
        <strong>Hora:</strong> <span th:text="${#temporals.format(event.eventTime, 'HH:mm')}"></span>
        <strong>Lugar:</strong> <span th:text="${event.location}"></span>
    </p>

    <h3>Tipos de Entrada</h3>
    <table class="table">
        <thead>
            <tr>
                <th>Tipo</th>
                <th>Precio</th>
                <th>Disponibles</th>
                <th>Accion</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="type : ${event.ticketTypes}">
                <td th:text="${type.name}"></td>
                <td th:text="${#numbers.formatCurrency(type.price)}"></td>
                <td th:text="${type.totalAvailable - type.soldCount}"></td>
                <td>
                    <a th:href="@{/eventos/{id}/buy(id=${event.id}, typeId=${type.id})}"
                       class="btn btn-sm btn-primary">Comprar</a>
                </td>
            </tr>
        </tbody>
    </table>

    <h3>Reseñas (Puntuacion media: <span th:text="${event.averageRating}"></span>/5)</h3>
    <div th:each="review : ${event.reviews}" class="card mb-2">
        <div class="card-body">
            <p>
                <strong th:text="${review.user.username}"></strong>
                <span th:text="${review.rating} + '/5'"></span>
            </p>
            <p th:text="${review.comment}"></p>
        </div>
    </div>
</div>
```

**Ejemplo de commit:**
```
feat(eventos): crear controladores y vistas de catálogo y compra

- Crear EventController con catálogo y detalle
- Crear EventTicketController con compra y mis entradas
- Crear vistas catálogo, detail, buy, my-tickets, review-form
```

---

### Semanas 7-8: Seguridad (proteger la aplicación)

**Qué hacen los de mañana (Spring):**

- Modificar `SecurityConfig`:
  - `/eventos` y `/eventos/{id}` -- acceso público (cualquiera puede ver los eventos)
  - `/eventos/*/buy` y `/mis-entradas` -- solo USER
  - `/eventos/*/review` -- solo USER (y solo si tiene entrada para ese evento)
  - `/admin/**` -- solo ADMIN
- No permitir comprar entradas de eventos que ya han pasado
- Solo permitir dejar reseña de eventos que ya han terminado

**Qué hacen los de tarde (Testing):**

- Tests de seguridad con `@WithMockUser`
- Test de que solo un asistente (alguien con entrada) puede dejar reseña
- Test de que no se pueden comprar entradas de eventos pasados

**Ejemplo de commit:**
```
feat(eventos): proteger rutas y validar compras y reseñas

- Catálogo público, compra y reseñas solo para USER, admin para ADMIN
- No vender entradas de eventos pasados
- Solo asistentes pueden dejar reseña
- Tests de autorización con @WithMockUser
```

---

### Semanas 9-10: Calidad, tests E2E y finalización

**Qué hacen los de mañana (Spring):**

- Revisar y limpiar código, mejorar vistas, reutilizar fragments.

**Qué hacen los de tarde (Testing):**

- Test E2E: ver eventos -> filtrar por categoría -> ver detalle -> comprar entrada -> ver mis entradas
- Test E2E admin: crear evento -> crear tipos de entrada -> ver ventas
- Tests de validación de rating (1-5)
- Alcanzar cobertura del 80% o más

**Ejemplo de commit:**
```
test(eventos): crear tests E2E de flujo de compra con Selenium

- Test de flujo completo: catálogo, detalle, compra, mis entradas
- Test de flujo admin: crear evento, tipos de entrada, ver ventas
- Cobertura de tests al 80%
```


## Checklist semanal

```
SEMANA ___ -- GRUPO 5 EVENTOS

DESARROLLO (mañana)
[ ] Entidades creadas: Event, TicketType, EventTicket, EventReview
[ ] Repositorios con queries de búsqueda y filtro por categoría
[ ] Servicios con checkAvailability, purchaseTickets
[ ] Controladores para catálogo, detalle, compra, mis entradas, reseñas
[ ] Vistas funcionales en el navegador
[ ] Cada persona tiene al menos 1 commit esta semana
[ ] El proyecto compila: mvn clean compile

TESTING (tarde)
[ ] Tests unitarios de checkAvailability con Mockito
[ ] Tests de validación de rating (1-5)
[ ] Tests MockMvc para compra de entradas
[ ] Test de que no se vende si no hay disponibilidad
[ ] Test de que solo un asistente puede dejar reseña
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
Que puede hacer: crear eventos, gestiónar tipos de entrada, ver informes de ventas

Usuario: user / Contraseña: user / Rol: USER
Que puede hacer: ver eventos, comprar entradas, ver mis entradas, dejar reseñas
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

**¿Cómo calculo las entradas disponibles de un tipo?**
Resta: `totalAvailable - soldCount`. Si un tipo tiene 200 entradas totales y se han vendido 150, quedan 50 disponibles.

**Por qué se crea una entrada individual (EventTicket) por cada unidad comprada?**
Porque cada entrada necesita su propio código único (ticketCode). Si un usuario compra 3 entradas, se crean 3 registros de EventTicket, cada uno con un código diferente. En una aplicación real, estos códigos se usarian para generar códigos QR.

**Puedo añadir más categorías de eventos?**
Si, las categorías son Strings. Puedes usar las que quieras. Si prefieres, usa un enum.

**Qué pasa si un usuario intenta comprar más entradas de las disponibles?**
El servicio comprueba la disponibilidad antes de vender. Si no hay suficientes, lanza una excepción y el controlador muestra un mensaje de error.

**Qué hago si no me da tiempo a terminar una semana?**
No pasa nada. Avisa al profesor. Lo importante es que entiendas lo que estás haciendo, no que vayas rápido.
