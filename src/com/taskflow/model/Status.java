package com.taskflow.model;

import java.util.Set;
import java.util.EnumSet;

/**
 * ESQUELETO — Estado de una tarea (mini máquina de estados).
 * <p>
 * 🎯 Objetivo: que un enum encapsule REGLAS DE NEGOCIO, no solo valores.
 * <p>
 * PASOS SUGERIDOS:
 * 1. Define las constantes: PENDING, IN_PROGRESS, DONE, CANCELLED.
 * 2. (Opcional) agrega una etiqueta legible como en Priority.
 * 3. Implementa boolean canTransitionTo(Status target) con estas reglas:
 * - DONE y CANCELLED son TERMINALES: no se puede salir de ellos.
 * - PENDING -> IN_PROGRESS / DONE / CANCELLED.
 * - IN_PROGRESS -> PENDING / DONE / CANCELLED.
 * - No tiene sentido transicionar al mismo estado.
 * 4. Implementa boolean isTerminal() (true para DONE y CANCELLED).
 * <p>
 * PISTAS:
 * - Un switch sobre `this` es muy cómodo: switch (this) { case PENDING -> ...; }
 * - Para chequear pertenencia: Set.of(IN_PROGRESS, DONE, CANCELLED).contains(target).
 */
public enum Status {

    // TODO 1: declarar constantes -> PENDING, IN_PROGRESS, DONE, CANCELLED;
    PENDING("PENDING", false),
    IN_PROGRESS("IN_PROGRESS", false),
    DONE("DONE", true),
    CANCELLED("CANCELLED", true);

    // TODO 2 (opcional): campo label + getter
    private final String label;
    private final boolean terminal;

    private Set<Status> allowedTransitions;

    Status(String label, boolean terminal) {
        this.label = label;
        this.terminal = terminal;
    }

    static {
        PENDING.allowedTransitions = EnumSet.of(IN_PROGRESS, DONE, CANCELLED);
        IN_PROGRESS.allowedTransitions = EnumSet.of(DONE, CANCELLED, PENDING);
        DONE.allowedTransitions = EnumSet.noneOf(Status.class);
        CANCELLED.allowedTransitions = EnumSet.noneOf(Status.class);
    }

    public String getLabel() {
        return label;
    }

    // TODO 3: public boolean canTransitionTo(Status target) { ... }
    public boolean canTransitionTo(Status target) {
        if (target == null) {
            return false;
        }

        return allowedTransitions.contains(target);
    }

    // TODO 4: public boolean isTerminal() { ... }
    public boolean isTerminal() {
        return terminal;
    }
}
