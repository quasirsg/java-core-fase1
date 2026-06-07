package com.taskflow;

import com.taskflow.model.Status;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests para el enum Status (mini máquina de estados).
 *
 * Usa reflexión para tolerar el esqueleto: si faltan constantes o métodos,
 * el test se omite (assumeTrue) en lugar de fallar con error de compilación.
 */
class StatusTest {

    @Test
    void existenPendingInProgressDoneCancelled() {
        constant("PENDING");
        constant("IN_PROGRESS");
        constant("DONE");
        constant("CANCELLED");
        assertTrue(Status.values().length >= 4,
                "Status debería tener al menos 4 constantes");
    }

    @Test
    void doneYCancelledSonTerminales() {
        assertTrue(isTerminal(constant("DONE")),      "DONE.isTerminal()");
        assertTrue(isTerminal(constant("CANCELLED")), "CANCELLED.isTerminal()");
    }

    @Test
    void pendingEInProgressNoSonTerminales() {
        assertFalse(isTerminal(constant("PENDING")),     "PENDING.isTerminal()");
        assertFalse(isTerminal(constant("IN_PROGRESS")), "IN_PROGRESS.isTerminal()");
    }

    @Test
    void pendingPuedePasarAInProgressDoneCancelled() {
        Status pending = constant("PENDING");
        assertTrue(canTransition(pending, constant("IN_PROGRESS")), "PENDING -> IN_PROGRESS");
        assertTrue(canTransition(pending, constant("DONE")),        "PENDING -> DONE");
        assertTrue(canTransition(pending, constant("CANCELLED")),   "PENDING -> CANCELLED");
    }

    @Test
    void inProgressPuedeVolverAPendingOTerminar() {
        Status inProgress = constant("IN_PROGRESS");
        assertTrue(canTransition(inProgress, constant("PENDING")),   "IN_PROGRESS -> PENDING");
        assertTrue(canTransition(inProgress, constant("DONE")),      "IN_PROGRESS -> DONE");
        assertTrue(canTransition(inProgress, constant("CANCELLED")), "IN_PROGRESS -> CANCELLED");
    }

    @Test
    void desdeEstadoTerminalNoSePuedeTransicionar() {
        Status done      = constant("DONE");
        Status cancelled = constant("CANCELLED");
        assertFalse(canTransition(done, constant("PENDING")),      "DONE -> PENDING debe ser false");
        assertFalse(canTransition(done, constant("IN_PROGRESS")),  "DONE -> IN_PROGRESS debe ser false");
        assertFalse(canTransition(cancelled, constant("PENDING")), "CANCELLED -> PENDING debe ser false");
    }

    @Test
    void noTieneSentidoTransicionarAlMismoEstado() {
        Status pending = constant("PENDING");
        assertFalse(canTransition(pending, pending), "PENDING -> PENDING debe ser false");
    }

    // ---- helpers de reflexión ----

    private static Status constant(String name) {
        try {
            return Status.valueOf(name);
        } catch (IllegalArgumentException e) {
            assumeTrue(false, "falta la constante Status." + name);
            throw new AssertionError("unreachable");
        }
    }

    private static boolean isTerminal(Status s) {
        try {
            Method m = Status.class.getMethod("isTerminal");
            return (boolean) m.invoke(s);
        } catch (NoSuchMethodException e) {
            assumeTrue(false, "falta el método isTerminal()");
            throw new AssertionError("unreachable");
        } catch (Exception e) {
            throw unwrap(e);
        }
    }

    private static boolean canTransition(Status from, Status to) {
        try {
            Method m = Status.class.getMethod("canTransitionTo", Status.class);
            return (boolean) m.invoke(from, to);
        } catch (NoSuchMethodException e) {
            assumeTrue(false, "falta el método canTransitionTo(Status)");
            throw new AssertionError("unreachable");
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
