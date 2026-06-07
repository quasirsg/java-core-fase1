package com.taskflow.model;

/**
 * ESQUELETO — Prioridad de una tarea.
 *
 * 🎯 Objetivo de aprendizaje: un enum NO es solo una lista de constantes.
 * Puede tener campos, constructor y métodos propios.
 *
 * PASOS SUGERIDOS:
 *  1. Define las constantes: LOW, MEDIUM, HIGH, URGENT.
 *  2. Agrega campos: un peso numérico (int) y una etiqueta legible (String).
 *  3. Crea el constructor privado del enum y un par de getters: weight() y label().
 *  4. (Opcional) Crea un método estático parse(String) que acepte el nombre
 *     ("HIGH") o el número (1..4) y devuelva la Priority correspondiente.
 *
 * PISTAS:
 *  - El constructor de un enum es implícitamente privado.
 *  - Para iterar las constantes usa Priority.values().
 *  - Para convertir texto a enum por nombre: Priority.valueOf(texto.toUpperCase()).
 */
public enum Priority {

    // TODO 1: declarar las constantes con sus argumentos, por ejemplo:
    // LOW(1, "Baja"), MEDIUM(2, "Media"), HIGH(3, "Alta"), URGENT(4, "Urgente");

    ; // <-- borra este punto y coma cuando agregues las constantes arriba

    // TODO 2: declarar campos finales (weight, label)

    // TODO 3: constructor del enum

    // TODO 4: getters weight() y label()

    // TODO 5 (opcional): static Priority parse(String input)
}
