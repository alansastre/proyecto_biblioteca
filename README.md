# Biblioteca Digital — Spring Boot 4

Aplicación web completa de gestión de una biblioteca digital construida con **Java 25** y **Spring Boot 4.0.3**.
Cubre la arquitectura en capas de Spring Boot y sirve como base para desarrollar proyectos similares.


## Stack tecnológico

| Componente           | Tecnología                   | Versión   |
|----------------------|------------------------------|-----------|
| Lenguaje             | Java                         | 25        |
| Framework            | Spring Boot                  | 4.0.3     |
| Build                | Maven                        | 3.6.3+    |
| Base de datos        | H2 (en memoria)              | —         |
| ORM                  | Spring Data JPA / Hibernate  | —         |
| Seguridad            | Spring Security              | 7.x       |
| Plantillas           | Thymeleaf                    | 3.x       |
| CSS                  | Bootstrap (WebJars)          | 5.3.3     |
| Validación           | Bean Validation (JSR 380)    | —         |
| Generación de código | Lombok                       | —         |
| Testing unitario     | JUnit Jupiter (JUnit 6)      | —         |
| Testing con mocks    | Mockito                      | —         |
| Testing integración  | Spring Boot Test + MockMvc   | —         |
| Testing E2E          | Selenium WebDriver           | 4.x       |
| Cobertura            | JaCoCo                       | 0.8.13    |

## Requisitos previos

- **JDK 25** instalado y configurado en `JAVA_HOME`
- **IntelliJ IDEA** (recomendado) con el plugin de Lombok habilitado
- **Google Chrome** (para los tests de Selenium)
- **Maven 3.6.3+** (o usar el wrapper incluido `mvnw`)

## Cómo ejecutar

```bash
# Opción 1: Maven wrapper (recomendado, no requiere Maven instalado)
./mvnw spring-boot:run

# Opción 2: Maven instalado
mvn spring-boot:run

# Opción 3: Desde IntelliJ IDEA
# Ejecutar BibliotecaApplication.java con botón derecho > Run
```

La aplicación arranca en **http://localhost:8080**

### Usuarios de prueba

| Usuario | Contraseña | Rol          | Permisos                                         |
|---------|------------|--------------|--------------------------------------------------|
| `admin` | `admin`    | `ROLE_ADMIN` | Acceso total: CRUD completo + gestión de usuarios |
| `user`  | `user`     | `ROLE_USER`  | Ver, buscar, comprar, favoritos, reseñar         |

### Consola H2 (base de datos)

Accesible en **http://localhost:8080/h2-console** con:
- JDBC URL: `jdbc:h2:mem:biblioteca`
- User: `sa`
- Password: *(vacío)*

---

## Arquitectura del proyecto

