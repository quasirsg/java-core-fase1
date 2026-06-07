# 🎫 Backlog estilo Jira — TaskFlow CLI (Fase 1)

> Tablero de trabajo para implementar el template de **TaskFlow CLI**.
> Cada ticket está pensado como una historia/tarea de Jira: tiene **tipo**, **estimación (story points)**,
> **dependencias**, **librerías / APIs a usar**, **descripción técnica** y **criterios de aceptación (DoD)**.
>
> 📌 Solo **Java puro (JDK 21+)**. No se permiten frameworks ni librerías externas, salvo **JUnit 5** para los tests.
> Referencias: [`proyecto_fase1_cli_tareas.md`](proyecto_fase1_cli_tareas.md) · [`COMO_EMPEZAR.md`](COMO_EMPEZAR.md)

---

## 🏷️ Convenciones del tablero

| Campo | Valores posibles |
|-------|------------------|
| **Tipo** | 🟦 Story · 🟩 Task · 🟥 Bug · 🟪 Spike |
| **Prioridad** | 🔴 Highest · 🟠 High · 🟡 Medium · 🟢 Low |
| **Estado** | `TO DO` · `IN PROGRESS` · `IN REVIEW` · `DONE` |
| **Estimación** | Story Points (1 SP ≈ ½–1 h de trabajo) |

**Épicas:**
- **EPIC-1 — Modelo de dominio** (`model/`)
- **EPIC-2 — Persistencia** (`repository/`)
- **EPIC-3 — Lógica de negocio** (`service/`)
- **EPIC-4 — Interfaz CLI** (`cli/`)
- **EPIC-5 — Concurrencia** (`concurrent/`)
- **EPIC-6 — Calidad / Tests** (`test/`)

---

## EPIC-1 — Modelo de dominio

### TF-1 · Implementar enum `Priority`
- **Tipo:** 🟩 Task · **Prioridad:** 🔴 Highest · **Estimación:** 1 SP · **Estado:** `TO DO`
- **Épica:** EPIC-1 · **Dependencias:** ninguna
- **Archivo:** [`model/Priority.java`](../src/com/taskflow/model/Priority.java)

**Descripción**
Definir las 4 constantes `LOW`, `MEDIUM`, `HIGH`, `URGENT`, cada una con un **peso numérico** (para ordenar) y una **etiqueta** legible.

**Librerías / APIs a usar**
- `enum` de Java con **campos finales + constructor privado**.
- Sin imports externos.

**Detalles técnicos**
- Campos: `private final int weight;` y `private final String label;`.
- Getters `weight()` / `label()`.
- Opcional: método estático `fromString(String)` tolerante a mayúsculas usando `valueOf(s.trim().toUpperCase())`.

**Criterios de aceptación (DoD)**
- [ ] Las 4 constantes existen con peso y etiqueta correctos.
- [ ] Compila sin warnings.
- [ ] `URGENT.weight() > LOW.weight()` (peso usable por un `Comparator`).

---

### TF-2 · Implementar enum `Status` con máquina de estados
- **Tipo:** 🟩 Task · **Prioridad:** 🔴 Highest · **Estimación:** 2 SP · **Estado:** `TO DO`
- **Épica:** EPIC-1 · **Dependencias:** ninguna
- **Archivo:** [`model/Status.java`](../src/com/taskflow/model/Status.java)

**Descripción**
Definir `PENDING`, `IN_PROGRESS`, `DONE`, `CANCELLED` y las reglas de transición entre estados.

**Librerías / APIs a usar**
- `enum` con métodos.
- `java.util.Set` / `java.util.EnumSet` para declarar transiciones válidas.

**Detalles técnicos**
- `boolean canTransitionTo(Status next)` — usa `EnumSet` por constante (ej: `PENDING → {IN_PROGRESS, CANCELLED}`).
- `boolean isTerminal()` — `true` para `DONE` y `CANCELLED`.
- Una transición desde un estado terminal debe devolver `false`.

**Criterios de aceptación (DoD)**
- [ ] `PENDING.canTransitionTo(IN_PROGRESS)` == `true`.
- [ ] `CANCELLED.canTransitionTo(IN_PROGRESS)` == `false`.
- [ ] `DONE.isTerminal()` == `true`.

---

### TF-3 · Implementar record inmutable `Task`
- **Tipo:** 🟦 Story · **Prioridad:** 🔴 Highest · **Estimación:** 5 SP · **Estado:** `TO DO`
- **Épica:** EPIC-1 · **Dependencias:** TF-1, TF-2
- **Archivo:** [`model/Task.java`](../src/com/taskflow/model/Task.java)

