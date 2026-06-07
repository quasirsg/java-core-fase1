package com.taskflow.testkit;

import com.taskflow.FileTaskRepositoryTest;
import com.taskflow.PriorityTest;
import com.taskflow.StatusTest;
import com.taskflow.TaskServiceTest;
import com.taskflow.TaskTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Punto de entrada que ejecuta TODAS las suites de tests.
 *
 * Ejecutar (desde la raíz, con el classpath que incluye out):
 *   java -cp out com.taskflow.testkit.RunAllTests
 *
 * Códigos de salida:
 *   0 -> no hubo fallos (los "sin implementar" no cuentan como fallo)
 *   1 -> hubo al menos un test en FAIL
 */
public final class RunAllTests {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("         TaskFlow CLI - Suite de Tests");
        System.out.println("=================================================");
        System.out.println();

        List<TestRunner> runners = new ArrayList<>();

        runners.add(runSuite("Priority", PriorityTest::run));
        runners.add(runSuite("Status", StatusTest::run));
        runners.add(runSuite("Task", TaskTest::run));
        runners.add(runSuite("FileTaskRepository", FileTaskRepositoryTest::run));
        runners.add(runSuite("TaskService", TaskServiceTest::run));

        // Resumen global
        int totalFailed = 0;
        List<String> allFailures = new ArrayList<>();
        for (TestRunner r : runners) {
            totalFailed += r.getFailed();
            allFailures.addAll(r.getFailures());
        }

        System.out.println("=================================================");
        if (totalFailed == 0) {
            System.out.println("  RESULTADO GLOBAL: ✅ Sin fallos");
            System.out.println("  (Los tests marcados [SKIP] corresponden a métodos");
            System.out.println("   que todavía no implementaste. Impleméntalos y");
            System.out.println("   volverán a ejecutarse.)");
        } else {
            System.out.println("  RESULTADO GLOBAL: ❌ " + totalFailed + " test(s) en FALLO");
            System.out.println("  -----------------------------------------------");
            for (String f : allFailures) {
                System.out.println("   - " + f);
            }
        }
        System.out.println("=================================================");

        System.exit(totalFailed == 0 ? 0 : 1);
    }

    private interface Suite {
        void run(TestRunner t);
    }

    private static TestRunner runSuite(String name, Suite suite) {
        TestRunner runner = new TestRunner(name);
        suite.run(runner);
        runner.printSummary();
        return runner;
    }
}
