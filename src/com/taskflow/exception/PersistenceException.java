package com.taskflow.exception;

/**
 * Excepción para fallos de persistencia (lectura/escritura del archivo de tareas).
 * Se modela como unchecked para no contaminar toda la API con {@code throws},
 * pero siempre conserva la causa original de I/O.
 */
public class PersistenceException extends RuntimeException {

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(String message) {
        super(message);
    }
}
