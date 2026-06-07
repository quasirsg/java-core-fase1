package com.taskflow;

import com.taskflow.model.Status;
import com.taskflow.testkit.TestRunner;

import java.lang.reflect.Method;

/**
 * Tests para el enum Status (mini máquina de estados).
 *
 * Usa reflexión para tolerar el esqueleto: si faltan constantes o métodos,
 * el test se marca como "sin implementar" en lugar de romper la compilación.
 */
public final class StatusTest {

    public static void run(TestRunner t) {
        System.out.println("== StatusTest ==");

        t.test("existen PENDING, IN_PROGRESS, DONE, CANCELLED", () -> {
            constant("PENDING");
            constant("IN_PROGRESS");
            constant("DONE");
            constant("CANCELLED");
            TestRunner.assertTrue(Status.values().length >= 4,
                    "Status debería tener al menos 4 constantes");
        });

        t.test("DONE y CANCELLED son terminales", () -> {
            TestRunner.assertTrue(isTerminal(constant("DONE")), "DONE.isTerminal()");
            TestRunner.assertTrue(isTerminal(constant("CANCELLED")), "CANCELLED.isTerminal()");
        });

        t.test("PENDING e IN_PROGRESS NO son terminales", () -> {
            TestRunner.assertFalse(isTerminal(constant("PENDING")), "PENDING.isTerminal()");
            TestRunner.assertFalse(isTerminal(constant("IN_PROGRESS")), "IN_PROGRESS.isTerminal()");
        });

        t.test("PENDING puede pasar a IN_PROGRESS / DONE / CANCELLED", () -> {
            Status pending = constant("PENDING");
            TestRunner.assertTrue(canTransition(pending, constant("IN_PROGRESS")), "PENDING -> IN_PROGRESS");
            TestRunner.assertTrue(canTransition(pending, constant("DONE")), "PENDING -> DONE");
            TestRunner.assertTrue(canTransition(pending, constant("CANCELLED")), "PENDING -> CANCELLED");
        });

        t.test("IN_PROGRESS puede volver a PENDING o terminar", () -> {
            Status inProgress = constant("IN_PROGRESS");
            TestRunner.assertTrue(canTransition(inProgress, constant("PENDING")), "IN_PROGRESS -> PENDING");
            TestRunner.assertTrue(canTransition(inProgress, constant("DONE")), "IN_PROGRESS -> DONE");
            TestRunner.assertTrue(canTransition(inProgress, constant("CANCELLED")), "IN_PROGRESS -> CANCELLED");
        });

        t.test("desde un estado terminal NO se puede transicionar", () -> {
            Status done = constant("DONE");
            Status cancelled = constant("CANCELLED");
            TestRunner.assertFalse(canTransition(done, constant("PENDING")), "DONE -> PENDING debe ser false");
            TestRunner.assertFalse(canTransition(done, constant("IN_PROGRESS")), "DONE -> IN_PROGRESS debe ser false");
            TestRunner.assertFalse(canTransition(cancelled, constant("PENDING")), "CANCELLED -> PENDING debe ser false");
        });

        t.test("no tiene sentido transicionar al mismo estado", () -> {
            Status pending = constant("PENDING");
            TestRunner.assertFalse(canTransition(pending, pending), "PENDING -> PENDING debe ser false");
        });
    }

    // ---- helpers de reflexión ----

    private static Status constant(String name) {
        try {
            return Status.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("falta la constante Status." + name);
        }
    }

    private static boolean isTerminal(Status s) {
        try {
            Method m = Status.class.getMethod("isTerminal");
            return (boolean) m.invoke(s);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("falta el método isTerminal()");
        } catch (Exception e) {
            throw unwrap(e);
        }
    }

    private static boolean canTransition(Status from, Status to) {
        try {
            Method m = Status.class.getMethod("canTransitionTo", Status.class);
            return (boolean) m.invoke(from, to);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("falta el método canTransitionTo(Status)");
        } catch (Exception e) {
            throw unwrap(e);
        }
    }

    private static RuntimeException unwrap(Exception e) {
        Throwable cause = e.getCause() != null ? e.getCause() : e;
        if (cause instanceof RuntimeException re) {
            return re;
        }
        return new RuntimeException(cause);
    }
}
