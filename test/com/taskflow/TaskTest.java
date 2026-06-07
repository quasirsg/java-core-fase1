package com.taskflow;

import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;
import com.taskflow.testkit.TestRunner;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Tests para el record Task.
 *
 * - create / dueDateOpt / isOverdue / compareTo ya existen como métodos (lanzan
 *   UnsupportedOperationException hasta que los implementes => se marcan SKIP).
 * - Los "withers" (withStatus, withTitle, ...) están comentados en el esqueleto,
 *   por eso se acceden por reflexión para no romper la compilación.
 */
public final class TaskTest {

    public static void run(TestRunner t) {
        System.out.println("== TaskTest ==");

        t.test("constructor compacto rechaza título vacío", () ->
                TestRunner.assertThrows(RuntimeException.class, () ->
                                new Task("id1", "", "desc", priority("MEDIUM"), status("PENDING"),
                                        LocalDateTime.now(), null),
                        "título vacío debería lanzar InvalidTaskException"));

        t.test("constructor compacto rechaza título demasiado largo", () -> {
            String tooLong = "x".repeat(Task.MAX_TITLE_LENGTH + 1);
            TestRunner.assertThrows(RuntimeException.class, () ->
                            new Task("id1", tooLong, "desc", priority("MEDIUM"), status("PENDING"),
                                    LocalDateTime.now(), null),
                    "título > MAX_TITLE_LENGTH debería lanzar excepción");
        });

        t.test("constructor compacto acepta una tarea válida", () -> {
            Task task = new Task("id1", "Comprar pan", "desc", priority("MEDIUM"),
                    status("PENDING"), LocalDateTime.now(), null);
            TestRunner.assertEquals("Comprar pan", task.title(), "title()");
            TestRunner.assertEquals("id1", task.id(), "id()");
        });

        t.test("create() genera id, status PENDING y createdAt", () -> {
            Task task = Task.create("Tarea X", "detalle", priority("HIGH"), null);
            TestRunner.assertNotNull(task.id(), "id generado");
            TestRunner.assertTrue(!task.id().isBlank(), "id no vacío");
            TestRunner.assertEquals(status("PENDING"), task.status(), "status inicial");
            TestRunner.assertNotNull(task.createdAt(), "createdAt");
        });

        t.test("dueDateOpt() vacío cuando no hay fecha", () -> {
            Task task = new Task("id1", "T", "d", priority("LOW"), status("PENDING"),
                    LocalDateTime.now(), null);
            Optional<LocalDateTime> opt = task.dueDateOpt();
            TestRunner.assertNotNull(opt, "nunca null");
            TestRunner.assertTrue(opt.isEmpty(), "debería estar vacío");
        });

        t.test("dueDateOpt() presente cuando hay fecha", () -> {
            LocalDateTime due = LocalDateTime.now().plusDays(1);
            Task task = new Task("id1", "T", "d", priority("LOW"), status("PENDING"),
                    LocalDateTime.now(), due);
            TestRunner.assertEquals(due, task.dueDateOpt().orElse(null), "dueDateOpt()");
        });

        t.test("isOverdue() true si la fecha pasó y no está en estado terminal", () -> {
            Task task = new Task("id1", "T", "d", priority("LOW"), status("PENDING"),
                    LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
            TestRunner.assertTrue(task.isOverdue(), "tarea vencida y PENDING");
        });

        t.test("isOverdue() false si está en estado terminal (DONE)", () -> {
            Task task = new Task("id1", "T", "d", priority("LOW"), status("DONE"),
                    LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
            TestRunner.assertFalse(task.isOverdue(), "tarea DONE no se considera vencida");
        });

        t.test("isOverdue() false si no hay dueDate", () -> {
            Task task = new Task("id1", "T", "d", priority("LOW"), status("PENDING"),
                    LocalDateTime.now(), null);
            TestRunner.assertFalse(task.isOverdue(), "sin fecha no puede estar vencida");
        });

        t.test("withStatus() devuelve copia con nuevo estado (inmutabilidad)", () -> {
            Task task = new Task("id1", "T", "d", priority("LOW"), status("PENDING"),
                    LocalDateTime.now(), null);
            Task changed = (Task) wither(task, "withStatus", Status.class, status("DONE"));
            TestRunner.assertEquals(status("DONE"), changed.status(), "nuevo status");
            TestRunner.assertEquals(status("PENDING"), task.status(), "original NO cambia");
            TestRunner.assertEquals(task.id(), changed.id(), "id se conserva");
        });

        t.test("compareTo ordena URGENT antes que LOW (prioridad desc)", () -> {
            LocalDateTime now = LocalDateTime.now();
            Task urgent = new Task("a", "U", "d", priority("URGENT"), status("PENDING"), now, null);
            Task low = new Task("b", "L", "d", priority("LOW"), status("PENDING"), now, null);
            TestRunner.assertTrue(urgent.compareTo(low) < 0,
                    "URGENT debería ordenarse antes (compareTo < 0)");
        });
    }

    // ---- helpers de reflexión ----

    private static Priority priority(String name) {
        try {
            return Priority.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("falta Priority." + name + " (implementa Priority primero)");
        }
    }

    private static Status status(String name) {
        try {
            return Status.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("falta Status." + name + " (implementa Status primero)");
        }
    }

    private static Object wither(Task task, String method, Class<?> argType, Object arg) {
        try {
            Method m = Task.class.getMethod(method, argType);
            return m.invoke(task, arg);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("falta el método " + method + "(...) en Task");
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof RuntimeException re) {
                throw re;
            }
            throw new RuntimeException(cause);
        }
    }
}