```
src/main/java/com/certidevs/
├── BibliotecaApplication.java          # Clase principal (@SpringBootApplication)
│
├── config/                             # Configuración
│   ├── SecurityConfig.java             # Reglas de seguridad (autorización, login, logout)
│   ├── CustomUserDetailsService.java   # Carga usuarios de BD para Spring Security
│   ├── DataInitializer.java            # Datos de ejemplo al arrancar (CommandLineRunner)
│   └── ViewSecurityAdvice.java         # Expone isAuthenticated/isAdmin a Thymeleaf
│
├── model/                              # Entidades JPA (tablas de BD)
│   ├── User.java                       # Usuario (implementa UserDetails)
│   ├── Role.java                       # Enum de roles (ROLE_USER, ROLE_ADMIN)
│   ├── Book.java                       # Libro (entidad central)
│   ├── Author.java                     # Autor
│   ├── Category.java                   # Categoría
│   ├── Review.java                     # Reseña (1-5 estrellas)
│   └── Purchase.java                   # Compra
│
├── repository/                         # Acceso a datos (Spring Data JPA)
│   ├── UserRepository.java             # Derived queries: findByUsername, existsByEmail...
│   ├── BookRepository.java             # @Query JPQL + @EntityGraph para optimización
│   ├── AuthorRepository.java           # findByNameContaining, findByNationality
│   ├── CategoryRepository.java         # findByNameContaining, findDetailedById
│   ├── ReviewRepository.java           # findByBookId, findByRating, ordenadas por fecha
│   └── PurchaseRepository.java         # findByUserId con @EntityGraph
│
├── service/                            # Lógica de negocio
│   ├── UserService.java                # Registro, favoritos, codificación de contraseñas
│   ├── BookService.java                # Filtros null-safe por título/autor/categoría/precio
│   ├── AuthorService.java              # CRUD + búsqueda por nombre/nacionalidad
│   ├── CategoryService.java            # CRUD + búsqueda por nombre
│   ├── ReviewService.java              # CRUD + autorización a nivel de recurso (canModify)
│   └── PurchaseService.java            # Registro de compras (buy)
│
├── controller/                         # Controladores web (MVC)
│   ├── HomeController.java             # Dashboard (GET /) con estadísticas
│   ├── AuthController.java             # Login y registro con validación
│   ├── RegisterForm.java               # DTO para formulario de registro
│   ├── BookController.java             # CRUD libros + favoritos + compras + filtros
│   ├── AuthorController.java           # CRUD autores (patrón PRG)
│   ├── CategoryController.java         # CRUD categorías (patrón PRG)
│   ├── ReviewController.java           # CRUD reseñas (control de propiedad)
│   ├── UserController.java             # Perfil del usuario autenticado
│   └── UserManagementController.java   # Gestión de usuarios (solo ADMIN)
│
└── util/                               # Clases de utilidad (lógica pura sin dependencias)
    ├── IsbnValidator.java              # Validación ISBN-10/ISBN-13 con checksum
    ├── BookStatistics.java             # Estadísticas: media, distribución, min/max precio
    └── PriceCalculator.java            # Precios con descuentos por volumen, fidelidad e IVA

src/main/resources/
├── application.properties              # Configuración (H2, JPA, Thymeleaf)
└── templates/                          # Plantillas Thymeleaf (HTML)
    ├── index.html                      # Página principal (dashboard)
    ├── error.html                      # Página de error
    ├── fragments/
    │   ├── layout.html                 # Plantilla base
    │   └── navbar.html                 # Barra de navegación (con CSS personalizado)
    ├── auth/
    │   ├── login.html                  # Formulario de login
    │   └── register.html               # Formulario de registro
    ├── book/
    │   ├── book-list.html              # Catálogo con filtros (título, autor, categoría)
    │   ├── book-detail.html            # Detalle del libro + reseñas + acciones
    │   └── book-form.html              # Formulario crear/editar libro
    ├── author/
    │   ├── author-list.html            # Listado de autores con búsqueda
    │   ├── author-detail.html          # Detalle del autor + sus libros
    │   └── author-form.html            # Formulario crear/editar autor
    ├── category/
    │   ├── category-list.html          # Listado de categorías
    │   ├── category-detail.html        # Detalle de categoría + libros asociados
    │   └── category-form.html          # Formulario crear/editar categoría
    ├── review/
    │   ├── review-list.html            # Listado de reseñas con filtro por rating
    │   └── review-form.html            # Formulario crear/editar reseña
    └── user/
        ├── profile.html                # Perfil del usuario (favoritos + compras)
        ├── user-list.html              # Listado de usuarios (admin)
        └── user-detail.html            # Detalle de usuario (admin)
```

## Relaciones entre entidades

```
User ──1:N──> Review       (un usuario escribe muchas reseñas)
User ──1:N──> Purchase     (un usuario realiza muchas compras)
User ──N:M──> Book         (favoritos — lado propietario: User)

Author ──1:N──> Book       (un autor tiene muchos libros, cascade ALL)

Book ──N:M──> Category     (un libro tiene varias categorías — lado propietario: Book)
Book ──1:N──> Review       (un libro tiene muchas reseñas, cascade ALL)
Book ──1:N──> Purchase     (un libro tiene muchas compras)
```

