# 🚀 Cómo empezar a desarrollar TaskFlow CLI (template)

Este proyecto es un **esqueleto/template**: la estructura ya está armada y **compila**,
pero la lógica está marcada con `// TODO` y los métodos lanzan
`UnsupportedOperationException`. Tu trabajo es implementarlos uno a uno y así
practicar todo el Java Core del nivel básico.

- 📋 Requerimientos completos: [`doc/proyecto_fase1_cli_tareas.md`](proyecto_fase1_cli_tareas.md)
- 🗺️ Roadmap del nivel: [`doc/path_backend_java_basico.md`](path_backend_java_basico.md)

---

## 🧱 Qué está ya hecho vs. qué te toca a ti

| Archivo | Estado | Qué practicas |
|--------|--------|---------------|
| [`common/Result.java`](../src/com/taskflow/common/Result.java) | ✅ Resuelto (estúdialo) | sealed interface, records, pattern matching |
| [`exception/*.java`](../src/com/taskflow/exception) | ✅ Resuelto | excepciones personalizadas |
| [`repository/Repository.java`](../src/com/taskflow/repository/Repository.java) | ✅ Interfaz lista | generics |
| [`model/Priority.java`](../src/com/taskflow/model/Priority.java) | 🔲 Esqueleto | enum con campos/métodos |
| [`model/Status.java`](../src/com/taskflow/model/Status.java) | 🔲 Esqueleto | enum + máquina de estados |
| [`model/Task.java`](../src/com/taskflow/model/Task.java) | 🔲 Esqueleto | record, inmutabilidad, Comparable, java.time |
| [`repository/FileTaskRepository.java`](../src/com/taskflow/repository/FileTaskRepository.java) | 🔲 Esqueleto | colecciones, NIO de archivos |
| [`service/TaskService.java`](../src/com/taskflow/service/TaskService.java) | 🔲 Esqueleto | Streams, Optional, Result |
| [`concurrent/BackgroundScheduler.java`](../src/com/taskflow/concurrent/BackgroundScheduler.java) | 🔲 Esqueleto | ExecutorService, concurrencia |
| [`cli/ConsoleApp.java`](../src/com/taskflow/cli/ConsoleApp.java) | 🔲 Esqueleto (bucle ya hecho) | I/O por consola, manejo de errores |
| [`Main.java`](../src/Main.java) | ✅ Wiring listo | composición de la app |

---

## 🪜 Orden recomendado de implementación

Sigue este orden: cada paso te deja algo que ya puedes probar.

1. **`Priority`** → define las 4 constantes con peso y etiqueta. (5 min)
2. **`Status`** → constantes + `canTransitionTo` + `isTerminal`. (15 min)
3. **`Task`** → constructor compacto con validaciones, `create`, `isOverdue`,
   los `with...` y los `Comparator`. (30–45 min)
4. **`FileTaskRepository`** → primero solo la parte en memoria (`save`, `findById`,
   `findAll`, `deleteById`, `count`). Deja `load`/`flush` para después. (20 min)
5. **`TaskService`** → empieza por `createTask`, `findById`, `listAll`; luego los
   filtros y al final las estadísticas con Streams. (1 h)
6. **`ConsoleApp`** → completa `handleCreate` y `handleList` para ver tu app viva;
   luego el resto de handlers. (1–2 h)
7. **Persistencia** → vuelve a `FileTaskRepository` e implementa `load`/`flush`;
   descomenta `load()` en el constructor. (45 min)
8. **`BackgroundScheduler`** → implementa el autosave + aviso de vencidas y
   descomenta el bloque del scheduler en [`Main.java`](../src/Main.java). (45 min)
9. **Tests** → crea `test/com/taskflow/service/TaskServiceTest.java` con JUnit 5.

> 💡 Tip: trabaja método a método. Reemplaza cada `throw new UnsupportedOperationException(...)`
> por tu implementación y vuelve a compilar.

---

## ▶️ Compilar y ejecutar

### Opción A — desde IntelliJ (lo más simple)
- Abre [`src/Main.java`](../src/Main.java) y pulsa ▶ (Run).

### Opción B — desde la terminal
Tu JDK está en `C:\Users\Quasir\.jdks\openjdk-26.0.1\bin`. En **CMD**:

```bat
set JDK=C:\Users\Quasir\.jdks\openjdk-26.0.1\bin

REM Compilar todo a la carpeta out
dir /b /s src\*.java > sources.txt
"%JDK%\javac.exe" -d out @sources.txt

REM Ejecutar
"%JDK%\java.exe" -cp out Main
```

> Mientras haya métodos sin implementar, la app compilará pero lanzará
> `UnsupportedOperationException` al usar esa función. Es lo esperado.

---

## ✅ Checklist de aprendizaje (del enunciado)

- [ ] enum con campos y métodos (`Priority`, `Status`)
- [ ] record inmutable con validación (`Task`)
- [ ] interfaz genérica + implementación (`Repository` / `FileTaskRepository`)
- [ ] Streams en filtros y estadísticas (`TaskService`)
- [ ] `Optional` en búsquedas (sin `null`)
- [ ] excepciones personalizadas en uso
- [ ] persistencia en archivo (cargar/guardar)
- [ ] proceso concurrente con `ExecutorService` que cierra limpio
- [ ] tests con JUnit 5
- [ ] la app no crashea ante entradas inválidas

---

## 🆘 Si te trabas

- Lee el Javadoc de cada esqueleto: tiene los PASOS y PISTAS concretas.
- Estudia [`common/Result.java`](../src/com/taskflow/common/Result.java) como ejemplo
  resuelto de `sealed` + `switch` con patrones.
- No intentes hacerlo todo de golpe: sigue el orden de arriba.
