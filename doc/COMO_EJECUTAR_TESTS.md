# 🧪 Cómo ejecutar los tests de TaskFlow CLI

Esta guía explica cómo correr la **suite de tests propia** del proyecto. Los tests
te sirven como **brújula**: te dicen, paso a paso, qué te falta implementar y si lo
que ya hiciste funciona correctamente.

> 💡 Estos tests **no necesitan JUnit ni ninguna dependencia externa**: corren solo
> con el JDK que ya usas para el proyecto. Por eso son ideales para un template de
> aprendizaje como este.

---

## 📂 Qué se creó

```
test/
└── com/taskflow/
    ├── PriorityTest.java              # tests del enum Priority
    ├── StatusTest.java                # tests del enum Status (máquina de estados)
    ├── TaskTest.java                  # tests del record Task
    ├── FileTaskRepositoryTest.java    # tests del repositorio (memoria + archivo)
    ├── TaskServiceTest.java           # tests de la lógica de negocio
    └── testkit/
        ├── TestRunner.java            # mini-framework de aserciones (sin JUnit)
        └── RunAllTests.java           # ejecuta TODAS las suites (main)

run-tests.bat                          # script: compila y ejecuta todo de un tirón
```

---

## ▶️ Opción A — Script automático (lo más fácil)

Desde la **raíz del proyecto**, en una terminal (CMD o la terminal integrada de
VS Code / IntelliJ):

```bat
run-tests.bat
```

El script hace 3 cosas:

1. Junta todos los `.java` de `src` y `test` en `sources.txt`.
2. Compila todo a la carpeta `out`.
3. Ejecuta [`com.taskflow.testkit.RunAllTests`](../test/com/taskflow/testkit/RunAllTests.java:1).

> ⚠️ Si tu JDK está en otra ruta, edita la línea `set "JDK=..."` dentro de
> [`run-tests.bat`](../run-tests.bat:1). Por defecto apunta a
> `C:\Users\Quasir\.jdks\openjdk-26.0.1\bin`.

---

## ▶️ Opción B — Manual desde la terminal (CMD)

Si prefieres entender cada paso:

```bat
REM 1) Define la ruta de tu JDK
set JDK=C:\Users\Quasir\.jdks\openjdk-26.0.1\bin

REM 2) Genera la lista de TODOS los fuentes (src + test)
dir /b /s src\*.java > sources.txt
dir /b /s test\*.java >> sources.txt

REM 3) Compila a la carpeta out
"%JDK%\javac.exe" -d out @sources.txt

REM 4) Ejecuta la suite de tests
"%JDK%\java.exe" -cp out com.taskflow.testkit.RunAllTests
```

---

## ▶️ Opción C — Desde IntelliJ IDEA

1. Marca la carpeta `test` como **Test Sources Root**:
   clic derecho en `test` → **Mark Directory as → Test Sources Root**.
2. Abre [`RunAllTests.java`](../test/com/taskflow/testkit/RunAllTests.java:1).
3. Pulsa ▶ (Run) sobre el método `main`.

---

## 📊 Cómo leer los resultados

Cada test imprime una etiqueta:

| Etiqueta   | Significado |
|------------|-------------|
| `[ OK ]`   | ✅ El test pasó: esa funcionalidad está bien implementada. |
| `[FAIL]`   | ❌ El test corrió pero el resultado NO fue el esperado. Hay un bug que corregir (el mensaje te dice qué se esperaba vs. qué obtuviste). |
| `[SKIP]`   | ⏭️ Todavía no implementaste ese método/constante (lanza `UnsupportedOperationException` o falta un enum). **No cuenta como error**, es tu "lista de pendientes". |

Al final verás un **resumen global**:

```
RESULTADO GLOBAL: ✅ Sin fallos
```

o, si algo está mal implementado:

```
RESULTADO GLOBAL: ❌ 2 test(s) en FALLO
 - Task :: isOverdue() true ... -> esperado=<true> obtenido=<false>
```

> 🎯 **Tu objetivo:** ir transformando los `[SKIP]` en `[ OK ]` a medida que
> implementas cada parte, sin que aparezcan `[FAIL]`.

---

## 🪜 Flujo de trabajo recomendado

Sigue el mismo orden que [`doc/COMO_EMPEZAR.md`](COMO_EMPEZAR.md):

1. Implementa **`Priority`** → vuelve a correr `run-tests.bat`.
   Los tests de `PriorityTest` deberían pasar de `[SKIP]` a `[ OK ]`.
2. Implementa **`Status`** → los de `StatusTest` se ponen verdes.
3. Implementa **`Task`** → se activan los tests de `TaskTest`.
4. Implementa **`FileTaskRepository`** (memoria primero, luego `load`/`flush`).
5. Implementa **`TaskService`** → se activa `TaskServiceTest`.

Repite **implementar → correr tests → corregir** hasta que el resumen global diga
`✅ Sin fallos` y no queden `[SKIP]`.

> 💡 Nota: algunos tests de `TaskTest` ya aparecen en `[ OK ]` desde el inicio si
> ya implementaste las validaciones del constructor compacto de `Task`.

---

## ❓ Preguntas frecuentes

**¿Por qué no usamos JUnit como dice el enunciado?**
El enunciado lo menciona como objetivo de aprendizaje, pero JUnit requiere
descargar los `.jar` y configurarlos en el classpath. Para que puedas testear
**desde ya, sin instalar nada**, se incluyó un mini-runner propio en
[`TestRunner.java`](../test/com/taskflow/testkit/TestRunner.java:1). La forma de
escribir y leer los tests es muy parecida (assertEquals, assertTrue, assertThrows),
así que cuando pases a JUnit te resultará familiar. Si más adelante quieres migrar
a JUnit 5, la estructura de paquetes ya está lista (`test/com/taskflow/...`).

**¿Por qué muchos tests usan reflexión?**
Porque el esqueleto todavía no tiene algunas constantes (`Priority.LOW`,
`Status.PENDING`, ...) ni los métodos `with...`. Si los referenciáramos
directamente, el archivo de test **no compilaría**. Con reflexión, los tests
compilan siempre y simplemente se marcan `[SKIP]` hasta que implementes esas piezas.

**Los tests dejan archivos sueltos, ¿es normal?**
- `out/` → clases compiladas (ya está en `.gitignore`).
- `sources.txt` → lista temporal de fuentes (puedes borrarla).
- Los tests del repositorio crean archivos en una **carpeta temporal del sistema**,
  no en tu proyecto, así que no ensucian nada.