## Reglas de seguridad

| Recurso                              | Anónimo | USER | ADMIN |
|--------------------------------------|---------|------|-------|
| `/`, `/login`, `/register`           | Sí      | Sí   | Sí    |
| `/books`, `/authors`, `/categories`, `/reviews` (listar/detalle) | Sí | Sí | Sí |
| `/user/profile`                      | No      | Sí   | Sí    |
| `/books/*/favorite`, `/books/*/buy`  | No      | Sí   | Sí    |
| `/reviews/create`, `/reviews/*/edit`, `/reviews/*/delete` | No | Sí | Sí |
| `/books/create`, `/authors/create`, `/categories/create`  | No | No | Sí |
| `/books/*/edit`, `/authors/*/edit`, `/categories/*/edit`   | No | No | Sí |
| `/books/*/delete`, `/authors/*/delete`, `/categories/*/delete` | No | No | Sí |
| `/users/**` (gestión de usuarios)    | No      | No   | Sí    |

---

## Testing

El proyecto incluye una suite completa de tests organizados en 5 niveles progresivos.

### Ejecutar tests

```bash
# Ejecutar todos los tests (unitarios + integración + Selenium)
./mvnw test

# Ejecutar solo tests unitarios sin Mockito (lógica pura)
./mvnw test -Dtest="com.certidevs.unit.**"

# Ejecutar solo tests unitarios con Mockito (servicios)
./mvnw test -Dtest="*ServiceTest"

# Ejecutar solo tests de repositorio (@DataJpaTest)
./mvnw test -Dtest="*RepositoryTest"

# Ejecutar solo tests de controlador (MockMvc)
./mvnw test -Dtest="*IntegrationTest"

# Ejecutar solo tests de Selenium (requiere Chrome)
./mvnw test -Dtest="*SeleniumTest"
```

### Ejecutar tests desde IntelliJ IDEA

El proyecto incluye **JUnit Platform Suites** para ejecutar grupos de tests con un solo clic:

| Suite                        | Paquete                          | Qué ejecuta                    |
|------------------------------|----------------------------------|--------------------------------|
| `AllTestsSuite.java`         | `com.certidevs`                  | Todos los tests                |
| `UnitTestsSuite.java`        | `com.certidevs.unit`             | Tests unitarios sin Mockito    |
| `IntegrationTestsSuite.java` | `com.certidevs.repository`, `com.certidevs.controller`, `com.certidevs.service` | Repositorio + controlador + servicio |
| `SeleniumTestsSuite.java`    | `com.certidevs.selenium`         | Tests E2E con Selenium         |

Para ejecutar una suite: abre el archivo en IntelliJ → botón ▶️ de play a la izquierda de la clase.

### Análisis de cobertura (JaCoCo)

```bash
# Ejecutar tests y generar informe de cobertura
./mvnw test

# El informe HTML se genera automáticamente en:
# target/site/jacoco/index.html

# En IntelliJ IDEA: clic derecho en test → "Run with Coverage"
```

### Organización de los tests

