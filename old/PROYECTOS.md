# Proyectos en Grupo -- Cursos Paralelos Java Spring y Testing

## Bienvenida

Este documento te explica cómo funcionan los proyectos en grupo durante el curso. Léelo con calma antes de empezar a programar.

Estamos en **dos cursos paralelos de 230 horas cada uno** que comparten el mismo proyecto:

- **Curso de mañana (Java Spring Boot):** te encargas de desarrollar la aplicación. Creas entidades, repositorios, servicios, controladores y vistas HTML.
- **Curso de tarde (Testing):** te encargas de probar la aplicación. Creas tests unitarios, de integración y tests E2E con Selenium.

Ambos cursos trabajan sobre el **mismo repositorio de código**, así que lo que los alumnos de mañana programan por la mañana, los alumnos de tarde lo testean por la tarde. El resultado final es una aplicación web completa y bien testada.


## Qué vas a aprender

Al final de las 8-10 semanas del proyecto habrás aprendido a:

- Crear una aplicación web completa con Java y Spring Boot desde cero
- Diseñar entidades de base de datos con JPA y validaciones
- Escribir queries personalizadas para buscar datos
- Construir páginas web con Thymeleaf y Bootstrap
- Proteger tu aplicación con Spring Security (login, roles, permisos)
- Escribir tests automáticos (unitarios, integración y E2E)
- Trabajar en equipo con Git (commits, push, pull, resolver conflictos)

No necesitas experiencia previa con Spring Boot. Cada concepto se explica en clase antes de que tengas que aplicarlo en tu proyecto.


## Cómo funciona: la dinámica diaria

Cada día de clase sigue este patrón. El profesor guía todo el proceso:

### Paso 1 -- Explicación teórica (20 minutos)

El profesor explica un concepto nuevo. Por ejemplo: "Cómo crear una entidad JPA con validaciones" o "Cómo hacer un formulario con Thymeleaf". Se muestra en la pizarra o presentación qué es y por qué se usa.

### Paso 2 -- Demostración en el proyecto Biblioteca (10 minutos)

El profesor programa en directo sobre el proyecto Biblioteca (este proyecto base). Ves en pantalla como se aplica el concepto que acaba de explicar: donde va el código, que anotaciones se usan, como se prueba.

### Paso 3 -- El profesor sube el cambio (5 minutos)

El profesor hace commit y push del cambio al repositorio de Biblioteca. Asi todos podeis ver el ejemplo resuelto.

### Paso 4 -- Tu turno: replicar en tu proyecto (75 minutos)

Ahora te toca a ti. Haces `git pull` para tener el ejemplo del profesor y luego aplicas el mismo concepto en tu propio proyecto (restaurante, cine, cursos, etc.):

- **Sí eres de mañana (Spring):** creas o modificas entidades, servicios, controladores o vistas en tu dominio.
- **Sí eres de tarde (Testing):** creas tests para el código que los compañeros de mañana han programado.

El profesor pasa por los grupos resolviendo dudas y validando que todo el mundo avanza.

### Paso 5 -- Revisión en grupo (10 minutos)

2 o 3 alumnos muestran su código en pantalla grande. El profesor y los compañeros dan feedback constructivo.

### Paso 6 -- Profundización o siguiente concepto (60 minutos)

Si hay tiempo, el profesor propone un ejercicio adicional o empieza con el siguiente tema.


## Los 5 proyectos

Hay 5 dominios diferentes, uno por grupo. Todos tienen la misma estructura y dificultad, solo cambia la temática:

| Grupo | Dominio | Que hace la aplicación |
|-------|---------|------------------------|
| 1 | Restaurante | Gestión de carta, mesas y pedidos de un restaurante |
| 2 | Biblioteca de Materiales | Préstamos y reservas de libros, DVDs y periódicos |
| 3 | Cine | Cartelera de películas, sesiónes y compra de entradas |
| 4 | Cursos Online | Plataforma de formación con inscripciones y progreso |
| 5 | Eventos | Venta de entradas para conciertos y conferencias |

