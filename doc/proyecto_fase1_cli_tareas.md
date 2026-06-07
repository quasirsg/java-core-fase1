# 🟢 Proyecto Fase 1 — App CLI de Gestión de Tareas (TaskFlow CLI)

Proyecto práctico del **NIVEL BÁSICO** del path de Backend Java.
Objetivo: aplicar **todo el Java Core** en un proyecto real, sin frameworks, solo Java puro.

> Este documento es el **enunciado / requerimientos**. La solución de referencia está implementada en `src/com/taskflow/`.

---

## 🎯 Meta de aprendizaje

Construir desde cero una aplicación de línea de comandos (CLI) para gestionar tareas (estilo "to-do" profesional) que te obligue a usar y entender:

| Tema del path | Dónde se aplica en el proyecto |
|---------------|-------------------------------|
| Clases, interfaces, enums, **records** | Modelo de dominio (`Task`, `Priority`, `Status`) |
| Herencia vs **composición** | Servicios que componen repositorios |
| Polimorfismo | Interfaz `Repository<T, ID>` con implementación en archivo |
| **Generics** | `Repository<T, ID>`, `Result<T>` |
| Excepciones checked/unchecked | `TaskException`, `PersistenceException`, validaciones |
| Lambdas y **Functional interfaces** | Filtros y `Comparator` de tareas |
| **Streams** | Búsquedas, filtros, ordenamientos, estadísticas |
| **Optional** | `findById`, búsquedas que pueden no existir |
| **Sealed classes / Pattern matching** | `Result` (Ok / Error) sellado |
| Inmutabilidad | `Task` como record inmutable (cambios crean copias) |
| `equals`, `hashCode`, `compareTo` | Igualdad e ordenamiento de tareas |
| **Fechas** (`java.time`) | `LocalDateTime` de creación y vencimiento |
| Colecciones (`List`, `Map`, etc.) | Almacenamiento en memoria + índices |
| **Concurrencia** (`ExecutorService`) | Autoguardado periódico + chequeo de vencimientos en background |
| Manejo de errores | Validaciones de entrada del usuario |
| Tests unitarios (JUnit 5) | `TaskServiceTest` |

---

## 📋 Requerimientos funcionales

La app es interactiva por consola (menú con opciones numéricas).

### RF-1 — Crear tarea
- El usuario ingresa: **título** (obligatorio), **descripción** (opcional), **prioridad** (LOW/MEDIUM/HIGH/URGENT) y **fecha de vencimiento** opcional.
- El sistema asigna un **ID único**, estado inicial `PENDING` y fecha de creación automática.
- Validar: el título no puede estar vacío ni superar 120 caracteres.

### RF-2 — Listar tareas
- Mostrar todas las tareas en formato tabular legible.
- Permitir ordenarlas por: prioridad, fecha de vencimiento o fecha de creación.

### RF-3 — Filtrar / buscar tareas
- Filtrar por **estado** (PENDING, IN_PROGRESS, DONE, CANCELLED).
- Filtrar por **prioridad**.
- Buscar por **texto** en el título o descripción (case-insensitive).
- Mostrar solo tareas **vencidas** (due date pasada y no DONE).

### RF-4 — Actualizar estado
- Cambiar el estado de una tarea por su ID.
- Transiciones inválidas deben rechazarse (ej: una tarea `CANCELLED` no puede pasar a `IN_PROGRESS`).

### RF-5 — Editar tarea
- Modificar título, descripción, prioridad o fecha de vencimiento por ID.
- Como `Task` es inmutable, editar genera una **nueva versión** del record.

### RF-6 — Eliminar tarea
- Eliminar por ID con confirmación.

### RF-7 — Estadísticas
- Total de tareas, cantidad por estado, cantidad por prioridad y cantidad de vencidas.
- Implementar con **Streams** (`groupingBy`, `counting`, `partitioningBy`).

### RF-8 — Persistencia
- Las tareas se guardan en un archivo de texto (`tasks.dat`, formato delimitado o JSON simple hecho a mano).
- Al iniciar la app, se cargan desde el archivo.
- Al salir (o periódicamente), se guardan.

---

## ⚙️ Requerimientos técnicos (lo que el path exige practicar)

