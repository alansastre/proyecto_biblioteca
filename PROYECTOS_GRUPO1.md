# Grupo 1: Sistema de Gestión de Restaurante

## Qué vas a construir

Tu grupo va a crear una **aplicación web para gestiónar un restaurante**. Piensa en una app donde un cliente puede ver la carta, hacer un pedido y consultar su estado, y donde el personal del restaurante gestióna platos, mesas, pedidos y facturas.

Es una aplicación sencilla con operaciones básicas (crear, ver, editar, eliminar) que te servirá para aprender los patrones fundamentales de Spring Boot paso a paso.

**Alumnos de mañana (Spring Boot):** vosotros construís la aplicación. Crearéis las entidades de base de datos, los servicios con la lógica, los controladores web y las páginas HTML.

**Alumnos de tarde (Testing):** vosotros probáis la aplicación. Crearéis tests unitarios para los servicios, tests de integración para los repositorios y controladores, y tests E2E con Selenium que simulan un usuario real navegando.

Al final de las 8-10 semanas tendréis un proyecto completo y bien testado que podréis enseñar en entrevistas de trabajo.


## Las 4 entidades de tu proyecto

Tu proyecto tiene 4 entidades principales. Cada entidad es una clase Java que representa una tabla en la base de datos. A continuación se describe cada una con sus campos y validaciones.

### RestaurantMenu (los platos de la carta)

Representa cada plato que ofrece el restaurante.

```
Campos:
- id: Long (clave primaria, se genera automáticamente)
- name: String (nombre del plato, por ejemplo "Ensalada César")
- description: String (descripción corta del plato)
- price: BigDecimal (precio del plato)
- category: String (tipo de plato: "entrada", "plato" o "postre")
- available: Boolean (indica si el plato está disponible ahora mismo)
- createdAt: LocalDateTime (fecha y hora en que se creo el registro)

Validaciones:
- @NotBlank(message = "El nombre es obligatorio") en name
- @NotNull(message = "El precio es obligatorio") en price
- @Min(0) en price (el precio no puede ser negativo)
```

### RestaurantTable (las mesas del restaurante)

Representa cada mesa fisica del restaurante.

```
Campos:
- id: Long (clave primaria)
- tableNumber: Integer (número visible de la mesa: 1, 2, 3...)
- capacity: Integer (cuántas personas caben en la mesa)
- status: String (estado actual: "available", "occupied" o "dirty")
- lastOrderId: Long (referencia al último pedido asociado a esta mesa)

Validaciones:
- @NotNull(message = "El número de mesa es obligatorio") en tableNumber
- @Min(1) en capacity (al menos 1 persona)
```

### RestaurantOrder (los pedidos)

Representa un pedido que hace un cliente.

```
Campos:
- id: Long (clave primaria)
- orderDate: LocalDateTime (cuando se hizo el pedido)
- tableId: Long (a que mesa pertenece este pedido)
- menuItemsCount: Integer (cuántos platos incluye)
- totalAmount: BigDecimal (importe total del pedido)
- status: String (estado: "new", "cooking", "served" o "paid")
- notes: String (notas especiales, por ejemplo "sin gluten")
- userId: Long (quién creo el pedido)

Validaciones:
- @NotNull en tableId
- @Min(0.01) en totalAmount (el total debe ser positivo)
```

### RestaurantBill (las facturas)

Representa la factura de un pedido ya servido.

```
Campos:
- id: Long (clave primaria)
- orderId: Long (a que pedido corresponde esta factura, relación 1:1)
- amount: BigDecimal (importe a cobrar)
- paymentMethod: String (método de pago: "cash", "card" o "transfer")
- paidAt: LocalDateTime (cuando se pago)
- status: String (estado: "pending" o "paid")
```

### Cómo se relacionan las entidades

```
RestaurantMenu
      |
      | Un plato puede aparecer en muchos pedidos (1:N)
      v
RestaurantOrder ----1:1----> RestaurantBill
      ^                      (cada pedido tiene una sola factura)
      |
      | Muchos pedidos pertenecen a un usuario (N:1)
      |
User (esta entidad ya existe en el proyecto base, no hay que crearla)
```