Todos son proyectos de **dificultad sencilla**: operaciones CRUD básicas (crear, leer, actualizar, eliminar), flujos intuitivos y lógica de negocio simple. El objetivo es aprender los patrones de Spring Boot, no resolver problemas de negocio complicados.

Lee el documento específico de tu grupo para ver tus entidades, pantallas y plan detallado:

- [Grupo 1: Restaurante](PROYECTOS_GRUPO1.md)
- [Grupo 2: Biblioteca de Materiales](PROYECTOS_GRUPO2.md)
- [Grupo 3: Cine](PROYECTOS_GRUPO3.md)
- [Grupo 4: Cursos Online](PROYECTOS_GRUPO4.md)
- [Grupo 5: Eventos](PROYECTOS_GRUPO5.md)


## Estructura del código

Cada grupo tiene su propio repositorio. Todos siguen la misma organizacion de carpetas. Es importante que sepas donde va cada cosa:

```
src/main/java/com/certidevs/
  config/          Configuración de seguridad y datos iniciales.
                   Apenas tendrás que tocar esta carpeta.

  model/           Tus entidades (clases Java que representan tablas en base de datos).
                   Aquí defines los campos, validaciones y relaciones.

  repository/      Interfaces que permiten leer y guardar datos en base de datos.
                   Aquí defines queries personalizadas (buscar por nombre, por estado, etc.).

  service/         Clases con la lógica de negocio.
                   Aqui va el "que hacer" con los datos (crear pedido, calcular total, etc.).

  controller/      Clases que conectan las peticiones HTTP con los servicios.
                   Aquí defines las rutas (GET /menu, POST /pedidos, etc.)
                   y preparas los datos para las vistas.

src/main/resources/
  templates/       Vistas HTML con Thymeleaf. Lo que ve el usuario en el navegador.
  static/          Archivos CSS, JavaScript e imagenes.

src/test/java/com/certidevs/
  service/         Tests unitarios de los servicios (con Mockito).
  controller/      Tests de integración de los controladores (con MockMvc).
  repository/      Tests de los repositorios (con @DataJpaTest).
  selenium/        Tests E2E que simulan un usuario real en el navegador.
```

**Importante:** los alumnos de mañana trabajan en `src/main/` y los de tarde en `src/test/`. Al estar en carpetas distintas, no hay conflictos de Git entre los dos turnos.


## Plan de 8-10 semanas

La dificultad crece poco a poco. Cada fase construye sobre la anterior:

| Semanas | Tema principal | Que hacen los de mañana (Spring) | Que hacen los de tarde (Testing) |
|---------|----------------|----------------------------------|----------------------------------|
| 1-2 | Modelos de datos | Crear entidades JPA con campos, validaciones y Lombok | Escribir tests unitarios básicos con Mockito |
| 3-4 | Acceso a datos | Crear repositorios con queries personalizadas y servicios con lógica CRUD | Escribir tests de repositorio con @DataJpaTest |
| 5-6 | Web y vistas | Crear controladores (GET/POST), formularios y vistas Thymeleaf | Escribir tests de controladores con MockMvc |
| 7-8 | Seguridad | Configurar Spring Security, proteger rutas por rol (USER/ADMIN) | Escribir tests de seguridad con @WithMockUser |
| 9-10 | Calidad y E2E | Refactorizar código, mejorar vistas, optimizar | Escribir tests E2E con Selenium, alcanzar cobertura alta |

No te preocupes si al principio todo es nuevo. Las semanas 1-2 son las más sencillas y el profesor explica cada paso. Cada semana aprendes algo nuevo que se apoya en lo que ya sabes.


## Repositorios de los grupos

Se crean 5 repositorios en GitHub, uno por grupo:

```
grupo-1-restaurante
grupo-2-materiales
grupo-3-cine
grupo-4-cursos
grupo-5-eventos
```

Cada repositorio parte de una copia del proyecto Biblioteca. Los colaboradores del grupo (alumnos de mañana y de tarde) tienen acceso al mismo repositorio.