### RT-1 — Modelo inmutable con records
- `Task` debe ser un `record`.
- Los enums `Priority` y `Status` con métodos propios (ej: `Status.canTransitionTo(...)`).

### RT-2 — Programación genérica
- Definir `interface Repository<T, ID>` con `save`, `findById`, `findAll`, `deleteById`.
- Implementación `FileTaskRepository implements Repository<Task, String>`.

### RT-3 — Sealed + pattern matching (opcional pero recomendado)
- Tipo `sealed interface Result<T>` con `Ok<T>` y `Err<T>` para representar éxito/error sin lanzar excepciones en validaciones de negocio.

### RT-4 — Streams obligatorios
- Toda la lógica de filtrado, ordenamiento y estadísticas debe usar la **Stream API**.

### RT-5 — Optional
- Las búsquedas por ID devuelven `Optional<Task>`. Prohibido devolver `null`.

### RT-6 — Concurrencia simple
- Un `ScheduledExecutorService` que cada N segundos:
  1. Guarda el estado actual en disco (autosave).
  2. Revisa tareas vencidas y muestra una notificación.
- Debe cerrarse correctamente al salir (`shutdown` + `awaitTermination`).
- Usar estructuras thread-safe o sincronización donde corresponda.

### RT-7 — Manejo de errores
- Excepciones personalizadas: `InvalidTaskException` (unchecked), `PersistenceException` (checked o runtime envuelta).
- Validar TODA entrada del usuario; nunca debe crashear por una entrada inválida.

### RT-8 — Tests (JUnit 5)
- Tests unitarios del servicio: creación, validaciones, transiciones de estado, filtros y estadísticas.
- Mínimo 8 casos de prueba.

---

## 🗂️ Estructura propuesta del proyecto

```
src/
└── com/taskflow/
    ├── Main.java                 # Punto de entrada
    ├── model/
    │   ├── Task.java             # record inmutable
    │   ├── Priority.java         # enum
    │   └── Status.java           # enum con reglas de transición
    ├── common/
    │   └── Result.java           # sealed interface (Ok / Err)
    ├── repository/
    │   ├── Repository.java       # interface genérica
    │   └── FileTaskRepository.java
    ├── service/
    │   └── TaskService.java      # lógica + streams + Optional
    ├── exception/
    │   ├── InvalidTaskException.java
    │   └── PersistenceException.java
    ├── concurrent/
    │   └── BackgroundScheduler.java  # ExecutorService
    └── cli/
        └── ConsoleApp.java       # menú interactivo
```

---

## ✅ Criterios de aceptación (checklist de aprendizaje)

- [ ] Usé al menos un `record`, un `enum` con métodos y una `sealed interface`.
- [ ] Definí una interfaz **genérica** y una implementación concreta.
- [ ] Usé **Streams** para todos los filtros y estadísticas.
- [ ] Las búsquedas devuelven **Optional**, nunca `null`.
- [ ] Implementé y usé **excepciones personalizadas**.
- [ ] La persistencia en archivo funciona (guardar y cargar).
- [ ] Hay un proceso **concurrente** con `ExecutorService` que se apaga limpio.
- [ ] Escribí **tests** con JUnit 5 que pasan.
- [ ] La app no crashea ante entradas inválidas del usuario.
- [ ] Sobreescribí `equals`/`hashCode` donde tiene sentido y usé `Comparator`.

---

## 🧪 Cómo extenderlo (retos extra)

1. Soportar **etiquetas (tags)** por tarea (`Set<String>`).
2. Exportar reporte a CSV usando Streams + `Collectors.joining`.
3. Reemplazar el formato propio por **JSON** real (sin librerías, parser a mano) — buen ejercicio de strings.
4. Añadir **virtual threads** (concepto del path) para el chequeo de vencimientos.
5. Internacionalizar los mensajes ( resource bundles).

---

## ▶️ Cómo compilar y ejecutar

```bash
# Compilar (desde la raíz del proyecto)
javac -d out src/com/taskflow/**/*.java src/com/taskflow/Main.java

# Ejecutar
java -cp out com.taskflow.Main
```

> Requiere **JDK 21+** (recomendado JDK 25 LTS, como indica el path).

---

**Path de referencia:** `doc/path_backend_java_basico.md`
**Siguiente proyecto del nivel:** API REST con Spring Boot.