**Descripción**
Modelo central inmutable. Una `Task` con ID, título, descripción, prioridad, estado, fechas de creación y vencimiento. Toda "edición" devuelve una **copia nueva**.

**Librerías / APIs a usar**
- `record` (constructor compacto con validaciones).
- `java.time.LocalDateTime` (creación / vencimiento).
- `java.util.UUID` para el ID único.
- `java.util.Comparator` para ordenamientos.
- `InvalidTaskException` ([`exception/InvalidTaskException.java`](../src/com/taskflow/exception/InvalidTaskException.java)) en validaciones.

**Detalles técnicos**
- **Constructor compacto:** título no nulo, no vacío, ≤ 120 caracteres → si no, `InvalidTaskException`.
- Factory `static Task create(...)`: asigna `UUID.randomUUID().toString()`, `Status.PENDING`, `createdAt = LocalDateTime.now()`.
- Métodos `withStatus`, `withTitle`, `withPriority`, `withDueDate`... que retornan **nuevas instancias**.
- `boolean isOverdue()`: `dueDate != null && dueDate.isBefore(now) && status != DONE`.
- Comparators estáticos: `BY_PRIORITY` (usa `Priority.weight()` desc), `BY_DUE_DATE`, `BY_CREATED_AT`.

**Criterios de aceptación (DoD)**
- [ ] Crear con título vacío lanza `InvalidTaskException`.
- [ ] `withStatus(...)` no muta la instancia original (inmutabilidad real).
- [ ] `equals`/`hashCode` funcionan por ID (los provee el record).
- [ ] Existe al menos un `Comparator` reutilizable.

---

## EPIC-2 — Persistencia

### TF-4 · `FileTaskRepository` — almacenamiento en memoria
- **Tipo:** 🟦 Story · **Prioridad:** 🟠 High · **Estimación:** 3 SP · **Estado:** `TO DO`
- **Épica:** EPIC-2 · **Dependencias:** TF-3
- **Archivos:** [`repository/Repository.java`](../src/com/taskflow/repository/Repository.java) (interfaz lista), [`repository/FileTaskRepository.java`](../src/com/taskflow/repository/FileTaskRepository.java)

**Descripción**
Implementar primero la parte **en memoria** del repositorio genérico: `save`, `findById`, `findAll`, `deleteById`, `count`.

**Librerías / APIs a usar**
- `java.util.Map` → recomendado `ConcurrentHashMap` (será accedido por el scheduler en TF-9).
- `java.util.Optional` (para `findById`).
- `java.util.List` + `List.copyOf(...)` para devolver copias inmutables en `findAll`.

**Detalles técnicos**
- `Map<String, Task> store = new ConcurrentHashMap<>();`
- `findById` devuelve `Optional.ofNullable(store.get(id))` — **prohibido devolver `null`**.
- `findAll` devuelve una copia defensiva (no exponer el map interno).

**Criterios de aceptación (DoD)**
- [ ] `save` + `findById` recupera la misma tarea.
- [ ] `findById` de un ID inexistente devuelve `Optional.empty()`.
- [ ] `deleteById` reduce el `count()`.
- [ ] `findAll` no permite mutar el estado interno.

---

### TF-5 · `FileTaskRepository` — persistencia en disco (`load` / `flush`)
- **Tipo:** 🟦 Story · **Prioridad:** 🟠 High · **Estimación:** 5 SP · **Estado:** `TO DO`
- **Épica:** EPIC-2 · **Dependencias:** TF-4
- **Archivo:** [`repository/FileTaskRepository.java`](../src/com/taskflow/repository/FileTaskRepository.java)

**Descripción**
Guardar/cargar las tareas en un archivo de texto delimitado (`tasks.dat`). Cargar al iniciar, persistir al `flush`.

**Librerías / APIs a usar**
- `java.nio.file.Path` / `Files` (`readAllLines`, `write`, `exists`, `createFile`).
- `java.nio.charset.StandardCharsets.UTF_8`.
- `java.time.format.DateTimeFormatter` (`ISO_LOCAL_DATE_TIME`) para serializar fechas.
- `String.split` / `String.join` con un delimitador seguro (ej: `|` o `\u001F`).
- `PersistenceException` ([`exception/PersistenceException.java`](../src/com/taskflow/exception/PersistenceException.java)) envolviendo `IOException`.

