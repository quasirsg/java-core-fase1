package com.taskflow.testkit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Mini-framework de tests SIN dependencias externas (no necesita JUnit).
 *
 * Idea: cada "test" es un nombre + un bloque de código (Runnable). Dentro del
 * bloque usas los métodos de aserción (check, assertEquals, assertTrue...).
 * Si una aserción falla, lanza AssertionError y el test se marca como FAIL.
 *
 * Esto permite ejecutar todo con solo el JDK:
 *   javac -d out @sources.txt
 *   java -cp out com.taskflow.testkit.RunAllTests
 */
public final class TestRunner {

    private final String suiteName;
    private int passed = 0;
    private int failed = 0;
    private int skipped = 0;
    private final List<String> failures = new ArrayList<>();

    public TestRunner(String suiteName) {
        this.suiteName = suiteName;
    }

    /** Ejecuta un test. Si lanza cualquier excepción/AssertionError -> FAIL. */
    public void test(String name, Runnable body) {
        try {
            body.run();
            passed++;
            System.out.println("  [ OK ] " + name);
        } catch (UnsupportedOperationException e) {
            // El método del esqueleto todavía no está implementado.
            skipped++;
            System.out.println("  [SKIP] " + name + "  (aún sin implementar: " + e.getMessage() + ")");
        } catch (AssertionError e) {
            failed++;
            String msg = "  [FAIL] " + name + "  -> " + e.getMessage();
            failures.add(suiteName + " :: " + name + " -> " + e.getMessage());
            System.out.println(msg);
        } catch (Throwable t) {
            failed++;
            String msg = "  [FAIL] " + name + "  -> Excepción inesperada: " + t;
            failures.add(suiteName + " :: " + name + " -> " + t);
            System.out.println(msg);
        }
    }

    // ---------------- Aserciones ----------------

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Se esperaba true: " + message);
        }
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("Se esperaba false: " + message);
        }
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " | esperado=<" + expected + "> obtenido=<" + actual + ">");
        }
    }

    public static void assertNotNull(Object value, String message) {
        if (value == null) {
            throw new AssertionError("Se esperaba un valor no nulo: " + message);
        }
    }

    public static void assertNull(Object value, String message) {
        if (value != null) {
            throw new AssertionError("Se esperaba null: " + message + " (obtenido=" + value + ")");
        }
    }

    /** Verifica que el bloque lance una excepción del tipo esperado. */
    public static void assertThrows(Class<? extends Throwable> expectedType, Runnable body, String message) {
        try {
            body.run();
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) {
                return; // ok
            }
            throw new AssertionError(message + " | se lanzó " + t.getClass().getSimpleName()
                    + " pero se esperaba " + expectedType.getSimpleName());
        }
        throw new AssertionError(message + " | no se lanzó ninguna excepción (se esperaba "
                + expectedType.getSimpleName() + ")");
    }

    public static <T> void assertMatches(T value, Predicate<T> predicate, String message) {
        if (!predicate.test(value)) {
            throw new AssertionError(message + " | valor=" + value);
        }
    }

    // ---------------- Resumen ----------------

    public int getFailed() {
        return failed;
    }

    public List<String> getFailures() {
        return failures;
    }

    public void printSummary() {
        System.out.println("  ---------------------------------------------");
        System.out.printf("  %s: %d OK, %d FALLOS, %d sin implementar%n",
                suiteName, passed, failed, skipped);
        System.out.println();
    }
}