RestaurantTable tiene una relación 1:N con RestaurantOrder: una mesa puede tener muchos pedidos a lo largo del tiempo.


## Pantallas que hay que crear

A continuación se listan todas las páginas web de tu aplicación. Cada una indica la ruta HTTP, que debe mostrar y para que tipo de usuario es.

### Pantallas para el cliente (usuario con rol USER)

**1. Ver la carta (GET /menu)**

El cliente ve todos los platos disponibles. Puede filtrar por categoría (entrada, plato, postre) y buscar por nombre. Cada plato muestra su nombre, categoría, precio y si está disponible.

**2. Ver detalle de un plato (GET /menu/{id})**

Al hacer clic en un plato, se muestra su información completa: nombre, descripción, precio y un botón para añadirlo al pedido.

**3. Mi pedido actual (GET /orders/current)**

Muestra los platos que el cliente ha selecciónado, la cantidad de cada uno, el total a pagar y un botón para confirmar el pedido.

**4. Mis pedidos anteriores (GET /orders/my-orders)**

Tabla con el historial de pedidos del cliente: fecha, total y estado de cada uno.

**5. Ver estado de un pedido (GET /orders/{id})**

Muestra el estado actual de un pedido concreto (nuevo, cocinando, servido, pagado).

### Pantallas para el administrador (usuario con rol ADMIN)

**6. Gestión de la carta (GET /admin/menu)**

Lista todos los platos con botónes para crear uno nuevo, editar o eliminar.

**7. Formulario de plato (GET /admin/menu/create y GET /admin/menu/{id}/edit)**

Formulario para crear un plato nuevo o editar uno existente. Campos: nombre, descripción, precio, categoría, disponibilidad.

**8. Gestión de mesas (GET /admin/tables)**

Muestra el estado de cada mesa (libre, ocupada, sucia) y botónes para cambiar el estado.

**9. Gestión de pedidos (GET /admin/orders)**

Lista todos los pedidos con filtro por estado. Botónes para avanzar el estado: nuevo -> cocinando -> servido -> pagado.

**10. Gestión de facturas (GET /admin/bills)**

Permite generar la factura de un pedido servido y ver el historial de pagos.


## Flujo de uso de la aplicación

Asi es como un usuario usaria la aplicación paso a paso:

**Como cliente:**
1. Entrar en la aplicación con usuario "user" y contraseña "user"
2. Ver la carta del restaurante
3. Buscar un plato y ver su detalle
4. Añadirlo a mi pedido
5. Revisar mi pedido (ver el total)
6. Confirmar el pedido
7. Consultar el estado del pedido (cocinando, servido...)
8. Una vez servido, pagar
9. Ver el recibo

**Como administrador:**
1. Entrar con usuario "admin" y contraseña "admin"
2. Crear platos nuevos en la carta, editar precios, activar o desactivar platos
3. Ver que mesas estan libres, ocupadas o sucias
4. Ver los pedidos pendientes y marcarlos como "cocinando"
5. Cuando el plato está listo, marcar como "servido"
6. Generar la factura y marcar como "pagado"


## Plan semana por semana

A continuación se detalla que hay que hacer cada quincena. Cada fase construye sobre la anterior, así que es importante completarlas en orden.

### Semanas 1-2: Crear las entidades (la base de datos)

En esta fase creas las clases Java que representan las tablas. Es lo primero porque todo lo demás (repositorios, servicios, controladores) depende de que las entidades existan.

**Qué hacen los de mañana (Spring):**

- Crear la clase `RestaurantMenu` dentro de la carpeta `model/`. Añadirle las anotaciones `@Entity`, `@Table`, `@Id`, `@GeneratedValue` y las anotaciones de Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`). Definir todos los campos listados arriba con sus validaciones.
- Crear la clase `RestaurantTable` de la misma forma.
- Crear la clase `RestaurantOrder` con sus relaciones (`@ManyToOne` hacia User).
- Crear la clase `RestaurantBill` con su relación `@OneToOne` hacia RestaurantOrder.
- Comprobar que el proyecto compila sin errores: ejecutar `mvn clean compile`.

**Qué hacen los de tarde (Testing):**

- Escribir un test unitario sencillo para `RestaurantMenu`: verificar que se pueden crear instancias y que los getters/setters funcionan.
- Si se usan enums para los estados (status), escribir un test que compruebe que los valores del enum son los esperados.
- Familiarizarse con la estructura de tests del proyecto base (mirar los tests existentes como ejemplo).

**Ejemplo de commit al terminar esta fase:**
```
feat(restaurante): crear entidades base del dominio