```
src/test/java/com/certidevs/
│
├── BibliotecaApplicationTests.java     # Smoke test (carga de contexto Spring)
│
├── AllTestsSuite.java                  # Suite: todos los tests
├── UnitTestsSuite.java                 # Suite: tests unitarios sin Mockito
├── IntegrationTestsSuite.java          # Suite: repositorio + controlador + servicio
├── SeleniumTestsSuite.java             # Suite: tests E2E
│
├── unit/                               # ══ NIVEL 1: Tests unitarios sin Mockito ══
│   ├── model/                          # Tests de entidades (JUnit básico)
│   │   ├── RoleTest.java              #   @Test, assertEquals, assertThrows, @ParameterizedTest
│   │   ├── BookTest.java             #   @Nested, @BeforeEach, assertAll, AssertJ
│   │   ├── AuthorTest.java           #   @AfterEach, AssertJ extracting
│   │   ├── UserTest.java             #   @BeforeAll, @AfterAll, assertInstanceOf
│   │   ├── ReviewTest.java           #   @ValueSource, @CsvSource, @MethodSource
│   │   ├── CategoryTest.java         #   @EnabledOnOs (ejecución condicional)
│   │   └── PurchaseTest.java         #   @RepeatedTest (repetición de tests)
│   │
│   ├── util/                           # Tests de utilidades (lógica pura sin dependencias)
│   │   ├── IsbnValidatorTest.java     #   @ParameterizedTest, @NullAndEmptySource
│   │   ├── BookStatisticsTest.java    #   Optional, colecciones, edge cases
│   │   └── PriceCalculatorTest.java   #   @CsvSource, boundary testing, assertThrows
│   │
│   ├── service/
│   │   └── ReviewServiceLogicTest.java #  Test de lógica sin mocks (canModify)
│   │
│   └── RegisterFormValidationTest.java #  Bean Validation sin Spring (Validator API)
│
├── service/                            # ══ NIVEL 2: Tests unitarios con Mockito ══
│   ├── AuthorServiceTest.java          #   @Mock, @InjectMocks, when/thenReturn, verify
│   ├── BookServiceTest.java            #   Lógica de filtros con mocks
│   └── ReviewServiceTest.java          #   Autorización canModify con mocks
│
├── repository/                         # ══ NIVEL 3: Tests de integración JPA ══
│   ├── AuthorRepositoryTest.java       #   @DataJpaTest, derived queries
│   ├── BookRepositoryTest.java         #   @Query JPQL, @EntityGraph
│   ├── CategoryRepositoryTest.java     #   EntityManager flush/clear
│   ├── UserRepositoryTest.java         #   existsByUsername, findByEmail
│   ├── ReviewRepositoryTest.java       #   findByBookId, findByRating, ordenación
│   └── PurchaseRepositoryTest.java     #   findByUserId, @EntityGraph
│
├── controller/                         # ══ NIVEL 4: Tests de integración web ══
│   ├── HomeControllerIntegrationTest.java      # MockMvc: endpoints públicos
│   ├── BookControllerIntegrationTest.java      # MockMvc + seguridad + modelo
│   ├── AuthorControllerIntegrationTest.java    # CRUD completo + validación
│   ├── CategoryControllerIntegrationTest.java  # CRUD + flash messages
│   ├── ReviewControllerIntegrationTest.java    # Autorización a nivel de recurso
│   ├── AuthControllerIntegrationTest.java      # Registro: validación, duplicados
│   ├── CatalogRenderingIntegrationTest.java    # Renderizado de vistas
│   └── SecurityIntegrationTest.java            # Reglas de autorización por rol
│
└── selenium/                           # ══ NIVEL 5: Tests E2E con Selenium ══
    ├── BaseSeleniumTest.java           #   Clase base: Chrome headless + @SpringBootTest
    ├── HomeSeleniumTest.java           #   Navegación por página principal
    ├── PublicPagesSeleniumTest.java    #   Páginas públicas sin autenticación
    ├── LoginLogoutSeleniumTest.java    #   Flujo de login/logout
    ├── BookFilterSeleniumTest.java     #   Filtros del catálogo de libros
    ├── AuthenticatedUserSeleniumTest.java  # Favoritos y compras como usuario
    ├── AdminNavigationSeleniumTest.java    # Navegación del panel admin
    └── AdminCrudSeleniumTest.java          # CRUD completo desde el navegador
```

### Tipos de test incluidos

#### Nivel 1 — Tests unitarios sin Mockito (lógica pura)

Tests que no necesitan Spring, ni mocks, ni base de datos. Prueban lógica de negocio pura:

- **`unit/util/`**: `IsbnValidator`, `BookStatistics`, `PriceCalculator` — algoritmos y reglas de negocio reales.
- **`unit/model/`**: entidades JPA — ilustran el uso de las anotaciones de JUnit 6.
- **`unit/service/ReviewServiceLogicTest`**: instancia `new ReviewService(null)` para testar `canModify()` sin mocks.
- **`unit/RegisterFormValidationTest`**: Bean Validation con `Validator` de Jakarta, sin Spring.

