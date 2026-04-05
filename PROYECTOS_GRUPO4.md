# Grupo 4: Plataforma de Cursos Online

## Qué vas a construir

Tu grupo va a crear una **plataforma de cursos online** similar a Udemy o Coursera, pero en versión simplificada. Los usuarios pueden ver un catálogo de cursos, inscribirse, seguir su progreso y dejar una calificación al terminar. El administrador gestióna cursos y módulos.

Es una aplicación sencilla con operaciones básicas (crear, ver, editar, eliminar) más la lógica de inscripciones y progreso, que te servirá para aprender los patrones fundamentales de Spring Boot paso a paso.

**Alumnos de mañana (Spring Boot):** vosotros construís la aplicación. Crearéis las entidades, los servicios con la lógica de inscripciones y progreso, los controladores web y las páginas HTML.

**Alumnos de tarde (Testing):** vosotros probáis la aplicación. Crearéis tests unitarios para los servicios (especialmente para el cálculo de progreso y la validación de calificaciones), tests de integración y tests E2E con Selenium.

Al final de las 8-10 semanas tendréis un proyecto completo y bien testado que podréis enseñar en entrevistas de trabajo.


## Las 4 entidades de tu proyecto

### OnlineCourse (los cursos)

Representa cada curso que ofrece la plataforma.

```
Campos:
- id: Long (clave primaria, se genera automáticamente)
- title: String (nombre del curso, por ejemplo "Introducción a Java")
- description: String (descripción del contenido del curso)
- instructor: String (nombre del profesor)
- duration: Integer (horas totales estimadas)
- level: String (nivel: "básico", "intermedio" o "avanzado")
- price: BigDecimal (precio del curso; 0 si es gratuito)

Validaciones:
- @NotBlank en title y description
- @Min(1) en duration (al menos 1 hora)
- @Min(0) en price (puede ser gratuito pero no negativo)
```

### CourseModule (los módulos de cada curso)

Representa cada tema o lección dentro de un curso. Un curso se divide en varios módulos ordenados.

```
Campos:
- id: Long (clave primaria)
- courseId: Long (a que curso pertenece)
- title: String (nombre del módulo, por ejemplo "Variables y tipos de datos")
- order: Integer (posición dentro del curso: 1, 2, 3...)
- duration: Integer (horas estimadas de este módulo)

Validaciones:
- @NotBlank en title
- @Min(1) en order y duration
```

### Enrollment (las inscripciones)

Representa que un usuario se ha inscrito en un curso. Almacena su progreso.

```
Campos:
- id: Long (clave primaria)
- courseId: Long (en que curso se inscribió)
- userId: Long (quién se inscribió)
- enrollmentDate: LocalDateTime (cuando se inscribió)
- status: String (estado: "active" mientras lo está haciendo, "completed" cuando lo termina)
- progressPercentage: Integer (progreso del 0 al 100)

Validaciones:
- @Min(0) y @Max(100) en progressPercentage
```

### CourseFeedback (las calificaciones)

Representa la opinión que deja un usuario sobre un curso después de completarlo. Cada inscripción puede tener como máximo una calificación (relación 1:1).

```
Campos:
- id: Long (clave primaria)
- enrollmentId: Long (a que inscripción corresponde, relación 1:1)
- rating: Integer (puntuación de 1 a 5 estrellas)
- comment: String (comentario opciónal)
- feedbackDate: LocalDateTime (cuando se dejó la calificación)

Validaciones:
- @Min(1) y @Max(5) en rating
- Solo puede dejar calificación un usuario que haya completado el curso
```

### Cómo se relacionan las entidades

```
OnlineCourse ---1:N---> CourseModule
(un curso tiene varios módulos ordenados)

OnlineCourse ---1:N---> Enrollment ---1:1---> CourseFeedback
(un curso tiene muchas inscripciones; cada inscripción puede tener una calificación)

User ---1:N---> Enrollment
(un usuario puede inscribirse en varios cursos)
```


## Pantallas que hay que crear

### Pantallas para el usuario (rol USER)

**1. Catálogo de cursos (GET /cursos)**

Muestra todos los cursos disponibles. El usuario puede filtrar por nivel (básico, intermedio, avanzado). De cada curso se ve: título, instructor, precio, duración y nivel. Hay un botón para ver el detalle.

**2. Detalle de un curso (GET /cursos/{id})**

Muestra la descripción completa del curso, la lista de módulos, el instructor y la duración. Si el usuario ya está inscrito, aparece un botón "Ir al curso". Si no, aparece un botón "Inscribirse".

**3. Mi curso (GET /mis-cursos/{id})**

Muestra el curso en el que el usuario está inscrito: lista de módulos y una barra de progreso con el porcentaje completado. Si el progreso llega al 100%, aparece un botón "Calificar curso".

**4. Mis cursos (GET /mis-cursos)**

Lista todos los cursos en los que el usuario está inscrito (activos y completados) con el progreso de cada uno. Botónes para continuar un curso o ver la calificación que dejo.

