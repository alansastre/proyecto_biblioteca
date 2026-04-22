# Grupo 2: Sistema de Gestión de Biblioteca de Materiales

## Qué vas a construir

Tu grupo va a crear una **aplicación web para gestiónar una biblioteca de materiales**: libros, DVDs y periódicos. Piensa en el sistema que usa una biblioteca real para controlar que materiales tiene, quién los ha sacado en préstamo, cuando tienen que devolverlos y quién tiene reservas pendientes.

Es una aplicación sencilla con operaciones básicas (crear, ver, editar, eliminar) más una lógica de préstamos y devoluciones que te servirá para aprender los patrones fundamentales de Spring Boot paso a paso.

**Alumnos de mañana (Spring Boot):** vosotros construís la aplicación. Crearéis las entidades de base de datos, los servicios con la lógica de préstamos y devoluciones, los controladores web y las páginas HTML.

**Alumnos de tarde (Testing):** vosotros probáis la aplicación. Crearéis tests unitarios para los servicios (especialmente para las fechas y disponibilidad), tests de integración para los repositorios y controladores, y tests E2E con Selenium.

Al final de las 8-10 semanas tendréis un proyecto completo y bien testado que podréis enseñar en entrevistas de trabajo.


## Las 4 entidades de tu proyecto

Tu proyecto tiene 4 entidades principales. Cada entidad es una clase Java que representa una tabla en la base de datos.

### MaterialType (tipos de material)

Representa las categorías de material que gestióna la biblioteca. Cada tipo tiene un periodo de préstamo diferente.

```
Campos:
- id: Long (clave primaria, se genera automáticamente)
- name: String (nombre del tipo: "libro", "dvd", "periódico")
- maxLoanDays: Integer (cuántos días se puede prestar este tipo de material)

Validaciones:
- @NotBlank(message = "El tipo es obligatorio") en name
- @Min(1) en maxLoanDays (al menos 1 dia de préstamo)
```

### Material (los materiales de la biblioteca)

Representa cada material concreto que tiene la biblioteca.

```
Campos:
- id: Long (clave primaria)
- title: String (título del material, por ejemplo "El Quijote")
- author: String (autor o director)
- year: Integer (año de públicacion)
- copies: Integer (cuántas copias hay disponibles ahora mismo)
- typeId: Long (a que tipo de material pertenece)

Validaciones:
- @NotBlank en title y author
- @Min(0) en copies (no puede haber copias negativas)
```

### MaterialLoan (los préstamos)

Representa un préstamo: un usuario se ha llevado un material y tiene que devolverlo antes de una fecha.

```
Campos:
- id: Long (clave primaria)
- materialId: Long (que material se ha prestado)
- userId: Long (quién se lo ha llevado)
- startDate: LocalDateTime (cuando empezó el préstamo)
- dueDate: LocalDateTime (fecha límite para devolver)
- returnDate: LocalDateTime (cuando se devolvió realmente; null si aún no se ha devuelto)
- status: String (estado: "active" si está en préstamo, "returned" si ya se devolvió, "overdue" si está vencido)

Validaciones:
- @NotNull en materialId y userId
```

### MaterialReservation (las reservas)

Representa una reserva: un usuario quiere un material que no tiene copias disponibles y se pone en espera.

```
Campos:
- id: Long (clave primaria)
- materialId: Long (que material quiere reservar)
- userId: Long (quién hace la reserva)
- reservationDate: LocalDateTime (cuando se hizo la reserva)
- pickupDate: LocalDateTime (cuando podrá recoger el material)
- status: String (estado: "pending" si está en espera, "ready" si ya puede recogerlo, "cancelled" si se canceló)
```

### Cómo se relacionan las entidades

```
MaterialType ---1:N---> Material ---1:N---> MaterialLoan
                           |
                           +---1:N---> MaterialReservation

User ---1:N---> MaterialLoan
User ---1:N---> MaterialReservation
```

Un tipo de material puede tener muchos materiales. Un material puede tener muchos préstamos (a lo largo del tiempo) y muchas reservas. Un usuario puede tener varios préstamos y varias reservas.


## Pantallas que hay que crear

### Pantallas para el usuario (rol USER)

**1. Búsqueda de materiales (GET /materiales)**

El usuario ve todos los materiales de la biblioteca. Puede filtrar por tipo (libro, dvd, periódico) y buscar por título o autor. Cada material muestra si tiene copias disponibles o no.

**2. Detalle de un material (GET /materiales/{id})**