**Detalles técnicos**
- Formato por línea: `id|title|description|priority|status|createdAt|dueDate`.
- Escapar/normalizar el delimitador en los textos (o usar un separador improbable).
- `load()`: si el archivo no existe, no falla (arranca vacío). Parsear líneas → `Task`.
- `flush()`: serializa `store.values()` y escribe atómicamente (`Files.write` con `TRUNCATE_EXISTING`).
- Capturar `IOException` → relanzar como `PersistenceException`.
- Descomentar la llamada a `load()` en el constructor.

**Criterios de aceptación (DoD)**
- [ ] `flush()` y reiniciar → `load()` recupera las mismas tareas.
- [ ] Archivo inexistente al iniciar no rompe la app.
- [ ] Las fechas se serializan/parsean correctamente (round-trip).
- [ ] Un error de I/O se traduce a `PersistenceException`.

---

## EPIC-3 — Lógica de negocio

### TF-6 · `TaskService` — CRUD base + `Result`
- **Tipo:** 🟦 Story · **Prioridad:** 🟠 High · **Estimación:** 5 SP · **Estado:** `TO DO`
- **Épica:** EPIC-3 · **Dependencias:** TF-4 (al menos en memoria)
- **Archivos:** [`service/TaskService.java`](../src/com/taskflow/service/TaskService.java), [`common/Result.java`](../src/com/taskflow/common/Result.java) (ya resuelto)

**Descripción**
Implementar `createTask`, `findById`, `listAll`, `updateStatus`, `editTask`, `deleteTask`. Las validaciones de negocio devuelven `Result<T>` en lugar de lanzar excepción.

**Librerías / APIs a usar**
- `sealed interface Result<T>` (`Ok` / `Err`) + **pattern matching** (`switch`) — ver [`common/Result.java`](../src/com/taskflow/common/Result.java).
- `java.util.Optional` para `findById`.
- El `Repository<Task, String>` por **composición** (no herencia).

**Detalles técnicos**
- `createTask(...)`: valida con `Task.create` y guarda → `Result.ok(task)` o `Result.err(msg)`.
- `updateStatus(id, next)`: busca por ID, valida `status.canTransitionTo(next)`; si no, `Result.err`.
- `editTask(...)`: usa los `with...` del record y vuelve a `save` (nueva versión inmutable).
- Nunca devolver `null`; búsquedas → `Optional`.

**Criterios de aceptación (DoD)**
- [ ] Crear tarea válida → `Ok`; inválida → `Err` (sin crashear).
- [ ] Transición inválida de estado → `Err`.
- [ ] `editTask` genera una nueva versión del record y la persiste.
- [ ] `findById` devuelve `Optional`.

---

### TF-7 · `TaskService` — Filtros y búsquedas con Streams
- **Tipo:** 🟦 Story · **Prioridad:** 🟡 Medium · **Estimación:** 3 SP · **Estado:** `TO DO`
- **Épica:** EPIC-3 · **Dependencias:** TF-6
- **Archivo:** [`service/TaskService.java`](../src/com/taskflow/service/TaskService.java)

**Descripción**
Filtrar por estado, por prioridad, búsqueda de texto (case-insensitive) y listado de tareas vencidas. Permitir ordenar el listado.

**Librerías / APIs a usar**
- **Stream API** (`filter`, `sorted`, `map`, `collect`).
- `java.util.function.Predicate` (filtros componibles).
- `java.util.Comparator` (los de `Task`).
- `Collectors.toList()` / `toUnmodifiableList()`.

**Detalles técnicos**
- `filterByStatus(Status)`, `filterByPriority(Priority)`, `search(String)`, `overdue()`.
- Texto: `t.title().toLowerCase().contains(q.toLowerCase()) || descripción...`.
- `listSortedBy(Comparator<Task>)` reutilizable.
- **Obligatorio** resolver todo con Streams (no bucles `for` manuales).

**Criterios de aceptación (DoD)**
- [ ] Cada filtro devuelve solo los elementos esperados.
- [ ] La búsqueda es case-insensitive.
- [ ] `overdue()` excluye las tareas `DONE`.
- [ ] No hay bucles imperativos para filtrar/ordenar.

---

### TF-8 · `TaskService` — Estadísticas con Collectors
- **Tipo:** 🟩 Task · **Prioridad:** 🟡 Medium · **Estimación:** 2 SP · **Estado:** `TO DO`
- **Épica:** EPIC-3 · **Dependencias:** TF-7
- **Archivo:** [`service/TaskService.java`](../src/com/taskflow/service/TaskService.java)

**Descripción**
Calcular: total, cantidad por estado, cantidad por prioridad y cantidad de vencidas.

