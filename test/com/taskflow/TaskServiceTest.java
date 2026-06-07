package com.taskflow;

import com.taskflow.common.Result;
import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;
import com.taskflow.repository.Repository;
import com.taskflow.service.TaskService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests para TaskService.
 *
 * Usa un repositorio en memoria propio (InMemoryRepo) para que estos tests NO
 * dependan de que FileTaskRepository esté implementado. Así puedes testear la
 * lógica de negocio de forma aislada.
 */
class TaskServiceTest {

    @Test
    void createTaskConDatosValidosDevuelveResultOkYPersiste() {
        TaskService service = new TaskService(new InMemoryRepo());
        try {
            Result<Task> result = service.createTask("Comprar pan", "panadería",
                    priority("MEDIUM"), LocalDateTime.now().plusDays(1));
            assertTrue(result.isOk(), "debería ser Ok");
            assertEquals(1L, service.totalTasks(), "se persistió 1 tarea");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void createTaskConTituloVacioDevuelveResultErr() {
        TaskService service = new TaskService(new InMemoryRepo());
        try {
            Result<Task> result = service.createTask("", "x", priority("LOW"), null);
            assertFalse(result.isOk(), "título vacío debería ser Err");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void findByIdDevuelveLaTareaCreada() {
        TaskService service = new TaskService(new InMemoryRepo());
        try {
            Result<Task> created = service.createTask("Tarea", "d", priority("HIGH"), null);
            String id = ((Result.Ok<Task>) created).value().id();
            Optional<Task> found = service.findById(id);
            assertTrue(found.isPresent(), "findById debería encontrarla");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void findByStatusFiltraCorrectamente() {
        TaskService service = new TaskService(new InMemoryRepo());
        try {
            service.createTask("A", "d", priority("LOW"), null);
            service.createTask("B", "d", priority("LOW"), null);
            List<Task> pending = service.findByStatus(status("PENDING"));
            assertEquals(2, pending.size(), "ambas recién creadas están PENDING");
            List<Task> done = service.findByStatus(status("DONE"));
            assertEquals(0, done.size(), "ninguna está DONE");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void findByPriorityFiltraPorPrioridad() {
        TaskService service = new TaskService(new InMemoryRepo());
        try {
            service.createTask("A", "d", priority("URGENT"), null);
            service.createTask("B", "d", priority("LOW"), null);
            assertEquals(1, service.findByPriority(priority("URGENT")).size(),
                    "una sola URGENT");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void searchEsCaseInsensitiveEnTituloYDescripcion() {
        TaskService service = new TaskService(new InMemoryRepo());
        try {
            service.createTask("Comprar Leche", "supermercado", priority("LOW"), null);
            service.createTask("Pagar luz", "EDESUR factura", priority("LOW"), null);
            assertEquals(1, service.search("leche").size(),  "busca en título");
            assertEquals(1, service.search("edesur").size(), "busca en descripción");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void changeStatusAplicaTransicionValida() {
        TaskService service = new TaskService(new InMemoryRepo());
        try {
            Result<Task> created = service.createTask("Tarea", "d", priority("LOW"), null);
            String id = ((Result.Ok<Task>) created).value().id();
            Result<Task> changed = service.changeStatus(id, status("IN_PROGRESS"));
            assertTrue(changed.isOk(), "PENDING -> IN_PROGRESS debe ser Ok");
            assertEquals(status("IN_PROGRESS"),
                    service.findById(id).orElseThrow().status(), "estado actualizado");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void changeStatusDeIdInexistenteDevuelveErr() {
        TaskService service = new TaskService(new InMemoryRepo());
        try {
            Result<Task> changed = service.changeStatus("no-existe", status("DONE"));
            assertFalse(changed.isOk(), "id inexistente debe ser Err");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void deleteTaskEliminaLaTarea() {
        TaskService service = new TaskService(new InMemoryRepo());
        try {
            Result<Task> created = service.createTask("Tarea", "d", priority("LOW"), null);
            String id = ((Result.Ok<Task>) created).value().id();
            Result<String> deleted = service.deleteTask(id);
            assertTrue(deleted.isOk(), "delete debería ser Ok");
            assertTrue(service.findById(id).isEmpty(), "ya no existe");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void countByStatusCuentaAgrupandoPorEstado() {
        TaskService service = new TaskService(new InMemoryRepo());
        try {
            service.createTask("A", "d", priority("LOW"), null);
            service.createTask("B", "d", priority("LOW"), null);
            Map<Status, Long> counts = service.countByStatus();
            assertEquals(2L, counts.getOrDefault(status("PENDING"), 0L),
                    "2 tareas PENDING");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    @Test
    void listAllDevuelveTodasLasTareas() {
        TaskService service = new TaskService(new InMemoryRepo());
        try {
            service.createTask("A", "d", priority("LOW"), null);
            service.createTask("B", "d", priority("HIGH"), null);
            List<Task> all = service.listAll(TaskService.SortBy.PRIORITY);
            assertEquals(2, all.size(), "listAll().size()");
        } catch (UnsupportedOperationException e) {
            assumeTrue(false, "método aún no implementado: " + e.getMessage());
        }
    }

    // ---- helpers ----

    private static Priority priority(String name) {
        try {
            return Priority.valueOf(name);
        } catch (IllegalArgumentException e) {
            assumeTrue(false, "falta Priority." + name);
            throw new AssertionError("unreachable");
        }
    }

    private static Status status(String name) {
        try {
            return Status.valueOf(name);
        } catch (IllegalArgumentException e) {
            assumeTrue(false, "falta Status." + name);
            throw new AssertionError("unreachable");
        }
    }

    /** Repositorio en memoria mínimo, solo para los tests del servicio. */
    private static final class InMemoryRepo implements Repository<Task, String> {
        private final Map<String, Task> store = new LinkedHashMap<>();

        @Override
        public Task save(Task entity) {
            store.put(entity.id(), entity);
            return entity;
        }

        @Override
        public Optional<Task> findById(String id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Task> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public boolean deleteById(String id) {
            return store.remove(id) != null;
        }

        @Override
        public long count() {
            return store.size();
        }
    }
}
