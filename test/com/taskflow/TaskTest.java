package com.taskflow;

import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests para el record Task.
 *
 * - create / dueDateOpt / isOverdue / compareTo ya existen como métodos (lanzan
 *   UnsupportedOperationException hasta que los implementes => se omiten con assumeTrue).
 * - Los "withers" (withStatus, withTitle, ...) se acceden por reflexión para no
 *   romper la compilación si aún no existen.
 */
class TaskTest {

    @Test
    void constructorCompactoRechazaTituloVacio() {
        assertThrows(RuntimeException.class, () ->
                new Task("id1", "", "desc", priority("MEDIUM"), status("PENDING"),
                        LocalDateTime.now(), null),
                "título vacío debería lanzar InvalidTaskException");
    }

    @Test
    void constructorCompactoRechazaTituloDemaisadoLargo() {
        String tooLong = "x".repeat(Task.MAX_TITLE_LENGTH + 1);
        assertThrows(RuntimeException.class, () ->
                new Task("id1", tooLong, "desc", priority("MEDIUM"), status("PENDING"),
                        LocalDateTime.now(), null),
                "título > MAX_TITLE_LENGTH debería lanzar excepción");
    }

    @Test
    void constructorCompactoAceptaTareaValida() {
        Task task = new Task("id1", "Comprar pan", "desc", priority("MEDIUM"),
                status("PENDING"), LocalDateTime.now(), null);
        assertEquals("Comprar pan", task.title(), "title()");
        assertEquals("id1", task.id(), "id()");
    }

    @Test
    void createGeneraIdStatusPendingYCreatedAt() {
        Task task = Task.create("Tarea X", "detalle", priority("HIGH"), null);
        assertNotNull(task.id(), "id generado");
        assertFalse(task.id().isBlank(), "id no vacío");
        assertEquals(status("PENDING"), task.status(), "status inicial");
        assertNotNull(task.createdAt(), "createdAt");
    }

    @Test
    void dueDateOptVacioCuandoNoHayFecha() {
        Task task = new Task("id1", "T", "d", priority("LOW"), status("PENDING"),
                LocalDateTime.now(), null);
        Optional<LocalDateTime> opt = task.dueDateOpt();
        assertNotNull(opt, "nunca null");
        assertTrue(opt.isEmpty(), "debería estar vacío");
    }

    @Test
    void dueDateOptPresenteCuandoHayFecha() {
        LocalDateTime due = LocalDateTime.now().plusDays(1);
        Task task = new Task("id1", "T", "d", priority("LOW"), status("PENDING"),
                LocalDateTime.now(), due);
        assertEquals(due, task.dueDateOpt().orElse(null), "dueDateOpt()");
    }

    @Test
    void isOverdueTrueSiFechaPassadaYNoTerminal() {
        Task task = new Task("id1", "T", "d", priority("LOW"), status("PENDING"),
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        assertTrue(task.isOverdue(), "tarea vencida y PENDING");
    }

    @Test
    void isOverdueFalseSiEstaEnEstadoTerminalDone() {
        Task task = new Task("id1", "T", "d", priority("LOW"), status("DONE"),
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        assertFalse(task.isOverdue(), "tarea DONE no se considera vencida");
    }

    @Test
    void isOverdueFalseSiNoHayDueDate() {
        Task task = new Task("id1", "T", "d", priority("LOW"), status("PENDING"),
                LocalDateTime.now(), null);
        assertFalse(task.isOverdue(), "sin fecha no puede estar vencida");
    }

    @Test
    void withStatusDevuelveCopiaConNuevoEstadoInmutabilidad() {
        Task task = new Task("id1", "T", "d", priority("LOW"), status("PENDING"),
                LocalDateTime.now(), null);
        Task changed = (Task) wither(task, "withStatus", Status.class, status("DONE"));
        assertEquals(status("DONE"),    changed.status(), "nuevo status");
        assertEquals(status("PENDING"), task.status(),    "original NO cambia");
        assertEquals(task.id(),         changed.id(),     "id se conserva");
    }

    @Test
    void compareToOrdenaPorPrioridadDescUrgentAntesQueLow() {
        LocalDateTime now = LocalDateTime.now();
        Task urgent = new Task("a", "U", "d", priority("URGENT"), status("PENDING"), now, null);
        Task low    = new Task("b", "L", "d", priority("LOW"),    status("PENDING"), now, null);
        assertTrue(urgent.compareTo(low) < 0,
                "URGENT debería ordenarse antes (compareTo < 0)");
    }

    // ---- helpers de reflexión ----

    private static Priority priority(String name) {
        try {
            return Priority.valueOf(name);
        } catch (IllegalArgumentException e) {
            assumeTrue(false, "falta Priority." + name + " (implementa Priority primero)");
            throw new AssertionError("unreachable");
        }
    }

    private static Status status(String name) {
        try {
            return Status.valueOf(name);
        } catch (IllegalArgumentException e) {
            assumeTrue(false, "falta Status." + name + " (implementa Status primero)");
            throw new AssertionError("unreachable");
        }
    }

    private static Object wither(Task task, String method, Class<?> argType, Object arg) {
        try {
            Method m = Task.class.getMethod(method, argType);
            return m.invoke(task, arg);
        } catch (NoSuchMethodException e) {
            assumeTrue(false, "falta el método " + method + "(...) en Task");
            throw new AssertionError("unreachable");
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof RuntimeException re) {
                throw re;
            }
            throw new RuntimeException(cause);
        }
    }
}
