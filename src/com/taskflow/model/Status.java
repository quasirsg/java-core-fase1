package com.taskflow.model;

/**
 * ESQUELETO — Estado de una tarea (mini máquina de estados).
 *
 * 🎯 Objetivo: que un enum encapsule REGLAS DE NEGOCIO, no solo valores.
 *
 * PASOS SUGERIDOS:
 *  1. Define las constantes: PENDING, IN_PROGRESS, DONE, CANCELLED.
 *  2. (Opcional) agrega una etiqueta legible como en Priority.
 *  3. Implementa boolean canTransitionTo(Status target) con estas reglas:
 *       - DONE y CANCELLED son TERMINALES: no se puede salir de ellos.
 *       - PENDING -> IN_PROGRESS / DONE / CANCELLED.
 *       - IN_PROGRESS -> PENDING / DONE / CANCELLED.
 *       - No tiene sentido transicionar al mismo estado.
 *  4. Implementa boolean isTerminal() (true para DONE y CANCELLED).
 *
 * PISTAS:
 *  - Un switch sobre `this` es muy cómodo: switch (this) { case PENDING -> ...; }
 *  - Para chequear pertenencia: Set.of(IN_PROGRESS, DONE, CANCELLED).contains(target).
 */
public enum Status {

    // TODO 1: declarar constantes -> PENDING, IN_PROGRESS, DONE, CANCELLED;

    ; // <-- borra este punto y coma al agregar las constantes

    // TODO 2 (opcional): campo label + getter

    // TODO 3: public boolean canTransitionTo(Status target) { ... }

    // TODO 4: public boolean isTerminal() { ... }
}
