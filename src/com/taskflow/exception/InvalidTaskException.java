package com.taskflow.exception;

/**
 * Excepción <b>unchecked</b> para violaciones de invariantes del dominio
 * (datos de tarea inválidos, transiciones de estado no permitidas, etc.).
 */
public class InvalidTaskException extends RuntimeException {

    public InvalidTaskException(String message) {
        super(message);
    }

    public InvalidTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