**5. Dejar calificación (GET/POST /cursos/{id}/feedback)**

Formulario con un selector de puntuación (1 a 5 estrellas) y un campo de texto para el comentario. Solo aparece si el usuario ha completado el curso.

### Pantallas para el administrador (rol ADMIN)

**6. Gestión de cursos (GET/POST /admin/cursos)**

CRUD de cursos: crear, editar, eliminar. Ver cuántos usuarios estan inscritos en cada curso y estadísticas básicas (calificación media, número de completados).

**7. Gestión de módulos (GET/POST /admin/cursos/{id}/módulos)**

CRUD de módulos dentro de un curso: crear, editar, cambiar orden, eliminar.


## Plan semana por semana

### Semanas 1-2: Crear las entidades (la base de datos)

**Qué hacen los de mañana (Spring):**

- Crear la clase `OnlineCourse` dentro de `model/` con anotaciones `@Entity`, `@Table`, Lombok y validaciones.
- Crear `CourseModule` con su relación `@ManyToOne` hacia OnlineCourse y el campo de orden.
- Crear `Enrollment` con relaciones hacia OnlineCourse y User, y el campo progressPercentage.
- Crear `CourseFeedback` con su relación `@OneToOne` hacia Enrollment y validación de rating (1-5).
- Verificar que compila: `mvn clean compile`.

**Qué hacen los de tarde (Testing):**

- Tests unitarios básicos para las entidades: crear instancias, verificar validaciones.
- Familiarizarse con los tests del proyecto base.

**Ejemplo de commit:**
```
feat(cursos): crear entidades OnlineCourse, CourseModule, Enrollment, CourseFeedback

- Crear OnlineCourse con campos title, description, instructor, level, price
- Crear CourseModule con campo order para posición dentro del curso
- Crear Enrollment con progressPercentage (0-100)
- Crear CourseFeedback con rating (1-5) y relación OneToOne a Enrollment
```

---

### Semanas 3-4: Repositorios y servicios (acceso a datos y lógica)

La lógica más interesante de este proyecto es el sistema de inscripciones y el cálculo de progreso.

**Qué hacen los de mañana (Spring):**

- Crear `OnlineCourseRepository` con `findByLevel(String level)` para filtrar por nivel.
- Crear `EnrollmentRepository` con queries:
  - `findByUserId(Long userId)` para ver los cursos de un usuario
  - `findByCourseId(Long courseId)` para ver quién está inscrito en un curso
- Crear `CourseFeedbackRepository` con queries para obtener las calificaciones de un curso.
- Crear `EnrollmentService` con la lógica principal:
  - `enrollUser(Long courseId, User user)` -- crea una inscripción nueva con progreso 0% y estado "active"
  - `updateProgress(Long enrollmentId, Integer percentage)` -- actualiza el porcentaje. Si llega a 100, cambia el estado a "completed"
  - `completeEnrollment(Long enrollmentId)` -- marca la inscripción como completada
  - `findUserCourses(Long userId)` -- devuelve todas las inscripciones de un usuario

**Qué hacen los de tarde (Testing):**

- Test con `@DataJpaTest` para `OnlineCourseRepository`: verificar que findByLevel filtra correctamente.
- Test unitario con Mockito para `EnrollmentService`: verificar que enrollUser crea la inscripción con progreso 0, que updateProgress cambia el estado a "completed" cuando llega a 100, y que no se puede poner un progreso mayor que 100.

**Código de referencia (mañana):**

```java
// src/main/java/com/certidevs/service/EnrollmentService.java
@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final OnlineCourseRepository courseRepository;

    public Enrollment enrollUser(Long courseId, User user) {
        OnlineCourse course = courseRepository.findById(courseId)
            .orElseThrow();

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setUser(user);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus("active");
        enrollment.setProgressPercentage(0);

        return enrollmentRepository.save(enrollment);
    }

    public void updateProgress(Long enrollmentId, Integer percentage) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow();

        if (percentage >= 100) {
            enrollment.setStatus("completed");
        }

        enrollment.setProgressPercentage(Math.min(percentage, 100));
        enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> findUserCourses(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }
}
```

**Ejemplo de commit:**
```
feat(cursos): crear repositorios y servicio de inscripciones con progreso

- Crear OnlineCourseRepository con findByLevel
- Crear EnrollmentService con enrollUser, updateProgress, findUserCourses
- updateProgress marca como completado al llegar a 100%
```

---

### Semanas 5-6: Controladores y vistas (la parte web)

**Qué hacen los de mañana (Spring):**

- Crear `OnlineCourseController` con rutas para el catálogo y detalle de cursos.
- Crear `EnrollmentController` con rutas para inscribirse, ver mis cursos y actualizar progreso.
- Crear `CourseFeedbackController` con ruta para dejar calificación y ver calificaciones.
- Crear las vistas Thymeleaf:
  - `templates/cursos/catálogo.html` -- rejilla de cursos con filtro por nivel
  - `templates/cursos/detail.html` -- detalle con módulos y botón de inscripción
  - `templates/cursos/my-course.html` -- vista del curso con barra de progreso
  - `templates/cursos/my-courses.html` -- todos mis cursos con progreso
  - `templates/cursos/feedback-form.html` -- formulario de calificación

