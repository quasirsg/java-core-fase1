package com.taskflow.model;

/**
 * ESQUELETO — Prioridad de una tarea.
 * 🎯 Objetivo de aprendizaje: un enum NO es solo una lista de constantes.
 * Puede tener campos, constructor y métodos propios.
 * PASOS SUGERIDOS:
 *  1. Define las constantes: LOW, MEDIUM, HIGH, URGENT.
 *  2. Agrega campos: un peso numérico (int) y una etiqueta legible (String).
 *  3. Crea el constructor privado del enum y un par de getters: weight() y label().
 *  4. (Opcional) Crea un método estático parse(String) que acepte el nombre
 *     ("HIGH") o el número (1..4) y devuelva la Priority correspondiente.
 * PISTAS:
 *  - El constructor de un enum es implícitamente privado.
 *  - Para iterar las constantes usa Priority.values().
 *  - Para convertir texto a enum por nombre: Priority.valueOf(texto.toUpperCase()).
 */
public enum Priority {

    // TODO 1: declarar las constantes con sus argumentos, por ejemplo:
    // LOW(1, "Baja"), MEDIUM(2, "Media"), HIGH(3, "Alta"), URGENT(4, "Urgente");
    LOW(1,"Baja"),
    MEDIUM(2,"Media"),
    HIGH(3,"Alta"),
    URGENT(4,"Urgente");
    // TODO 2: declarar campos finales (weight, label)
    private final Integer weight;
    private final String label;
    // TODO 3: constructor del enum
    Priority(Integer weight, String label) {
        this.weight = weight;
        this.label = label;
    }
    // TODO 4: getters weight() y label()
    @SuppressWarnings("unused")
    public Integer weight() {
        return weight;
    }
    @SuppressWarnings("unused")
    public String label() {
        return label;
    }
    // TODO 5 (opcional): static Priority parse(String input)
    public static Priority parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("La prioridad no puede ser nula o vacía");
        }

        String normalizedInput = input.trim();

        for (Priority priority : Priority.values()) {

            // Buscar por label: "Baja", "Media", "Alta", "Urgente"
            if (priority.label.equalsIgnoreCase(normalizedInput)) {
                return priority;
            }

            // Buscar por nombre del enum: "LOW", "MEDIUM", "HIGH", "URGENT"
            if (priority.name().equalsIgnoreCase(normalizedInput)) {
                return priority;
            }

            // Buscar por weight: "1", "2", "3", "4"
            if (priority.weight.toString().equals(normalizedInput)) {
                return priority;
            }
        }

        throw new IllegalArgumentException("Prioridad inválida: " + input);
    }
}
