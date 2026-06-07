package com.taskflow;

import com.taskflow.common.Result;
import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;
import com.taskflow.repository.Repository;
import com.taskflow.service.TaskService;
import com.taskflow.testkit.TestRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Tests para TaskService.
 *
 * Usa un repositorio en memoria propio (InMemoryRepo) para que estos tests NO
 * dependan de que FileTaskRepository esté implementado. Así puedes testear la
 * lógica de negocio de forma aislada.
 */
public final class TaskServiceTest {

    public static void run(TestRunner t) {
        System.out.println("== TaskServiceTest ==");

        t.test("createTask con datos válidos devuelve Result.Ok y persiste", () -> {
            TaskService service = new TaskService(new InMemoryRepo());
            Result<Task> result = service.createTask("Comprar pan", "panadería",
                    priority("MEDIUM"), LocalDateTime.now().plusDays(1));
            TestRunner.assertTrue(result.isOk(), "debería ser Ok");
            TestRunner.assertEquals(1L, service.totalTasks(), "se persistió 1 tarea");
        });

        t.test("createTask con título vacío devuelve Result.Err", () -> {
            TaskService service = new TaskService(new InMemoryRepo());
            Result<Task> result = service.createTask("", "x", priority("LOW"), null);
            TestRunner.assertFalse(result.isOk(), "título vacío debería ser Err");
        });

        t.test("findById devuelve la tarea creada", () -> {
            TaskService service = new TaskService(new InMemoryRepo());
            Result<Task> created = service.createTask("Tarea", "d", priority("HIGH"), null);
            String id = ((Result.Ok<Task>) created).value().id();
            Optional<Task> found = service.findById(id);
            TestRunner.assertTrue(found.isPresent(), "findById debería encontrarla");
        });

        t.test("findByStatus filtra correctamente", () -> {
            TaskService service = new TaskService(new InMemoryRepo());
            service.createTask("A", "d", priority("LOW"), null);
            service.createTask("B", "d", priority("LOW"), null);
            List<Task> pending = service.findByStatus(status("PENDING"));
            TestRunner.assertEquals(2, pending.size(), "ambas recién creadas están PENDING");
            List<Task> done = service.findByStatus(status("DONE"));
            TestRunner.assertEquals(0, done.size(), "ninguna está DONE");
        });

        t.test("findByPriority filtra por prioridad", () -> {
            TaskService service = new TaskService(new InMemoryRepo());
            service.createTask("A", "d", priority("URGENT"), null);
            service.createTask("B", "d", priority("LOW"), null);
            TestRunner.assertEquals(1, service.findByPriority(priority("URGENT")).size(),
                    "una sola URGENT");
        });

        t.test("search es case-insensitive en título y descripción", () -> {
            TaskService service = new TaskService(new InMemoryRepo());
            service.createTask("Comprar Leche", "supermercado", priority("LOW"), null);
            service.createTask("Pagar luz", "EDESUR factura", priority("LOW"), null);
            TestRunner.assertEquals(1, service.search("leche").size(), "busca en título");
            TestRunner.assertEquals(1, service.search("edesur").size(), "busca en descripción");
        });

        t.test("changeStatus aplica una transición válida", () -> {
            TaskService service = new TaskService(new InMemoryRepo());
            Result<Task> created = service.createTask("Tarea", "d", priority("LOW"), null);
            String id = ((Result.Ok<Task>) created).value().id();
            Result<Task> changed = service.changeStatus(id, status("IN_PROGRESS"));
            TestRunner.assertTrue(changed.isOk(), "PENDING -> IN_PROGRESS debe ser Ok");
            TestRunner.assertEquals(status("IN_PROGRESS"),
                    service.findById(id).orElseThrow().status(), "estado actualizado");
        });

        t.test("changeStatus de id inexistente devuelve Err", () -> {
            TaskService service = new TaskService(new InMemoryRepo());
            Result<Task> changed = service.changeStatus("no-existe", status("DONE"));
            TestRunner.assertFalse(changed.isOk(), "id inexistente debe ser Err");
        });

        t.test("deleteTask elimina la tarea", () -> {
            TaskService service = new TaskService(new InMemoryRepo());
            Result<Task> created = service.createTask("Tarea", "d", priority("LOW"), null);
            String id = ((Result.Ok<Task>) created).value().id();
            Result<String> deleted = service.deleteTask(id);
            TestRunner.assertTrue(deleted.isOk(), "delete debería ser Ok");
            TestRunner.assertTrue(service.findById(id).isEmpty(), "ya no existe");
        });

        t.test("countByStatus cuenta agrupando por estado", () -> {
            TaskService service = new TaskService(new InMemoryRepo());
            service.createTask("A", "d", priority("LOW"), null);
            service.createTask("B", "d", priority("LOW"), null);
            Map<Status, Long> counts = service.countByStatus();
            TestRunner.assertEquals(2L, counts.getOrDefault(status("PENDING"), 0L),
                    "2 tareas PENDING");
        });

        t.test("listAll devuelve todas las tareas", () -> {
            TaskService service = new TaskService(new InMemoryRepo());
            service.createTask("A", "d", priority("LOW"), null);
            service.createTask("B", "d", priority("HIGH"), null);
            List<Task> all = service.listAll(TaskService.SortBy.PRIORITY);
            TestRunner.assertEquals(2, all.size(), "listAll().size()");
        });
    }

    // ---- helpers ----

    private static Priority priority(String name) {
        try {
            return Priority.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("falta Priority." + name);
        }
    }

    private static Status status(String name) {
        try {
            return Status.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("falta Status." + name);
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