Al hacer clic en un material se ve su información completa: título, autor, año, copias disponibles. Si hay copias, aparece un botón "Solicitar préstamo". Si no hay copias, aparece un botón "Reservar".

**3. Mis préstamos (GET /préstamos/mis-préstamos)**

Tabla con los materiales que el usuario tiene actualmente en préstamo. Muestra la fecha límite de devolución, cuántos días le quedan (en rojo si está vencido) y botónes para "Devolver" o "Renovar" (si es posible).

**4. Mis reservas (GET /reservas/mis-reservas)**

Tabla con los materiales que el usuario ha reservado. Muestra el estado de cada reserva (en espera o lista para recoger) y un botón para cancelar.

### Pantallas para el administrador (rol ADMIN)

**5. Gestión de tipos de material (GET /admin/tipos)**

CRUD de tipos de material (libro, dvd, periódico). Permite cambiar los días máximos de préstamo de cada tipo.

**6. Gestión de materiales (GET /admin/materiales)**

Lista todos los materiales. Permite crear nuevos, editar los existentes y ver cuántas copias hay disponibles de cada uno.

**7. Gestión de préstamos (GET /admin/préstamos)**

Lista todos los préstamos activos. Permite registrar devoluciones y ver que préstamos están vencidos.

**8. Gestión de reservas (GET /admin/reservas)**

Lista las reservas pendientes. Cuando un material se devuelve y hay reservas, el admin puede marcar la reserva como "ready" para avisar al usuario de que ya puede pasar a recogerlo.


## Plan semana por semana

### Semanas 1-2: Crear las entidades (la base de datos)

En esta fase creas las clases Java que representan las tablas.

**Qué hacen los de mañana (Spring):**

- Crear la clase `MaterialType` dentro de `model/` con anotaciones `@Entity`, `@Table`, Lombok y las validaciones descritas arriba.
- Crear la clase `Material` con su relación `@ManyToOne` hacia MaterialType.
- Crear la clase `MaterialLoan` con relaciones hacia Material y User.
- Crear la clase `MaterialReservation` con relaciones hacia Material y User.
- Verificar que compila: `mvn clean compile`.

**Qué hacen los de tarde (Testing):**

- Escribir tests unitarios básicos para las entidades: crear instancias, verificar getters/setters, comprobar que los valores por defecto son correctos.
- Familiarizarse con la estructura de tests del proyecto base.

**Ejemplo de commit:**
```
feat(materiales): crear entidades MaterialType, Material, MaterialLoan, MaterialReservation

- Crear MaterialType con campo maxLoanDays
- Crear Material con relación ManyToOne a MaterialType
- Crear MaterialLoan con campos de fechas y estado
- Crear MaterialReservation con estados pending/ready/cancelled
```

---

### Semanas 3-4: Repositorios y servicios (acceso a datos y lógica)

En esta fase creas las interfaces de acceso a datos y la lógica de préstamos y devoluciones. Esta es la parte más interesante del proyecto porque la lógica de préstamos tiene varias reglas que hay que implementar.

**Qué hacen los de mañana (Spring):**

- Crear `MaterialTypeRepository` básico (solo extiende JpaRepository).
- Crear `MaterialRepository` con queries personalizadas:
  - `findByType(MaterialType type)` para filtrar por tipo
  - `findByTitleIgnoreCaseContaining(String title)` para buscar por título
  - Una query que devuelva solo materiales con copias disponibles (copies > 0)
- Crear `MaterialLoanRepository` con queries:
  - `findByUserId(Long userId)` para obtener los préstamos de un usuario
  - `findByStatusAndDueDateBefore(String status, LocalDateTime date)` para encontrar préstamos vencidos
- Crear `MaterialLoanService` con la lógica de préstamos. Este servicio es el más importante del proyecto y debe tener estos métodos:
  - `createLoan(Material material, User user)` -- crea un préstamo nuevo, resta 1 copia al material
  - `renewLoan(Long loanId)` -- extiende la fecha de devolución 14 días más
  - `returnMaterial(Long loanId)` -- registra la devolución, suma 1 copia al material
  - `findOverdueLoans()` -- devuelve todos los préstamos vencidos

**Qué hacen los de tarde (Testing):**

- Test con `@DataJpaTest` para `MaterialRepository`: verificar que las queries de búsqueda devuelven resultados correctos.
- Test unitario con Mockito para `MaterialLoanService`: verificar que al crear un préstamo se descuenta una copia, que al devolver se suma una copia, y que no se puede prestar si no hay copias.

**Código de referencia (mañana):**

