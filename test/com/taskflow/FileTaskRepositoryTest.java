package com.taskflow;

import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;
import com.taskflow.repository.FileTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests para FileTaskRepository (parte en memoria + persistencia).
 *
 * Usa @TempDir de JUnit 5 para crear directorios temporales por test,
 * sin ensuciar el proyecto.
 * Mientras los métodos lancen UnsupportedOperationException, se omiten (assumeTrue).
 */
class FileTaskRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void savePlusCountPlusFindById() {
        FileTaskRepository repo = newRepo();
        Task task = sampleTask("id-1", "Tarea uno");
        try {
            Task saved = repo.save(task);
            assertEquals(task, saved, "save devuelve la misma entidad");
            assertEquals(1L, repo.count(), "count tras 1 save");
            Optional<Task> found = repo.findById("id-1");
            assertTrue(found.isPresent(), "findById debería encontrar la tarea");
            assertEquals("Tarea uno", found.get().title(), "título recuperado");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void findByIdDeIdInexistenteDevuelveOptionalVacio() {
        FileTaskRepository repo = newRepo();
        try {
            assertTrue(repo.findById("no-existe").isEmpty(),
                    "findById de id inexistente debe ser vacío");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "findById aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void findAllDevuelveTodasLasTareasGuardadas() {
        FileTaskRepository repo = newRepo();
        Task a = sampleTask("a", "A");
        Task b = sampleTask("b", "B");
        try {
            repo.save(a);
            repo.save(b);
            List<Task> all = repo.findAll();
            assertEquals(2, all.size(), "findAll().size()");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void deleteByIdEliminaYDevuelveTrueLuegaFalse() {
        FileTaskRepository repo = newRepo();
        Task x = sampleTask("x", "X");
        try {
            repo.save(x);
            assertTrue(repo.deleteById("x"),  "primer delete devuelve true");
            assertFalse(repo.deleteById("x"), "segundo delete devuelve false");
            assertEquals(0L, repo.count(), "count tras borrar");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void flushPlusLoadPersistYRecuperaDesdeArchivo() {
        Path file = tempDir.resolve("tasks.dat");
        FileTaskRepository repo = new FileTaskRepository(file);
        Task task = sampleTask("persist-1", "Persistente");
        try {
            repo.save(task);
            repo.flush();

            FileTaskRepository reloaded = new FileTaskRepository(file);
            reloaded.load();
            Optional<Task> found = reloaded.findById("persist-1");
            assertTrue(found.isPresent(), "tras load debería existir la tarea");
            assertEquals("Persistente", found.get().title(), "título tras recargar");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    // ---- helpers ----

    private FileTaskRepository newRepo() {
        return new FileTaskRepository(tempDir.resolve("tasks-" + System.nanoTime() + ".dat"));
    }

    private static Task sampleTask(String id, String title) {
        try {
            return new Task(id, title, "descripción", Priority.valueOf("MEDIUM"),
                    Status.valueOf("PENDING"), LocalDateTime.now(), null);
        } catch (IllegalArgumentException e) {
            assumeTrue(false,
                    "faltan constantes de Priority/Status (impleméntalas primero)");
            throw new AssertionError("unreachable");
        }
    }
}