**Qué hacen los de tarde (Testing):**

- Tests con MockMvc para los controladores: catálogo, detalle, inscripción, feedback.

**Vista Thymeleaf de referencia (cursos/my-course.html):**

```html
<div class="container">
    <h1 th:text="${enrollment.course.title}"></h1>

    <div class="progress mb-4">
        <div class="progress-bar" role="progressbar"
             th:style="'width: ' + ${enrollment.progressPercentage} + '%'"
             th:text="${enrollment.progressPercentage} + '%'"></div>
    </div>

    <div class="modules">
        <h3>Modulos</h3>
        <ul th:each="module : ${enrollment.course.modules}">
            <li th:text="${module.title}"></li>
        </ul>
    </div>

    <a th:if="${enrollment.status == 'completed'}"
       th:href="@{/cursos/{id}/feedback(id=${enrollment.course.id})}"
       class="btn btn-primary">Dejar calificación</a>
</div>
```

**Ejemplo de commit:**
```
feat(cursos): crear controladores y vistas de catálogo e inscripciones

- Crear OnlineCourseController con catálogo y detalle
- Crear EnrollmentController con inscripción y progreso
- Crear vistas catálogo, detail, my-course con barra de progreso
```

---

### Semanas 7-8: Seguridad (proteger la aplicación)

**Qué hacen los de mañana (Spring):**

- Modificar `SecurityConfig`:
  - `/cursos` y `/cursos/{id}` -- acceso público (cualquiera puede ver el catálogo)
  - `/mis-cursos/**` y `/cursos/*/feedback` -- solo USER
  - `/admin/cursos/**` -- solo ADMIN
- Verificar que solo un usuario que ha completado el curso puede dejar calificación
- Verificar que solo el autor de una calificación puede editarla

**Qué hacen los de tarde (Testing):**

- Tests de seguridad con `@WithMockUser`
- Test de que un usuario sin inscripción completada no puede acceder al formulario de feedback

**Ejemplo de commit:**
```
feat(cursos): proteger rutas y validar que solo completados pueden calificar

- Catálogo público, inscripciones y feedback solo para USER, admin para ADMIN
- Validar que el usuario ha completado el curso antes de dejar feedback
- Tests de autorización con @WithMockUser
```

---

### Semanas 9-10: Calidad, tests E2E y finalización

**Qué hacen los de mañana (Spring):**

- Revisar y limpiar código, mejorar vistas, reutilizar fragments.

**Qué hacen los de tarde (Testing):**

- Test E2E: ver catálogo -> inscribirse -> ver progreso -> completar -> dejar calificación
- Tests de validación de rating (comprobar que no se aceptan valores fuera de 1-5)
- Alcanzar cobertura del 80% o más

**Ejemplo de commit:**
```
test(cursos): crear tests E2E de flujo de inscripción con Selenium

- Test de flujo completo: catálogo, inscripción, progreso, calificación
- Tests de validación de rating (1-5)
- Cobertura de tests al 80%
```


## Checklist semanal

```
SEMANA ___ -- GRUPO 4 CURSOS ONLINE

DESARROLLO (mañana)
[ ] Entidades creadas: OnlineCourse, CourseModule, Enrollment, CourseFeedback
[ ] Repositorios con queries de búsqueda por nivel
[ ] Servicios con enrollUser, updateProgress, completeEnrollment
[ ] Controladores para catálogo, inscripción, mis cursos, feedback
[ ] Vistas funcionales en el navegador
[ ] Cada persona tiene al menos 1 commit esta semana
[ ] El proyecto compila: mvn clean compile

TESTING (tarde)
[ ] Tests unitarios de updateProgress con Mockito
[ ] Tests de validación de rating (1-5)
[ ] Tests MockMvc para inscripción y feedback
[ ] Test de que solo un usuario que ha completado puede calificar
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
Que puede hacer: crear y editar cursos, gestiónar módulos, ver estadísticas

Usuario: user / Contraseña: user / Rol: USER
Que puede hacer: ver catálogo, inscribirse, seguir progreso, calificar cursos
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

**Cómo funciona la barra de progreso?**
El campo `progressPercentage` va de 0 a 100. En la vista Thymeleaf se usa como ancho del div de la barra (width: 50%). Cada vez que el usuario avanza en un módulo, se llama a `updateProgress` con el nuevo porcentaje.

**Qué pasa cuando el progreso llega a 100?**
El servicio cambia automáticamente el estado de la inscripción a "completed". A partir de ese momento el usuario puede dejar una calificación.

**Puedo añadir más niveles de curso?**
Sí, los niveles son Strings. Puedes usar los que quieras. Si prefieres, usa un enum.

**Qué hago si no me da tiempo a terminar una semana?**
No pasa nada. Avisa al profesor. Lo importante es que entiendas lo que estás haciendo, no que vayas rápido.