- Crear RestaurantMenu con @Entity y validaciones @NotBlank, @Min
- Crear RestaurantTable con campos tableNumber, capacity, status
- Crear RestaurantOrder con relación @ManyToOne a User
- Crear RestaurantBill con relación @OneToOne a RestaurantOrder
```

---

### Semanas 3-4: Repositorios y servicios (acceso a datos y lógica)

En esta fase creas las interfaces que permiten guardar y buscar datos en base de datos, y las clases de servicio donde va la lógica de negocio.

**Qué hacen los de mañana (Spring):**

- Crear `RestaurantMenuRepository` que extienda de `JpaRepository<RestaurantMenu, Long>`. Añadir queries personalizadas:
  - `List<RestaurantMenu> findByCategory(String category)` para filtrar por tipo de plato
  - `List<RestaurantMenu> findByAvailableTrue()` para obtener solo los platos disponibles
  - `List<RestaurantMenu> findByNameIgnoreCaseContaining(String name)` para buscar por nombre
- Crear `RestaurantTableRepository` con `List<RestaurantTable> findByStatusEquals(String status)` para filtrar mesas por estado.
- Crear `RestaurantOrderRepository` básico.
- Crear `RestaurantMenuService` con métodos CRUD (findAll, findById, save, deleteById) y los métodos de búsqueda (findByCategory, findAvailable).
- Crear `RestaurantTableService` con métodos similares.
- Crear `RestaurantOrderService` con lógica para calcular el total de un pedido y cambiar su estado.

**Qué hacen los de tarde (Testing):**

- Escribir un test con `@DataJpaTest` para `RestaurantMenuRepository`. Este tipo de test arranca solo la capa de base de datos. Verificar que `findByCategory("plato")` devuelve solo los platos de esa categoría.
- Escribir un test unitario para `RestaurantMenuService` usando Mockito. Crear un mock del repositorio y verificar que la lógica del servicio funciona correctamente.
- Escribir un test unitario para `RestaurantOrderService` que verifique el cálculo del total.

**Código de referencia (mañana):**

```java
// src/main/java/com/certidevs/repository/RestaurantMenuRepository.java
public interface RestaurantMenuRepository extends JpaRepository<RestaurantMenu, Long> {
    List<RestaurantMenu> findByCategory(String category);
    List<RestaurantMenu> findByAvailableTrue();
    List<RestaurantMenu> findByNameIgnoreCaseContaining(String name);
}
```

```java
// src/main/java/com/certidevs/service/RestaurantMenuService.java
@Service
@RequiredArgsConstructor
public class RestaurantMenuService {
    private final RestaurantMenuRepository repository;

    public List<RestaurantMenu> findAll() {
        return repository.findAll();
    }

    public List<RestaurantMenu> findAvailable() {
        return repository.findByAvailableTrue();
    }

    public List<RestaurantMenu> findByCategory(String category) {
        return repository.findByCategory(category);
    }

    public RestaurantMenu save(RestaurantMenu menu) {
        return repository.save(menu);
    }
}
```

**Ejemplo de commit al terminar esta fase:**
```
feat(restaurante): crear repositorios y servicios con queries personalizadas