Conceptos JUnit practicados: `@Test`, `@Nested`, `@DisplayName`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`,
`@ParameterizedTest` con `@ValueSource`, `@CsvSource`, `@MethodSource`, `@EnumSource`, `@NullAndEmptySource`,
`@RepeatedTest`, `@Timeout`, `@EnabledOnOs`, `assertAll`, `assertThrows`, `assertInstanceOf`, AssertJ.

#### Nivel 2 — Tests unitarios con Mockito

- `AuthorServiceTest`, `BookServiceTest`, `ReviewServiceTest`
- Aíslan la clase bajo test usando mocks de las dependencias
- Patrón: `@ExtendWith(MockitoExtension.class)` + `@Mock` + `@InjectMocks`
- Son rápidos (~ms por test)

#### Nivel 3 — Tests de integración JPA (`@DataJpaTest`)

- Cargan solo la capa JPA (entidades + repositorios)
- Usan BD H2 en memoria con rollback automático
- Verifican que las queries (derived y JPQL) funcionan correctamente
- Practican `@EntityGraph`, `EntityManager` flush/clear, ordenación

#### Nivel 4 — Tests de integración web (MockMvc)

- Cargan el contexto completo y simulan peticiones HTTP
- `@WithMockUser` simula usuarios con diferentes roles
- Verifican status codes, vistas renderizadas, modelo, mensajes flash
- Prueban reglas de seguridad (acceso denegado, redirección a login)

#### Nivel 5 — Tests E2E con Selenium

- Abren un navegador Chrome real (en modo headless)
- Navegan por la aplicación como un usuario real
- Verifican flujos completos: login → navegar → actuar → verificar resultado
- Son los más lentos: usar solo para flujos críticos

### Selenium IDE

El directorio `selenium-ide/` contiene un archivo `biblioteca-tests.side` importable directamente
en [Selenium IDE](https://www.selenium.dev/selenium-ide/) (extensión de Chrome/Firefox).
Incluye 18 tests organizados en 4 suites que prueban la aplicación en ejecución.

---

## Conceptos cubiertos por el proyecto

### Java 25
- Pattern matching con `instanceof` (ej: `if (!(o instanceof Book other))`)
- API de Stream (`.stream()`, `.map()`, `.filter()`, `.toList()`)
- Switch expressions (`switch (cleaned.length()) { case 10 -> ... }`)
- `Optional` para manejo de nulabilidad
- `LocalDate` y `LocalDateTime` (API java.time)

### Spring Boot 4
- Auto-configuración y starters
- Arquitectura en capas: Controller → Service → Repository
- Inyección de dependencias por constructor (`@RequiredArgsConstructor`)
- Configuración con `application.properties`
- `CommandLineRunner` para inicialización de datos
- `@ControllerAdvice` para variables globales en vistas

### Spring Data JPA
- `JpaRepository`: CRUD automático
- Derived Query Methods (consultas generadas por nombre del método)
- `@Query` con JPQL para consultas personalizadas
- `@EntityGraph` para optimizar carga de relaciones
- Relaciones: `@OneToMany`, `@ManyToOne`, `@ManyToMany`
- `@JoinTable` para tablas intermedias
- `FetchType.LAZY` y manejo de `LazyInitializationException`
- `@Transactional` (readOnly, rollback)
- `cascade = ALL` y `orphanRemoval = true`

### Spring Security
- `SecurityFilterChain` con reglas de autorización por URL
- Formulario de login personalizado
- `DelegatingPasswordEncoder` (bcrypt por defecto)
- Roles y autoridades (`hasRole`, `isAuthenticated`, `permitAll`)
- `UserDetailsService` personalizado
- `@AuthenticationPrincipal` en controladores
- Token CSRF en formularios
- Autorización a nivel de recurso (`ReviewService.canModify`)

### Thymeleaf
- Expresiones: `${...}`, `*{...}`, `@{...}`, `~{...}`
- Iteración: `th:each`
- Condicionales: `th:if`, `th:unless`
- Formularios: `th:object`, `th:field`, `th:action`
- Fragmentos: `th:fragment`, `th:replace`
- Integración con Spring Security: `sec:authorize`, `sec:authentication`
- Objetos de utilidad: `#lists`, `#strings`, `#temporals`, `#numbers`, `#fields`
- Validación: `th:errors`, `#fields.hasErrors`

