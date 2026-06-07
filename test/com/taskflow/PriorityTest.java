package com.taskflow;

import com.taskflow.model.Priority;
import com.taskflow.testkit.TestRunner;

import java.lang.reflect.Method;

/**
 * Tests para el enum Priority.
 *
 * NOTA: usamos reflexión para obtener las constantes (LOW, MEDIUM, ...) porque
 * en el esqueleto todavía no existen. Así este archivo COMPILA aunque aún no
 * hayas agregado las constantes; los tests simplemente fallarán/saltarán hasta
 * que las implementes.
 */
public final class PriorityTest {

    public static void run(TestRunner t) {
        System.out.println("== PriorityTest ==");

        t.test("existen las 4 constantes LOW, MEDIUM, HIGH, URGENT", () -> {
            Priority[] values = Priority.values();
            if (values.length == 0) {
                // Aún no implementado: que aparezca como SKIP, no como FALLO.
                throw new UnsupportedOperationException("aún no agregaste las constantes de Priority");
            }
            TestRunner.assertTrue(values.length == 4,
                    "Priority.values() debería tener 4 constantes, tiene " + values.length);
            constant("LOW");
            constant("MEDIUM");
            constant("HIGH");
            constant("URGENT");
        });

        t.test("weight() crece de LOW(1) a URGENT(4)", () -> {
            int low = callInt(constant("LOW"), "weight");
            int medium = callInt(constant("MEDIUM"), "weight");
            int high = callInt(constant("HIGH"), "weight");
            int urgent = callInt(constant("URGENT"), "weight");
            TestRunner.assertEquals(1, low, "LOW.weight()");
            TestRunner.assertEquals(2, medium, "MEDIUM.weight()");
            TestRunner.assertEquals(3, high, "HIGH.weight()");
            TestRunner.assertEquals(4, urgent, "URGENT.weight()");
        });

        t.test("label() devuelve una etiqueta legible no vacía", () -> {
            String label = callString(constant("HIGH"), "label");
            TestRunner.assertNotNull(label, "HIGH.label() no debe ser null");
            TestRunner.assertTrue(!label.isBlank(), "HIGH.label() no debe estar vacío");
        });

        t.test("parse(\"HIGH\") y parse(\"3\") devuelven HIGH (si parse existe)", () -> {
            Method parse;
            try {
                parse = Priority.class.getMethod("parse", String.class);
            } catch (NoSuchMethodException e) {
                throw new UnsupportedOperationException("parse(String) es opcional / aún no existe");
            }
            Object byName = invoke(parse, null, "HIGH");
            Object byNumber = invoke(parse, null, "3");
            TestRunner.assertEquals(constant("HIGH"), byName, "parse(\"HIGH\")");
            TestRunner.assertEquals(constant("HIGH"), byNumber, "parse(\"3\")");
        });
    }

    // ---- helpers de reflexión ----

    private static Priority constant(String name) {
        try {
            return Priority.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("falta la constante Priority." + name);
        }
    }

    private static int callInt(Priority p, String method) {
        try {
            Method m = Priority.class.getMethod(method);
            return (int) m.invoke(p);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("falta el método " + method + "()");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String callString(Priority p, String method) {
        try {
            Method m = Priority.class.getMethod(method);
            return (String) m.invoke(p);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("falta el método " + method + "()");
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
