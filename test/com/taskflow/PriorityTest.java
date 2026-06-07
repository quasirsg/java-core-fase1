package com.taskflow;

import com.taskflow.model.Priority;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests para el enum Priority.
 *
 * Usa reflexión para tolerar el esqueleto: si faltan constantes o métodos,
 * el test se marca como "deshabilitado" (assumeTrue) en lugar de romper la compilación.
 */
class PriorityTest {

    @Test
    void existenLas4ConstantesLowMediumHighUrgent() {
        Priority[] values = Priority.values();
        assumeTrue(values.length > 0, "aún no agregaste las constantes de Priority");
        assertEquals(4, values.length,
                "Priority.values() debería tener 4 constantes, tiene " + values.length);
        constant("LOW");
        constant("MEDIUM");
        constant("HIGH");
        constant("URGENT");
    }

    @Test
    void weightCreceDeLOW1aURGENT4() {
        int low    = callInt(constant("LOW"),    "weight");
        int medium = callInt(constant("MEDIUM"), "weight");
        int high   = callInt(constant("HIGH"),   "weight");
        int urgent = callInt(constant("URGENT"), "weight");
        assertEquals(1, low,    "LOW.weight()");
        assertEquals(2, medium, "MEDIUM.weight()");
        assertEquals(3, high,   "HIGH.weight()");
        assertEquals(4, urgent, "URGENT.weight()");
    }

    @Test
    void labelDevuelveEtiquetaLegibleNoVacia() {
        String label = callString(constant("HIGH"), "label");
        assertNotNull(label, "HIGH.label() no debe ser null");
        assertFalse(label.isBlank(), "HIGH.label() no debe estar vacío");
    }

    @Test
    void parseByNameAndNumberDevuelveHIGH() {
        Method parse;
        try {
            parse = Priority.class.getMethod("parse", String.class);
        } catch (NoSuchMethodException e) {
            assumeTrue(false, "parse(String) es opcional / aún no existe");
            return;
        }
        Object byName   = invoke(parse, null, "HIGH");
        Object byNumber = invoke(parse, null, "3");
        assertEquals(constant("HIGH"), byName,   "parse(\"HIGH\")");
        assertEquals(constant("HIGH"), byNumber, "parse(\"3\")");
    }

    // ---- helpers de reflexión ----

    private static Priority constant(String name) {
        try {
            return Priority.valueOf(name);
        } catch (IllegalArgumentException e) {
            assumeTrue(false, "falta la constante Priority." + name);
            throw new AssertionError("unreachable");
        }
    }

    private static int callInt(Priority p, String method) {
        try {
            Method m = Priority.class.getMethod(method);
            return (int) m.invoke(p);
        } catch (NoSuchMethodException e) {
            assumeTrue(false, "falta el método " + method + "()");
            throw new AssertionError("unreachable");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String callString(Priority p, String method) {
        try {
            Method m = Priority.class.getMethod(method);
            return (String) m.invoke(p);
        } catch (NoSuchMethodException e) {
            assumeTrue(false, "falta el método " + method + "()");
            throw new AssertionError("unreachable");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object invoke(Method m, Object target, Object... args) {
        try {
            return m.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(e.getCause() != null ? e.getCause() : e);
        }
    }
}