### Bean Validation (JSR 380)
- `@NotBlank`, `@NotNull`, `@Email`, `@Size`, `@Min`, `@Max`, `@PositiveOrZero`
- `@Valid` en parámetros de controlador
- `BindingResult` para errores de validación

### Testing
- **JUnit Jupiter (JUnit 6)**: `@Test`, `@Nested`, `@DisplayName`, `@BeforeEach`, `@AfterEach`,
  `@BeforeAll`, `@AfterAll`, `@ParameterizedTest`, `@RepeatedTest`, `@Timeout`, `@EnabledOnOs`
- **Parametrización**: `@ValueSource`, `@CsvSource`, `@MethodSource`, `@EnumSource`, `@NullAndEmptySource`
- **Aserciones**: `assertEquals`, `assertTrue`, `assertThrows`, `assertAll`, `assertInstanceOf`
- **AssertJ**: `assertThat`, API fluida, `extracting`, `containsKeys`
- **Mockito**: `@Mock`, `@InjectMocks`, `when/thenReturn`, `verify`
- **Spring Boot Test**: `@SpringBootTest`, `@DataJpaTest`, `@AutoConfigureMockMvc`
- **MockMvc**: `get()`, `post()`, `status()`, `view()`, `model()`, `flash()`
- **Spring Security Test**: `@WithMockUser`, `csrf()`
- **Selenium**: `WebDriver`, `ChromeDriver`, `By`, `WebElement`, `WebDriverWait`
- **JUnit Platform Suites**: `@Suite`, `@SelectPackages`
- **JaCoCo**: análisis de cobertura de código
- **Bean Validation Test**: `Validator` sin Spring

### Lombok
- `@Getter`, `@Setter`: genera getters y setters
- `@NoArgsConstructor`, `@AllArgsConstructor`: constructores
- `@Builder`: patrón Builder para crear objetos
- `@Builder.Default`: valores por defecto en el Builder
- `@RequiredArgsConstructor`: constructor con campos `final` (inyección de dependencias)
- `@ToString(exclude = ...)`: evita bucles infinitos en relaciones bidireccionales
- `@Data`: shortcut para DTOs (getters, setters, equals, hashCode, toString)

### Patrones y buenas prácticas
- Arquitectura en capas (Controller → Service → Repository)
- DTO para formularios (`RegisterForm`) vs entidad JPA
- Patrón PRG (Post-Redirect-Get) con `RedirectAttributes`
- `equals/hashCode` manual para entidades JPA (patrón Vlad Mihalcea)
- Autorización a nivel de recurso (no solo por rol, sino por propiedad del objeto)
- Manejo de `LazyInitializationException` con `@Transactional` y `@EntityGraph`
- `spring.jpa.open-in-view=false` (desactivar anti-patrón OSIV)
- Filtros null-safe en servicios (parámetro null → devuelve todo)
- `data-testid` en HTML para localización fiable en tests Selenium

---

## Hoja de ruta

El proyecto se puede explorar progresivamente:

### Semana 1-2: Fundamentos

1. Estructura del proyecto Maven y `pom.xml`
2. `BibliotecaApplication.java` y auto-configuración
3. `application.properties` y configuración
4. Entidades JPA y relaciones (`model/`)
5. Consola H2 para inspeccionar la base de datos
6. **Tests unitarios sin Mockito** (`unit/model/`): primeros pasos con JUnit 6

### Semana 3-4: Acceso a datos