```java
// src/main/java/com/certidevs/service/MaterialLoanService.java
@Service
@RequiredArgsConstructor
@Transactional
public class MaterialLoanService {
    private final MaterialLoanRepository loanRepository;
    private final MaterialRepository materialRepository;

    public MaterialLoan createLoan(Material material, User user) {
        if (material.getCopies() <= 0) {
            throw new IllegalStateException("Sin copias disponibles");
        }

        MaterialLoan loan = new MaterialLoan();
        loan.setMaterial(material);
        loan.setUser(user);
        loan.setStartDate(LocalDateTime.now());
        loan.setDueDate(LocalDateTime.now().plusDays(14));
        loan.setStatus("active");

        material.setCopies(material.getCopies() - 1);
        materialRepository.save(material);

        return loanRepository.save(loan);
    }

    public void renewLoan(Long loanId) {
        MaterialLoan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new EntityNotFoundException("Préstamo no encontrado"));

        loan.setDueDate(loan.getDueDate().plusDays(14));
        loanRepository.save(loan);
    }

    public void returnMaterial(Long loanId) {
        MaterialLoan loan = loanRepository.findById(loanId)
            .orElseThrow();

        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus("returned");
        loanRepository.save(loan);

        Material material = loan.getMaterial();
        material.setCopies(material.getCopies() + 1);
        materialRepository.save(material);
    }

    public List<MaterialLoan> findOverdueLoans() {
        return loanRepository.findByStatusAndDueDateBefore("active", LocalDateTime.now());
    }
}
```

**Ejemplo de commit:**
```
feat(materiales): crear repositorios y servicio de préstamos

- Crear MaterialRepository con queries de búsqueda y disponibilidad
- Crear MaterialLoanService con createLoan, renewLoan, returnMaterial
- La lógica de createLoan descuenta copias y returnMaterial las recupera
```

---

### Semanas 5-6: Controladores y vistas (la parte web)

En esta fase conectas los servicios con páginas web.

**Qué hacen los de mañana (Spring):**

- Crear `MaterialController` con rutas para búsqueda y detalle de materiales.
- Crear `MaterialLoanController` con rutas para ver mis préstamos, solicitar préstamo, renovar y devolver.
- Crear `MaterialReservationController` con rutas para ver mis reservas y cancelar.
- Crear las vistas Thymeleaf:
  - `templates/material/list.html` -- listado con filtros
  - `templates/material/detail.html` -- detalle con botón de préstamo
  - `templates/prestamo/my-loans.html` -- mis préstamos con días restantes
  - `templates/reserva/my-reservations.html` -- mis reservas

**Qué hacen los de tarde (Testing):**

- Tests con MockMvc para los controladores: verificar que las rutas devuelven código 200, que los datos del modelo son correctos y que los formularios funcionan.

**Vista Thymeleaf de referencia (prestamo/my-loans.html):**

```html
<table class="table">
    <thead>
        <tr>
            <th>Título</th>
            <th>Devolucion prevista</th>
            <th>Dias restantes</th>
            <th>Estado</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="loan : ${myLoans}"
            th:classappend="${loan.daysRemaining < 0 ? 'table-danger' : ''}">
            <td th:text="${loan.material.title}"></td>
            <td th:text="${#temporals.format(loan.dueDate, 'dd/MM/yyyy')}"></td>
            <td th:text="${loan.daysRemaining}"
                th:classappend="${loan.daysRemaining < 0 ? 'text-danger' : 'text-success'}"></td>
            <td th:text="${loan.status}"></td>
            <td>
                <form method="post" action="/préstamos/devolver" style="display:inline;">
                    <input type="hidden" name="loanId" th:value="${loan.id}">
                    <button class="btn btn-sm btn-success">Devolver</button>
                </form>
                <form method="post" action="/préstamos/renovar" style="display:inline;"
                      th:if="${loan.renewalsRemaining > 0}">
                    <input type="hidden" name="loanId" th:value="${loan.id}">
                    <button class="btn btn-sm btn-warning">Renovar</button>
                </form>
            </td>
        </tr>
    </tbody>
</table>
```

**Ejemplo de commit:**
```
feat(materiales): crear controladores y vistas de préstamos y reservas

- Crear MaterialController con búsqueda y detalle
- Crear MaterialLoanController con mis préstamos, renovar y devolver
- Crear vistas con tabla de días restantes y acciones
```

---

### Semanas 7-8: Seguridad (proteger la aplicación)

**Qué hacen los de mañana (Spring):**