- Crear RestaurantMenuRepository con findByCategory y findByAvailableTrue
- Crear RestaurantMenuService con métodos CRUD y búsqueda
- Crear RestaurantOrderService con cálculo de total del pedido
```

---

### Semanas 5-6: Controladores y vistas (la parte web)

En esta fase conectas los servicios con páginas web. Creas los controladores que reciben las peticiones HTTP y las vistas HTML que ve el usuario en el navegador.

**Qué hacen los de mañana (Spring):**

- Crear `RestaurantMenuController` con las siguientes rutas:
  - GET `/menu` -- muestra la lista de platos con filtro por categoría
  - GET `/menu/{id}` -- muestra el detalle de un plato
  - GET `/admin/menu/create` -- muestra el formulario para crear un plato (solo ADMIN)
  - POST `/admin/menu/create` -- procesa el formulario de creación
  - GET `/admin/menu/{id}/edit` -- muestra el formulario para editar un plato
  - POST `/admin/menu/{id}/edit` -- procesa el formulario de edición
  - POST `/admin/menu/{id}/delete` -- elimina un plato

- Crear `RestaurantOrderController` con:
  - GET `/orders/current` -- muestra mi pedido actual
  - POST `/orders/add-item` -- añade un plato al pedido
  - POST `/orders/confirm` -- confirma el pedido
  - GET `/orders/my-orders` -- muestra mis pedidos anteriores

- Crear las vistas Thymeleaf (archivos HTML en templates/):
  - `templates/menu/list.html` -- listado de platos con filtro por categoría
  - `templates/menu/detail.html` -- detalle de un plato con botón de añadir
  - `templates/menu/form.html` -- formulario para crear o editar plato (solo ADMIN)
  - `templates/order/current.html` -- mi pedido actual con total
  - `templates/order/history.html` -- mis pedidos pasados

**Qué hacen los de tarde (Testing):**

- Escribir tests con MockMvc para `RestaurantMenuController`:
  - Verificar que GET `/menu` devuelve código 200 y contiene la lista de platos
  - Verificar que GET `/menu/{id}` devuelve el detalle correcto
  - Verificar que POST `/admin/menu/create` sin autenticación redirige a login (código 302)
  - Verificar que POST `/admin/menu/create` con `@WithMockUser(roles = "ADMIN")` funciona

**Código de referencia (mañana):**

```java
// src/main/java/com/certidevs/controller/RestaurantMenuController.java
@Controller
@RequestMapping("/menu")
@RequiredArgsConstructor
public class RestaurantMenuController {
    private final RestaurantMenuService menuService;

    @GetMapping
    public String list(@RequestParam(required = false) String category, Model model) {
        List<RestaurantMenu> menus = category != null
            ? menuService.findByCategory(category)
            : menuService.findAvailable();
        model.addAttribute("menus", menus);
        model.addAttribute("categories", List.of("entrada", "plato", "postre"));
        return "menu/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("menu", menuService.findById(id));
        return "menu/detail";
    }
}
```

**Vista Thymeleaf de referencia (menu/list.html):**

```html
<div class="container">
    <h1>Carta del Restaurante</h1>

    <form method="get">
        <select name="category">
            <option value="">Todas las categorías</option>
            <option th:each="cat : ${categories}"
                    th:value="${cat}"
                    th:text="${cat}"></option>
        </select>
        <button type="submit">Filtrar</button>
    </form>

    <table class="table">
        <thead>
            <tr>
                <th>Plato</th>
                <th>Categoría</th>
                <th>Precio</th>
                <th>Disponible</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="menu : ${menus}">
                <td th:text="${menu.name}"></td>
                <td th:text="${menu.category}"></td>
                <td th:text="${menu.price}"></td>
                <td th:text="${menu.available ? 'Si' : 'No'}"></td>
                <td>
                    <a th:href="@{/menu/{id}(id=${menu.id})}" class="btn btn-sm btn-info">Ver</a>
                    <a th:if="${#authorization.expression('hasRole(''ADMIN'')')}"
                       th:href="@{/admin/menu/{id}/edit(id=${menu.id})}"
                       class="btn btn-sm btn-warning">Editar</a>
                </td>
            </tr>
        </tbody>
    </table>

    <a th:if="${#authorization.expression('hasRole(''ADMIN'')')}"
       href="/admin/menu/create" class="btn btn-primary">Crear Plato</a>
</div>
```

**Ejemplo de commit al terminar esta fase:**
```
feat(restaurante): crear controladores y vistas Thymeleaf