7. `JpaRepository` y operaciones CRUD
8. Derived Query Methods en repositorios
9. `@Query` con JPQL (`BookRepository`)
10. `@EntityGraph` para optimizar carga de relaciones
11. **Tests de repositorio** (`@DataJpaTest`)

### Semana 5-6: Lógica de negocio

12. Servicios (`@Service`, `@Transactional`, inyección por constructor)
13. Clases de utilidad: `IsbnValidator`, `BookStatistics`, `PriceCalculator`
14. Bean Validation (`@NotBlank`, `@Size`, etc.)
15. **Tests unitarios de utilidades** (`unit/util/`): testing de lógica pura sin dependencias
16. **Tests con Mockito** (`service/`): mocks de repositorios

### Semana 7-8: Web MVC + Thymeleaf

17. Controladores (`@Controller`, `@GetMapping`, `@PostMapping`)
18. Thymeleaf básico (variables, iteración, condicionales)
19. Formularios con `th:object` y `th:field`
20. Validación en formularios y `BindingResult`
21. Patrón PRG y mensajes flash
22. **Tests de controlador** (MockMvc)

### Semana 9-10: Seguridad

23. `SecurityConfig` y reglas de autorización
24. Login personalizado y `UserDetailsService`
25. Roles, `@AuthenticationPrincipal` y `sec:authorize`
26. CSRF y protección de formularios
27. **Tests de seguridad** (`@WithMockUser`, `csrf()`)

### Semana 11-12: Testing avanzado y repaso

28. Tests E2E con Selenium (ChromeDriver headless)
29. Selenium IDE (archivo `.side`)
30. JaCoCo: análisis de cobertura
31. JUnit Platform Suites para ejecutar tests agrupados
32. Añadir nueva entidad (ej: Editorial, Préstamo) con toda su pila

---

## Cómo añadir una nueva entidad

Para añadir una entidad nueva se replica la estructura existente. Por ejemplo, para añadir `Publisher` (Editorial):

1. **Modelo**: crear `Publisher.java` en `model/` con `@Entity`, `@Id`, `@GeneratedValue`, `@NotBlank`, etc.
2. **Repositorio**: crear `PublisherRepository.java` en `repository/` extendiendo `JpaRepository<Publisher, Long>`.
3. **Servicio**: crear `PublisherService.java` en `service/` con CRUD + filtros.
4. **Controlador**: crear `PublisherController.java` en `controller/` con list, detail, create, edit, delete.
5. **Vistas**: crear `templates/publisher/publisher-list.html`, `publisher-detail.html`, `publisher-form.html`.
6. **Seguridad**: añadir reglas en `SecurityConfig` si aplica.
7. **Datos de prueba**: añadir editoriales en `DataInitializer`.
8. **Tests**: crear tests en cada nivel (unitario, repositorio, controlador, Selenium).

---

## Proyectos de grupo

Cada grupo desarrolla su propio proyecto replicando los patrones de esta biblioteca.
Las especificaciones de cada grupo están en:

- `PROYECTOS.md` — Guía general de los proyectos en grupo
- `PROYECTOS_GRUPO1.md` — Grupo 1: Restaurante
- `PROYECTOS_GRUPO2.md` — Grupo 2: E-commerce
- `PROYECTOS_GRUPO3.md` — Grupo 3: Cine
- `PROYECTOS_GRUPO4.md` — Grupo 4: Viajes
- `PROYECTOS_GRUPO5.md` — Grupo 5: Reseñas

---

## Documentación de referencia

- [Spring Boot 4](https://docs.spring.io/spring-boot/index.html)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/)
- [Spring Security](https://docs.spring.io/spring-security/reference/)
- [Thymeleaf](https://www.thymeleaf.org/documentation.html)
- [JUnit Jupiter](https://junit.org/junit5/docs/current/user-guide/)
- [Selenium WebDriver](https://www.selenium.dev/documentation/)
- [Lombok](https://projectlombok.org/features/)
- [Bootstrap 5](https://getbootstrap.com/docs/5.3/)
