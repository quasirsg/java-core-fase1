package com.taskflow.concurrent;

import com.taskflow.model.Task;
import com.taskflow.repository.FileTaskRepository;
import com.taskflow.service.TaskService;

import java.util.List;
import java.util.function.Consumer;

/**
 * ESQUELETO — Proceso concurrente simple del nivel básico.
 *
 * 🎯 Objetivos: ExecutorService (concretamente ScheduledExecutorService),
 * tareas periódicas, hilos daemon y cierre ORDENADO (shutdown + awaitTermination).
 *
 * Idea: cada N segundos, en un hilo aparte:
 *   1) guardar el estado en disco (autosave -> repository.flush()),
 *   2) revisar tareas vencidas (service.findOverdue()) y notificar.
 *
 * Implementa AutoCloseable para poder usarlo con try-with-resources en Main.
 *
 * PASOS SUGERIDOS:
 *  1. Crea un ScheduledExecutorService con Executors.newSingleThreadScheduledExecutor.
 *     Usa una ThreadFactory que marque el hilo como daemon (t.setDaemon(true)).
 *  2. start(periodSeconds): scheduleAtFixedRate(this::tick, period, period, SECONDS).
 *  3. tick(): try/catch para que una excepción NO mate al scheduler;
 *     dentro llama a repository.flush() y a un chequeo de vencidas.
 *  4. close(): executor.shutdown(); awaitTermination(2, SECONDS);
 *     si no termina, shutdownNow(); y al final un repository.flush() de seguridad.
 *
 * PISTAS:
 *  - import java.util.concurrent.* (Executors, ScheduledExecutorService, TimeUnit).
 *  - Para no notificar lo mismo en cada ciclo, recuerda los ids ya avisados
 *    (por ejemplo en un AtomicReference<List<String>>).
 */
public final class BackgroundScheduler implements AutoCloseable {

    private final FileTaskRepository repository;
    private final TaskService service;
    private final Consumer<List<Task>> onOverdue;

    // TODO 1: declarar el ScheduledExecutorService

    public BackgroundScheduler(FileTaskRepository repository,
                               TaskService service,
                               Consumer<List<Task>> onOverdue) {
        this.repository = repository;
        this.service = service;
        this.onOverdue = onOverdue;
        // TODO 1: inicializar el executor con un hilo daemon
    }

    /** TODO 2: programar tick() cada periodSeconds. */
    public void start(long periodSeconds) {
        throw new UnsupportedOperationException("TODO: implementar start");
    }

    /** TODO 3: una iteración (autosave + chequeo de vencidas) protegida con try/catch. */
    private void tick() {
        throw new UnsupportedOperationException("TODO: implementar tick");
    }

    /** TODO 4: cierre ordenado del executor + guardado final. */
    @Override
    public void close() {
        throw new UnsupportedOperationException("TODO: implementar close");
    }
}