**Librerías / APIs a usar**
- `java.util.stream.Collectors`: `groupingBy`, `counting`, `partitioningBy`.
- `Map<Status, Long>` / `Map<Priority, Long>` como retorno.

**Detalles técnicos**
- Por estado: `Collectors.groupingBy(Task::status, Collectors.counting())`.
- Vencidas vs no vencidas: `Collectors.partitioningBy(Task::isOverdue)`.

**Criterios de aceptación (DoD)**
- [ ] Los conteos por estado/prioridad cuadran con el total.
- [ ] Se usan `groupingBy` y `partitioningBy`.
- [ ] El resultado es serializable a texto para la CLI.

---

## EPIC-5 — Concurrencia

### TF-9 · `BackgroundScheduler` — autosave + aviso de vencidas
- **Tipo:** 🟦 Story · **Prioridad:** 🟡 Medium · **Estimación:** 5 SP · **Estado:** `TO DO`
- **Épica:** EPIC-5 · **Dependencias:** TF-5, TF-7
- **Archivo:** [`concurrent/BackgroundScheduler.java`](../src/com/taskflow/concurrent/BackgroundScheduler.java)

**Descripción**
Proceso en background que cada N segundos guarda el estado en disco y avisa de tareas vencidas. Debe apagarse limpio al salir.

**Librerías / APIs a usar**
- `java.util.concurrent.ScheduledExecutorService` + `Executors.newSingleThreadScheduledExecutor`.
- `scheduleAtFixedRate(...)` con `TimeUnit.SECONDS`.
- `shutdown()` + `awaitTermination(...)` para cierre ordenado.
- Estructuras thread-safe (`ConcurrentHashMap` del repo de TF-4).
- Opcional: `java.util.concurrent.ThreadFactory` para nombrar el hilo daemon.

**Detalles técnicos**
- Tarea periódica: `repository.flush()` + consultar `service.overdue()` y notificar.
- Envolver el cuerpo del task en `try/catch` para que una excepción no mate el scheduler.
- `stop()`: `shutdown()`, esperar con timeout, `shutdownNow()` si no termina.
- Descomentar el bloque del scheduler en [`Main.java`](../src/Main.java) y registrar un shutdown hook si aplica.

**Criterios de aceptación (DoD)**
- [ ] El autosave persiste sin intervención del usuario.
- [ ] Las vencidas se notifican en consola.
- [ ] Al salir, el executor cierra sin dejar hilos colgados.
- [ ] Una excepción dentro del task no detiene los ciclos siguientes.

---

## EPIC-4 — Interfaz CLI

### TF-10 · `ConsoleApp` — Crear y Listar
- **Tipo:** 🟦 Story · **Prioridad:** 🟠 High · **Estimación:** 3 SP · **Estado:** `TO DO`
- **Épica:** EPIC-4 · **Dependencias:** TF-6
- **Archivo:** [`cli/ConsoleApp.java`](../src/com/taskflow/cli/ConsoleApp.java)

**Descripción**
Implementar `handleCreate` y `handleList` para ver la app "viva" (el bucle de menú ya existe).

**Librerías / APIs a usar**
- `java.util.Scanner` para leer entrada (ya inyectado).
- `System.out.printf` / `String.format` para tablas legibles.
- Pattern matching sobre `Result` para mostrar Ok/Err.

**Detalles técnicos**
- `handleCreate`: pedir título/desc/prioridad/fecha; parsear prioridad con `Priority.valueOf` (con manejo de error).
- `handleList`: render tabular con columnas alineadas (`printf` con anchos fijos).
- Toda entrada inválida muestra mensaje y vuelve al menú (no excepción al usuario).

**Criterios de aceptación (DoD)**
- [ ] Se puede crear una tarea desde el menú.
- [ ] El listado se ve alineado y legible.
- [ ] Entrada inválida no crashea la app.

---

### TF-11 · `ConsoleApp` — Resto de handlers (filtrar, actualizar, editar, borrar, stats)
- **Tipo:** 🟦 Story · **Prioridad:** 🟡 Medium · **Estimación:** 5 SP · **Estado:** `TO DO`
- **Épica:** EPIC-4 · **Dependencias:** TF-7, TF-8, TF-10
- **Archivo:** [`cli/ConsoleApp.java`](../src/com/taskflow/cli/ConsoleApp.java)

**Descripción**
Completar `handleFilter`, `handleUpdateStatus`, `handleEdit`, `handleDelete` (con confirmación) y `handleStats`.

**Librerías / APIs a usar**
- `java.util.Scanner`, `String.format`.
- Reutilizar los métodos de `TaskService` (TF-7, TF-8).