## Usuarios de prueba

Todos los repositorios vienen con dos usuarios ya creados en el archivo `DataInitializer.java`. Los puedes usar para probar la aplicación en el navegador:

| Usuario | Contraseña | Rol | Que puede hacer |
|---------|------------|-----|-----------------|
| admin | admin | ADMIN | Crear, editar y eliminar cualquier dato. Gestionar usuarios. |
| user | user | USER | Ver datos, buscar, crear sus propios registros. |

Para probar, abre `http://localhost:8080/login` en tu navegador y entra con uno de estos usuarios.


## Commits: como guardar tu trabajo

Después de cada funcionalidad que completes, guarda tu trabajo con un commit. Un buen commit tiene un mensaje claro que explica que has hecho:

```bash
git add src/main/java/com/certidevs/model/RestaurantMenu.java
git add src/main/java/com/certidevs/repository/RestaurantMenuRepository.java
git commit -m "feat(restaurante): crear entidad RestaurantMenu y su repositorio

- Crear entidad RestaurantMenu con campos name, price, category
- Agregar validaciones @NotBlank y @Min
- Crear RestaurantMenuRepository con query findByCategory"
```

**Reglas básicas para commits:**

- Haz un commit por cada funcionalidad. No acumules cambios de varias pantallas o entidades en el mismo commit.
- Escribe el mensaje en imperativo: "crear", "agregar", "corregir" (no "creado" o "cree").
- Describe que cambios concretos has hecho en el cuerpo del commit.
- Intenta hacer al menos un commit al día o cada dosdías. El profesor revisa los repositorios a diario para asegurarse de que todos avanzan.


## Evaluación semanal

Cada viernes, el profesor revisa los repositorios de todos los grupos. Se evalua:

- **Commits:** Que cada persona del grupo tenga al menos 1 commit esa semana.
- **Compilacion:** Que el proyecto compile sin errores ejecutando `mvn clean compile`.
- **Tests:** Que todos los tests pasen ejecutando `mvn test`.
- **Cobertura:** Que la cobertura de tests sea al menos del 60%.
- **Calidad:** Que el código sea legible y siga los patrones de Spring Boot.

No se pide perfección. Se pide que avances, que preguntes cuando no entiendas algo y que tu código compile y los tests pasen.


## Si te bloqueas con Git

Es normal tener problemas con Git al principio. Estos son los más comunes y cómo resolverlos:

**"Mi compañero y yo hemos editado el mismo archivo"**
Esto no deberia pasar si mañana edita `src/main/` y tarde edita `src/test/`. Si aún así ocurre, avisad al profesor y lo resolvemos juntos en clase.

**"He hecho cambios pero no puedo hacer push"**
Probablemente otro compañero ha subido cambios. Haz `git pull` primero, resuelve cualquier conflicto si lo hay, y luego haz `git push`.

**"He roto algo y no se cómo volver atras"**
No borres nada. Avisa al profesor. Con `git log` y `git diff` podemos ver que ha cambiado y recuperarlo.

El profesor revisa los repositorios a diario y se asegura de que nadie se quede atascado con Git. Si tienes cualquier duda, pregunta antes de intentar soluciones que puedan borrar trabajo.


## Qué hacer si terminas todo antes de tiempo

Si tu grupo completa todas las entidades, pantallas y tests antes de que acabe el curso, aqui tienes ideas para seguir aprendiendo y mejorar tu proyecto:

### Mejoras de interfaz

- Mejorar el diseño visual de las páginas con CSS personalizado
- Añadir páginacion a los listados (en vez de mostrar todos los registros de golpe)
- Añadir ordenacion por columnas en las tablas (por nombre, por fecha, por precio)
- Añadir mensajes de éxito o error cuando el usuario crea o edita algo (flash messages)
- Crear una página de inicio (dashboard) con un resumen visual del estado de la aplicación

### Mejoras de funcionalidad

