package com.taskflow.model;

import com.taskflow.exception.InvalidTaskException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

/**
 * ESQUELETO — Tarea inmutable representada como {@code record}.
 * 🎯 Objetivos: records, inmutabilidad, validación en el constructor compacto,
 * Comparable/Comparator, java.time y Optional.
 * Este esqueleto YA COMPILA (los métodos lanzan UnsupportedOperationException).
 * Tu trabajo es reemplazar cada `throw` por la implementación real.
 * PASOS SUGERIDOS:
 * 1. Constructor compacto: valida que id y title no sean vacíos, que el título
 * no supere MAX_TITLE_LENGTH y que priority/status/createdAt no sean null.
 * Lanza InvalidTaskException si algo falla. (Ya tienes la excepción creada.)
 * 2. Implementa la factoría create(...): genera un id con UUID.randomUUID(),
 * estado inicial PENDING y createdAt = LocalDateTime.now().
 * 3. dueDateOpt(): devuelve Optional.ofNullable(dueDate).
 * 4. isOverdue(): true si hay dueDate, ya pasó y el estado NO es terminal.
 * 5. Métodos "with...": devuelven una COPIA con un campo cambiado (inmutabilidad).
 * 6. compareTo + Comparators estáticos para ordenar por prioridad/fecha.
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
    public Task {
        if (id == null) throw new InvalidTaskException("Id invalido");
        if (title == null || title.length() > Task.MAX_TITLE_LENGTH)
            throw new InvalidTaskException("Title invalido");
        if (description == null) throw new InvalidTaskException("Description invalido");
        if (priority == null) throw new InvalidTaskException("Priority invalido");
        if (status == null) throw new InvalidTaskException("Status invalido");
        if (createdAt == null) throw new InvalidTaskException("CreatedAt invalido");
    }

    /**
     * TODO 2: factoría para crear una tarea nueva (id UUID, status PENDING, createdAt ahora).
     */
    @org.jetbrains.annotations.NotNull
    @org.jetbrains.annotations.Contract("_, _, _, _ -> new")
    public static Task create(String title, String description, Priority priority, LocalDateTime dueDate) {
        return new Task(UUID.randomUUID().toString(), title, description, priority, Status.PENDING, LocalDateTime.now(), dueDate);
    }

    /**
     * TODO 3: devolver dueDate como Optional (nunca null hacia afuera).
     */
    public Optional<LocalDateTime> dueDateOpt() {
        if (dueDate == null) return Optional.empty();
        return Optional.of(dueDate);
    }

    /**
     * TODO 4: true si está vencida (tiene dueDate pasada y no está en estado terminal).
     */
    public boolean isOverdue() {
        if (dueDate == null || (dueDate.isAfter(createdAt) && status.isTerminal())) return false;
        return true;
    }

    // ---- TODO 5: "withers" (devuelven copias inmutables) ----
    public Task withStatus(Status newStatus) {
        return new Task(id, title, description, priority, newStatus, createdAt, dueDate);
    }

    public Task withTitle(String newTitle) {
        return new Task(id, newTitle, description, priority, status, createdAt, dueDate);
    }

    public Task withDescription(String newDescription) {
        return new Task(id, title, newDescription, priority, status, createdAt, dueDate);
    }

    public Task withPriority(Priority newPriority) {
        return new Task(id, title, description, newPriority, status, createdAt, dueDate);
    }

    public Task withDueDate(LocalDateTime newDueDate) {
        return new Task(id, title, description, priority, status, createdAt, newDueDate);
    }

    // ---- TODO 6: ordenamiento ----

    /**
     * Orden natural: prioridad descendente (URGENT primero, LOW último).
     * Si la prioridad es igual, desempata por createdAt ascendente (más antigua primero).
     */
    @Override
    public int compareTo(Task other) {
        // Orden descendente por weight: mayor weight → menor índice en la lista
        int cmp = Integer.compare(other.priority().weight(), this.priority().weight());
        if (cmp != 0) return cmp;
        // Desempate: createdAt ascendente (la más antigua primero)
        return this.createdAt().compareTo(other.createdAt());
    }

    /** Ordena por prioridad descendente (URGENT → LOW), luego por createdAt ascendente. */
    public static final Comparator<Task> BY_PRIORITY_DESC =
            Comparator.comparingInt((Task t) -> t.priority().weight())
                      .reversed()
                      .thenComparing(Task::createdAt);

    /** Ordena por fecha de creación ascendente (la más antigua primero). */
    public static final Comparator<Task> BY_CREATED_AT =
            Comparator.comparing(Task::createdAt);

    /** Ordena por fecha de vencimiento ascendente; las tareas sin fecha van al final. */
    public static final Comparator<Task> BY_DUE_DATE =
            Comparator.comparing(Task::dueDate, Comparator.nullsLast(Comparator.naturalOrder()));
}