- Crear RestaurantMenuController con GET/POST para carta y formularios
- Crear RestaurantOrderController con flujo de pedido
- Crear vistas list.html, detail.html, form.html con filtros y autorización
```

---

### Semanas 7-8: Seguridad (proteger la aplicación)

En esta fase configuras que rutas puede acceder cada tipo de usuario. El proyecto base ya tiene Spring Security configurado, así que solo tienes que extenderlo con tus nuevas rutas.

**Qué hacen los de mañana (Spring):**

- Modificar `SecurityConfig` para proteger las rutas de tu proyecto:
  - `/menu` -- acceso público (cualquiera puede ver la carta)
  - `/orders/**` -- solo usuarios con rol USER
  - `/admin/**` -- solo usuarios con rol ADMIN
- Implementar el flujo de cambio de estado de pedido: un ADMIN puede avanzar el estado de un pedido paso a paso (nuevo -> cocinando -> servido -> pagado)
- Mostrar el nombre del usuario logueado en las vistas

**Qué hacen los de tarde (Testing):**

- Escribir tests de seguridad con `@WithMockUser`:
  - Verificar que un usuario con rol USER puede acceder a `/orders` pero no a `/admin`
  - Verificar que un usuario con rol ADMIN puede acceder a `/admin`
  - Verificar que sin autenticación se redirige a `/login`
  - Verificar que un USER no puede crear platos (debe devolver 403 Forbidden)

**Código de referencia (tarde):**

```java
// src/test/java/com/certidevs/controller/RestaurantMenuControllerSecurityTest.java
@SpringBootTest
@AutoConfigureMockMvc
class RestaurantMenuControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateMenuPublic_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(post("/admin/menu/create")
                .param("name", "Pizza")
                .param("price", "10.00"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testCreateMenuAsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/admin/menu/create")
                .param("name", "Pizza")
                .param("price", "10.00"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateMenuAsAdmin_ShouldSucceed() throws Exception {
        mockMvc.perform(post("/admin/menu/create")
                .param("name", "Pizza")
                .param("price", "10.00")
                .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }
}
```

**Ejemplo de commit al terminar esta fase:**
```
feat(restaurante): configurar seguridad y flujo de estados de pedido

- Proteger rutas /orders para USER y /admin para ADMIN
- Implementar cambio de estado de pedido (new -> cooking -> served -> paid)
- Agregar tests de autorización con @WithMockUser
```

---

### Semanas 9-10: Calidad, tests E2E y finalización

Esta es la fase final. Se pule el proyecto y se crean los tests más completos: tests E2E con Selenium que simulan un usuario real en el navegador.

**Qué hacen los de mañana (Spring):**

- Revisar el código y eliminar duplicaciones (por ejemplo, si varios controladores repiten la misma lógica, extraerla a un método comun)
- Mejorar las vistas: reutilizar fragments de Thymeleaf, mejorar la navegación
- Verificar que todas las funcionalidades se prueban manualmente en el navegador
- Documentar los servicios con comentarios donde la lógica no sea obvia

**Qué hacen los de tarde (Testing):**

- Escribir un test E2E con Selenium que simule el flujo completo de un cliente:
  1. Login como "user"
  2. Ver la carta
  3. Buscar por categoría
  4. Hacer un pedido
  5. Ver el estado del pedido
  6. Logout

- Escribir un test E2E que simule el flujo completo de un administrador:
  1. Login como "admin"
  2. Crear un plato nuevo
  3. Editar su precio
  4. Ver pedidos activos
  5. Cambiar estado de un pedido a "servido"

- Aumentar la cobertura de tests al 80% o más
- Revisar y limpiar tests duplicados

**Código de referencia (tarde):**

```java
// src/test/java/com/certidevs/selenium/RestaurantSeleniumTest.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RestaurantSeleniumTest {
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    void testCompleteOrderFlow() {
        // 1. Login
        driver.get("http://localhost:8080/login");
        driver.findElement(By.name("username")).sendKeys("user");
        driver.findElement(By.name("password")).sendKeys("user");
        driver.findElement(By.tagName("button")).click();

        // 2. Ver carta
        driver.get("http://localhost:8080/menu");
        assertThat(driver.getTitle()).contains("Menu");
        List<WebElement> menus = driver.findElements(By.className("menu-item"));
        assertThat(menus).isNotEmpty();

        // 3. Selecciónar plato
        menus.get(0).click();
        driver.findElement(By.className("btn-add-order")).click();

        // 4. Verificar pedido
        driver.get("http://localhost:8080/orders/current");
        assertThat(driver.findElement(By.className("order-total")).getText())
            .contains("Total");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}
```

**Ejemplo de commit al terminar esta fase:**
```
test(restaurante): crear tests E2E con Selenium y alcanzar 80% de cobertura

- Crear test E2E del flujo completo de cliente (login, carta, pedido)
- Crear test E2E del flujo completo de admin (crear plato, gestiónar pedidos)
- Refactorizar tests duplicados
```


## Checklist semanal

Cada viernes, comprueba que tu grupo cumple con estos puntos. El profesor los revisará:

```
SEMANA ___ -- GRUPO 1 RESTAURANTE

DESARROLLO (mañana)
[ ] Las entidades compilan sin errores (mvn clean compile)
[ ] Los repositorios tienen queries personalizadas
[ ] Los servicios contienen la lógica de negocio
[ ] Los controladores responden a las rutas GET y POST
[ ] Las vistas Thymeleaf se ven correctamente en el navegador
[ ] SecurityConfig protege las rutas de admin
[ ] Cada persona del grupo tiene al menos 1 commit esta semana

TESTING (tarde)
[ ] Hay tests unitarios con Mockito para cada servicio
[ ] Hay tests de integración con @DataJpaTest para los repositorios
[ ] Hay tests MockMvc para los controladores
[ ] Los tests de autorización con @WithMockUser funcionan
[ ] La cobertura de tests es al menos del 60%
[ ] Todos los tests pasan: mvn test
[ ] Cada persona del grupo tiene al menos 1 commit esta semana

GENERAL
[ ] El repositorio está sincronizado (no hay conflictos de merge pendientes)
[ ] 2-3 personas del grupo presentan su código en clase (5 minutos)
```


## Usuarios de prueba

Para probar tu aplicación en el navegador, usa estos usuarios que ya estan precargados:

```
Usuario: admin
Contraseña: admin
Rol: ADMIN
Que puede hacer: crear, editar y eliminar platos, mesas, pedidos y facturas

Usuario: user
Contraseña: user
Rol: USER
Que puede hacer: ver la carta, hacer pedidos, ver sus pedidos
```

Entra en `http://localhost:8080/login` para probar.


## Comandos básicos para el día a día

```bash
# Compilar (comprueba que no hay errores)
./mvnw clean compile

# Arrancar el servidor (después abre http://localhost:8080)
./mvnw spring-boot:run

# Ejecutar todos los tests
./mvnw test

# Ejecutar solo los tests unitarios de servicios
./mvnw test -Dtest="*ServiceTest"

# Generar informe de cobertura
./mvnw test jacoco:report
```


## Preguntas frecuentes

**Puedo crear más entidades o tablas?**
Sí, pero antes coméntalo con el profesor. Es mejor mantener el proyecto sencillo y terminar bien las 4 entidades principales antes de añadir más.

**Dónde pongo mis archivos Java?**
Directamente en las carpetas `model/`, `repository/`, `service/`, `controller/`. No crees subcarpetas con el nombre de tu grupo.

**Dónde pongo mis vistas HTML?**
En `src/main/resources/templates/`. Crea una subcarpeta con el nombre de la entidad: `templates/menu/`, `templates/order/`, etc.

**Qué hago si hay un conflicto de Git?**
Los alumnos de mañana editan `src/main/` y los de tarde `src/test/`, así que normalmente no hay conflictos. Si ocurre, avisad al profesor y lo resolvemos juntos.

**Cuántos tests necesito como minimo?**
Al menos 1 test unitario por servicio, 1 test de integración por repositorio y 1 test de controlador por cada endpoint crítico. Mas adelante se añaden los tests E2E.

**Qué hago si no me da tiempo a terminar una semana?**
No pasa nada. Avisad al profesor. Lo importante es que entiendas lo que estás haciendo, no que vayas rápido. El profesor ajustará el ritmo si es necesario.
