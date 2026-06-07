package com.taskflow;

import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;
import com.taskflow.repository.FileTaskRepository;
import com.taskflow.testkit.TestRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Tests para FileTaskRepository (parte en memoria + persistencia).
 *
 * Crea un archivo temporal por test para no ensuciar el proyecto.
 * Mientras los métodos lancen UnsupportedOperationException, se marcan SKIP.
 */
public final class FileTaskRepositoryTest {

    public static void run(TestRunner t) {
        System.out.println("== FileTaskRepositoryTest ==");

        t.test("save + count + findById", () -> {
            FileTaskRepository repo = newRepo();
            Task task = sampleTask("id-1", "Tarea uno");
            Task saved = repo.save(task);
            TestRunner.assertEquals(task, saved, "save devuelve la misma entidad");
            TestRunner.assertEquals(1L, repo.count(), "count tras 1 save");
            Optional<Task> found = repo.findById("id-1");
            TestRunner.assertTrue(found.isPresent(), "findById debería encontrar la tarea");
            TestRunner.assertEquals("Tarea uno", found.get().title(), "título recuperado");
        });

        t.test("findById de id inexistente devuelve Optional vacío", () -> {
            FileTaskRepository repo = newRepo();
            TestRunner.assertTrue(repo.findById("no-existe").isEmpty(),
                    "findById de id inexistente debe ser vacío");
        });

        t.test("findAll devuelve todas las tareas guardadas", () -> {
            FileTaskRepository repo = newRepo();
            repo.save(sampleTask("a", "A"));
            repo.save(sampleTask("b", "B"));
            List<Task> all = repo.findAll();
            TestRunner.assertEquals(2, all.size(), "findAll().size()");
        });

        t.test("deleteById elimina y devuelve true; segunda vez false", () -> {
            FileTaskRepository repo = newRepo();
            repo.save(sampleTask("x", "X"));
            TestRunner.assertTrue(repo.deleteById("x"), "primer delete devuelve true");
            TestRunner.assertFalse(repo.deleteById("x"), "segundo delete devuelve false");
            TestRunner.assertEquals(0L, repo.count(), "count tras borrar");
        });

        t.test("flush + load: persiste y recupera desde archivo", () -> {
            Path file = tempFile();
            FileTaskRepository repo = new FileTaskRepository(file);
            repo.save(sampleTask("persist-1", "Persistente"));
            repo.flush();

            FileTaskRepository reloaded = new FileTaskRepository(file);
            reloaded.load();
            Optional<Task> found = reloaded.findById("persist-1");
            TestRunner.assertTrue(found.isPresent(), "tras load debería existir la tarea");
            TestRunner.assertEquals("Persistente", found.get().title(), "título tras recargar");
        });
    }

    // ---- helpers ----

    private static FileTaskRepository newRepo() {
        return new FileTaskRepository(tempFile());
    }

    private static Path tempFile() {
        try {
            Path dir = Files.createTempDirectory("taskflow-test");
            return dir.resolve("tasks.dat");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Task sampleTask(String id, String title) {
        try {
            return new Task(id, title, "descripción", Priority.valueOf("MEDIUM"),
                    Status.valueOf("PENDING"), LocalDateTime.now(), null);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(
                    "faltan constantes de Priority/Status (impleméntalas primero)");
        }
    }
}