- Modificar `SecurityConfig` para proteger las rutas:
  - `/materiales` -- acceso público
  - `/préstamos/**` y `/reservas/**` -- solo USER
  - `/admin/**` -- solo ADMIN
- Mostrar el botón "Renovar" solo si al préstamo le quedan renovacíones disponibles
- Calcular y mostrar los "días restantes" correctamente en las vistas

**Qué hacen los de tarde (Testing):**

- Tests de seguridad con `@WithMockUser`
- Tests de autorización: verificar que solo ADMIN puede registrar devoluciones desde el panel

**Ejemplo de commit:**
```
feat(materiales): proteger rutas por rol y mostrar días restantes

- Configurar SecurityConfig para /préstamos (USER) y /admin (ADMIN)
- Calcular días restantes en la vista de mis préstamos
- Tests de seguridad con @WithMockUser
```

---

### Semanas 9-10: Calidad, tests E2E y finalización

**Qué hacen los de mañana (Spring):**

- Revisar y limpiar el código: eliminar duplicaciones, mejorar nombres
- Mejorar las vistas: reutilizar fragments, añadir mensajes de confirmación

**Qué hacen los de tarde (Testing):**

- Test E2E con Selenium: buscar material -> solicitar préstamo -> ver mis préstamos -> renovar -> devolver
- Test E2E de admin: registrar devolución, ver préstamos vencidos
- Alcanzar cobertura del 80% o más

**Ejemplo de commit:**
```
test(materiales): crear tests E2E del flujo de préstamos con Selenium

- Test de flujo completo de usuario: buscar, prestar, renovar, devolver
- Test de flujo de admin: gestiónar préstamos y devoluciones
- Cobertura de tests al 80%
```


## Checklist semanal

Cada viernes, comprueba que tu grupo cumple con estos puntos:

```
SEMANA ___ -- GRUPO 2 BIBLIOTECA MATERIALES

DESARROLLO (mañana)
[ ] Entidades creadas: MaterialType, Material, MaterialLoan, MaterialReservation
[ ] Repositorios con queries de búsqueda y disponibilidad
[ ] Servicios con lógica de préstamos: createLoan, renewLoan, returnMaterial
[ ] Controladores para búsqueda y gestión de préstamos
[ ] Vistas funcionales en el navegador
[ ] Cada persona tiene al menos 1 commit esta semana
[ ] El proyecto compila: mvn clean compile

TESTING (tarde)
[ ] Tests unitarios de MaterialLoanService con Mockito
[ ] Tests de repositorio con @DataJpaTest
[ ] Tests de controladores con MockMvc
[ ] Tests de seguridad con @WithMockUser
[ ] La cobertura es al menos del 60%
[ ] Todos los tests pasan: mvn test
[ ] Cada persona tiene al menos 1 commit esta semana

GENERAL
[ ] Sin conflictos de Git pendientes
[ ] Presentación al grupo (5 minutos)
```


## Usuarios de prueba

```
Usuario: admin
Contraseña: admin
Rol: ADMIN
Que puede hacer: gestiónar tipos de material, materiales, préstamos y reservas

Usuario: user
Contraseña: user
Rol: USER
Que puede hacer: buscar materiales, solicitar préstamos, renovar, devolver
```

Entra en `http://localhost:8080/login` para probar.


## Comandos básicos para el día a día

```bash
./mvnw clean compile          # Compilar (verificar que no hay errores)
./mvnw spring-boot:run        # Arrancar el servidor
./mvnw test                   # Ejecutar todos los tests
./mvnw test -Dtest="*ServiceTest"   # Solo tests de servicios
./mvnw test jacoco:report     # Generar informe de cobertura
```


## Preguntas frecuentes

**Puedo crear más entidades?**
Sí, pero antes coméntalo con el profesor. Es mejor terminar bien las 4 entidades principales antes de añadir más.

**¿Cómo calculo los "días restantes" de un préstamo?**
Resta la fecha actual a la fecha de devolución (dueDate). Si el resultado es negativo, el préstamo está vencido. Esto se puede hacer con `ChronoUnit.DAYS.between(LocalDate.now(), loan.getDueDate().toLocalDate())`.

**Qué pasa si un usuario intenta prestar un material sin copias?**
El servicio debe lanzar una excepción (`IllegalStateException`) y el controlador debe mostrar un mensaje de error en la vista.

**Qué hago si no me da tiempo a terminar una semana?**
No pasa nada. Avisa al profesor. Lo importante es que entiendas lo que estás haciendo, no que vayas rápido.