**Detalles técnicos**
- `handleDelete`: pedir confirmación (`s/n`) antes de borrar.
- `handleUpdateStatus`: mostrar `Err` si la transición es inválida.
- `handleStats`: imprimir el mapa de estadísticas formateado.

**Criterios de aceptación (DoD)**
- [ ] Todos los handlers funcionan end-to-end.
- [ ] El borrado pide confirmación.
- [ ] Los errores de negocio se muestran como mensajes, no como stack traces.

---

## EPIC-6 — Calidad / Tests

### TF-12 · Tests unitarios de `TaskService` (JUnit 5)
- **Tipo:** 🟦 Story · **Prioridad:** 🟠 High · **Estimación:** 5 SP · **Estado:** `TO DO`
- **Épica:** EPIC-6 · **Dependencias:** TF-6, TF-7, TF-8
- **Archivo (nuevo):** `test/com/taskflow/service/TaskServiceTest.java`

**Descripción**
Mínimo 8 casos de prueba cubriendo creación, validaciones, transiciones de estado, filtros y estadísticas.

**Librerías / APIs a usar**
- **JUnit 5** (`org.junit.jupiter.api`): `@Test`, `@BeforeEach`, `@DisplayName`.
- `org.junit.jupiter.api.Assertions`: `assertEquals`, `assertTrue`, `assertThrows`.
- Opcional: `@ParameterizedTest` para varios casos de validación.

**Detalles técnicos**
- Setup con un repositorio en memoria limpio por test (`@BeforeEach`).
- Casos mínimos: crear OK, crear título vacío (`Err`/excepción), transición válida, transición inválida, filtro por estado, filtro por prioridad, búsqueda de texto, estadísticas por estado.
- Añadir JUnit 5 al classpath (vía IntelliJ: *Add library → JUnit 5*).

**Criterios de aceptación (DoD)**
- [ ] ≥ 8 tests y todos pasan en verde.
- [ ] Cubre validaciones, transiciones, filtros y estadísticas.
- [ ] Los tests no dependen del archivo de disco real (usan memoria).

---

### TF-13 · Hardening: la app nunca crashea por input inválido
- **Tipo:** 🟥 Bug / Hardening · **Prioridad:** 🟡 Medium · **Estimación:** 2 SP · **Estado:** `TO DO`
- **Épica:** EPIC-6 · **Dependencias:** TF-10, TF-11
- **Archivo:** [`cli/ConsoleApp.java`](../src/com/taskflow/cli/ConsoleApp.java)

**Descripción**
Revisar todos los puntos de entrada del usuario para garantizar que ninguna entrada inválida produzca un stack trace.

**Librerías / APIs a usar**
- `try/catch` sobre `NumberFormatException`, `DateTimeParseException`, `IllegalArgumentException`.
- `java.time.format.DateTimeParseException`.

**Criterios de aceptación (DoD)**
- [ ] Letras donde se espera un número → mensaje, no crash.
- [ ] Fecha mal formateada → mensaje, no crash.
- [ ] Enum inválido (prioridad/estado) → mensaje, no crash.

---

## 🧩 Backlog opcional (retos extra del enunciado)

| ID | Reto | API / técnica sugerida | SP |
|----|------|------------------------|----|
| TF-14 | Tags por tarea (`Set<String>`) | `java.util.Set`, parser CSV manual | 3 |
| TF-15 | Exportar reporte a CSV | `Stream` + `Collectors.joining`, `Files.write` | 2 |
| TF-16 | Persistencia en JSON real (parser a mano) | `String`/`StringBuilder`, sin librerías | 5 |
| TF-17 | Virtual Threads para chequeo de vencimientos | `Executors.newVirtualThreadPerTaskExecutor()` | 3 |
| TF-18 | Internacionalización de mensajes | `java.util.ResourceBundle`, `Locale` | 3 |

---

## 🗺️ Orden de ejecución sugerido (sprint plan)

```
Sprint 1 (modelo):        TF-1 → TF-2 → TF-3
Sprint 2 (datos):         TF-4 → TF-6 → TF-7 → TF-8
Sprint 3 (CLI viva):      TF-10 → TF-11 → TF-13
Sprint 4 (persistencia):  TF-5 → TF-9
Sprint 5 (calidad):       TF-12
Backlog opcional:         TF-14 … TF-18
```

> Cada ticket queda en `IN REVIEW` cuando compila y cumple su DoD; pásalo a `DONE`
> solo cuando lo hayas probado manualmente o con su test.
