package com.taskflow.service;

import com.taskflow.common.Result;
import com.taskflow.model.Priority;
import com.taskflow.model.Status;
import com.taskflow.model.Task;
import com.taskflow.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ESQUELETO — Lógica de negocio de tareas.
 *
 * 🎯 Objetivos: COMPOSICIÓN (el servicio USA un Repository, no hereda de él),
 * Streams para filtrar/ordenar/estadísticas, Optional en búsquedas y el tipo
 * Result para errores de negocio.
 *
 * Este esqueleto YA COMPILA. Implementa cada método reemplazando el `throw`.
 *
 * PISTAS GENERALES:
 *  - Para listar/filtrar: repository.findAll().stream().filter(...).sorted(...).toList()
 *  - Para estadísticas: Collectors.groupingBy / counting / partitioningBy
 *  - Para validar y reportar: Result.err("mensaje") / Result.ok(valor)
 */
public class TaskService {

    /** Criterios de ordenamiento que expondrás a la CLI. */
    public enum SortBy {PRIORITY, DUE_DATE, CREATED_AT}

    private final Repository<Task, String> repository;

    public TaskService(Repository<Task, String> repository) {
        this.repository = repository;
    }

    /** RF-1: crear tarea validando título, prioridad y fecha. Usa Task.create(...). */
    public Result<Task> createTask(String title, String description, Priority priority, LocalDateTime dueDate) {
        // TODO: validar (título no vacío, longitud, prioridad no nula, dueDate no en el pasado)
        //       y, si todo ok, guardar con repository.save(Task.create(...)).
        throw new UnsupportedOperationException("TODO: implementar createTask");
    }

    /** Búsqueda por id (devuelve Optional, nunca null). */
    public Optional<Task> findById(String id) {
        // TODO: delegar en repository.findById(id)
        throw new UnsupportedOperationException("TODO: implementar findById");
    }

    /** RF-2: listar todas ordenadas según el criterio. */
    public List<Task> listAll(SortBy sortBy) {
        // TODO: stream + sorted(comparatorFor(sortBy)) + toList()
        throw new UnsupportedOperationException("TODO: implementar listAll");
    }

    /** RF-3: filtrar por estado. */
    public List<Task> findByStatus(Status status) {
        throw new UnsupportedOperationException("TODO: implementar findByStatus");
    }

    /** RF-3: filtrar por prioridad. */
    public List<Task> findByPriority(Priority priority) {
        throw new UnsupportedOperationException("TODO: implementar findByPriority");
    }

    /** RF-3: búsqueda textual (case-insensitive) en título o descripción. */
    public List<Task> search(String text) {
        throw new UnsupportedOperationException("TODO: implementar search");
    }

    /** RF-3: tareas vencidas. Usa Task::isOverdue. */
    public List<Task> findOverdue() {
        throw new UnsupportedOperationException("TODO: implementar findOverdue");
    }

    /** RF-4: cambiar estado respetando Status.canTransitionTo(...). */
    public Result<Task> changeStatus(String id, Status newStatus) {
        // TODO: buscar por id (si no existe -> Result.err),
        //       validar la transición y guardar task.withStatus(newStatus).
        throw new UnsupportedOperationException("TODO: implementar changeStatus");
    }

    /** RF-5: editar campos (los null se ignoran y conservan el valor anterior). */
    public Result<Task> editTask(String id, String title, String description,
                                 Priority priority, LocalDateTime dueDate) {
        throw new UnsupportedOperationException("TODO: implementar editTask");
    }

    /** RF-6: eliminar por id. */
    public Result<String> deleteTask(String id) {
        throw new UnsupportedOperationException("TODO: implementar deleteTask");
    }

    // ---- RF-7: estadísticas con Streams ----

    /** Conteo por estado: Collectors.groupingBy(Task::status, Collectors.counting()). */
    public Map<Status, Long> countByStatus() {
        throw new UnsupportedOperationException("TODO: implementar countByStatus");
    }

    /** Conteo por prioridad. */
    public Map<Priority, Long> countByPriority() {
        throw new UnsupportedOperationException("TODO: implementar countByPriority");
    }

    /** Partición vencidas / no vencidas: Collectors.partitioningBy(Task::isOverdue). */
    public Map<Boolean, List<Task>> partitionOverdue() {
        throw new UnsupportedOperationException("TODO: implementar partitionOverdue");
    }

    public long totalTasks() {
        return repository.count();
    }

    // ---- Helper sugerido ----
    // private Comparator<Task> comparatorFor(SortBy sortBy) {
    //     return switch (sortBy) {
    //         case PRIORITY -> Task.BY_PRIORITY_DESC.thenComparing(Task.BY_CREATED_AT);
    //         case DUE_DATE -> Task.BY_DUE_DATE;
    //         case CREATED_AT -> Task.BY_CREATED_AT;
    //     };
    // }
}
