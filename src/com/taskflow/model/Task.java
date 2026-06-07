package com.taskflow.model;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ESQUELETO — Tarea inmutable representada como {@code record}.
 *
 * 🎯 Objetivos: records, inmutabilidad, validación en el constructor compacto,
 * Comparable/Comparator, java.time y Optional.
 *
 * Este esqueleto YA COMPILA (los métodos lanzan UnsupportedOperationException).
 * Tu trabajo es reemplazar cada `throw` por la implementación real.
 *
 * PASOS SUGERIDOS:
 *  1. Constructor compacto: valida que id y title no sean vacíos, que el título
 *     no supere MAX_TITLE_LENGTH y que priority/status/createdAt no sean null.
 *     Lanza InvalidTaskException si algo falla. (Ya tienes la excepción creada.)
 *  2. Implementa la factoría create(...): genera un id con UUID.randomUUID(),
 *     estado inicial PENDING y createdAt = LocalDateTime.now().
 *  3. dueDateOpt(): devuelve Optional.ofNullable(dueDate).
 *  4. isOverdue(): true si hay dueDate, ya pasó y el estado NO es terminal.
 *  5. Métodos "with...": devuelven una COPIA con un campo cambiado (inmutabilidad).
 *  6. compareTo + Comparators estáticos para ordenar por prioridad/fecha.
 */
public record Task(
        String id,
        String title,
        String description,
        Priority priority,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime dueDate   // puede ser null
) implements Comparable<Task> {

    public static final int MAX_TITLE_LENGTH = 120;

    // TODO 1: constructor compacto con validaciones
    // public Task {
    //     if (...) throw new InvalidTaskException("...");
    // }

    /** TODO 2: factoría para crear una tarea nueva (id UUID, status PENDING, createdAt ahora). */
    public static Task create(String title, String description, Priority priority, LocalDateTime dueDate) {
        throw new UnsupportedOperationException("TODO: implementar Task.create");
    }

    /** TODO 3: devolver dueDate como Optional (nunca null hacia afuera). */
    public Optional<LocalDateTime> dueDateOpt() {
        throw new UnsupportedOperationException("TODO: implementar dueDateOpt");
    }

    /** TODO 4: true si está vencida (tiene dueDate pasada y no está en estado terminal). */
    public boolean isOverdue() {
        throw new UnsupportedOperationException("TODO: implementar isOverdue");
    }

    // ---- TODO 5: "withers" (devuelven copias inmutables) ----
    // public Task withStatus(Status newStatus) { return new Task(id, title, description, priority, newStatus, createdAt, dueDate); }
    // public Task withTitle(String newTitle) { ... }
    // public Task withDescription(String newDescription) { ... }
    // public Task withPriority(Priority newPriority) { ... }
    // public Task withDueDate(LocalDateTime newDueDate) { ... }

    // ---- TODO 6: ordenamiento ----
    @Override
    public int compareTo(Task other) {
        // Sugerencia: ordenar por prioridad descendente y luego por createdAt.
        throw new UnsupportedOperationException("TODO: implementar compareTo");
    }

    // Sugerencia de Comparators a definir como constantes estáticas:
    // public static final Comparator<Task> BY_PRIORITY_DESC = ...
    // public static final Comparator<Task> BY_CREATED_AT = Comparator.comparing(Task::createdAt);
    // public static final Comparator<Task> BY_DUE_DATE = Comparator.comparing(Task::dueDate, Comparator.nullsLast(Comparator.naturalOrder()));
}