- Añadir búsqueda avanzada con múltiples filtros combinados
- Añadir exportacion de datos a CSV o PDF
- Añadir subida de imagenes (para platos, películas, eventos, etc.)
- Añadir un campo "favoritos" para que el usuario marque sus registros preferidos
- Añadir notificaciones por email (simuladas con log) para eventos como vencimiento de préstamos o confirmación de compra

### Mejoras tecnicas

- Sustituir la base de datos H2 en memoria por PostgreSQL o MySQL
- Añadir un perfil de configuración para produccion (application-prod.properties)
- Configurar cache de datos con Spring Cache para mejorar rendimiento
- Implementar una API REST (JSON) además de las vistas Thymeleaf
- Añadir documentacion de la API con Swagger/OpenAPI

### Mejoras de testing

- Aumentar la cobertura de tests al 90% o más
- Añadir tests de rendimiento básicos (medir tiempos de respuesta)
- Añadir tests de accesibilidad en las vistas
- Probar distintos navegadores con Selenium (Chrome, Firefox)
- Configurar ejecución automática de tests con GitHub Actions (CI/CD)

Estas mejoras son opcionales y puedes elegir las que más te interesen. Consúltalo con el profesor para decidir cuáles tienen más sentido para tu proyecto.


## Instalacion del JDK (hacer una sola vez)

IntelliJ IDEA descarga el JDK Temurin 25 automáticamente para compilar y ejecutar dentro del IDE. Pero si necesitas usar la terminal (para ejecutar `mvn`, `java` u otros comandos), el JDK no estara en el PATH del sistema.

Para solucionarlo, el proyecto incluye un script que descarga el JDK y lo configura de forma **permanente**. Solo hay que ejecutarlo **una vez** al comienzo del curso. A partir de ese momento, cualquier terminal que abras tendra Java 25 disponible.

**En Windows** (abrir una terminal y ejecutar):
```
setup-jdk.cmd
```

**En Linux o macOS:**
```bash
chmod +x setup-jdk.sh
./setup-jdk.sh
```

El script descarga el JDK en tu carpeta de usuario (`%USERPROFILE%\.jdks\temurin-25` en Windows, `~/.jdks/temurin-25` en Linux/macOS), la misma ubicacion que usa IntelliJ IDEA. El JDK no depende del proyecto: puedes borrar, mover o clonar el proyecto sin perder Java. También limpia cualquier JDK antiguo que tengas en el PATH. Si lo ejecutas de nuevo, detecta que ya esta instalado y no hace nada.

Si en Windows el script detecta un JDK antiguo en las variables del sistema que no puede limpiar, te pedira que lo ejecutes como administrador (clic derecho > Ejecutar como administrador) para completar la limpieza.


## Ejecucion del proyecto en local

Una vez que tienes el JDK configurado (ver seccion anterior), puedes usar estos comandos:

```bash
# Compilar el proyecto (verifica que no hay errores)
./mvnw clean compile

# Arrancar el servidor (abre http://localhost:8080 en tu navegador)
./mvnw spring-boot:run

# Ejecutar todos los tests
./mvnw test

# Ejecutar solo los tests unitarios de servicios
./mvnw test -Dtest="*ServiceTest"

# Generar informe de cobertura de tests
./mvnw test jacoco:report
```

En Windows, sustituye `./mvnw` por `mvnw.cmd`.


## Integración continua en local

El proyecto incluye un script que hace todo el proceso automáticamente: compila, ejecuta los tests y empaqueta la aplicación en un JAR. Es como tener un servidor de CI pero en tu propio ordenador.

**En Windows:**
```
ci-local.cmd
```

**En Linux o macOS:**
```bash
chmod +x ci-local.sh
./ci-local.sh
```

El script muestra el resultado de cada paso (compilacion, tests, empaquetado) y al final indica si todo ha ido bien o si hay algun error. Ejecutalo antes de hacer push para asegurarte de que tu código esta correcto.

Si el script dice "TODO CORRECTO", puedes hacer commit y push con tranquilidad.
