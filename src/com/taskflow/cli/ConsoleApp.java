package com.taskflow.cli;

import com.taskflow.service.TaskService;

import java.util.Scanner;

/**
 * ESQUELETO — Interfaz de línea de comandos (menú interactivo).
 *
 * 🎯 Objetivos: leer entrada del usuario sin que la app crashee, mostrar datos
 * de forma legible y conectar la UI con el TaskService.
 *
 * Te dejamos el ESQUELETO DEL BUCLE PRINCIPAL ya armado (lee una opción y hace
 * switch). Tu trabajo es completar cada handler (los métodos handleXxx) y los
 * helpers de lectura (readNonEmpty, readPriority, readDate, etc.).
 *
 * PISTAS:
 *  - Usa un único Scanner(System.in) para toda la app.
 *  - Envuelve los parseos en try/catch para no romper ante entradas inválidas.
 *  - Para mostrar tablas, String.format("%-36s %-20s ...", ...) ayuda a alinear.
 *  - Conecta cada opción con un método del TaskService y muestra el Result
 *    con result.match(ok -> ..., err -> ...).
 */
public class ConsoleApp {

    private final TaskService service;
    private final Scanner in = new Scanner(System.in);
    private boolean running = true;

    public ConsoleApp(TaskService service) {
        this.service = service;
    }

    /** Bucle principal del menú (ya implementado como punto de partida). */
    public void run() {
        System.out.println("=== TaskFlow CLI ===");
        while (running) {
            printMenu();
            String option = in.nextLine().trim();
            try {
                handleOption(option);
            } catch (RuntimeException e) {
                // Nunca dejar que una excepción tumbe la app
                System.out.println("⚠ Error: " + e.getMessage());
            }
        }
        System.out.println("¡Hasta luego!");
    }

    private void printMenu() {
        System.out.println("""
                ------------------------------
                1) Crear tarea
                2) Listar tareas
                3) Buscar / filtrar
                4) Cambiar estado
                5) Editar tarea
                6) Eliminar tarea
                7) Estadísticas
                0) Salir
                ------------------------------""");
        System.out.print("Elige una opción: ");
    }

    private void handleOption(String option) {
        switch (option) {
            case "1" -> handleCreate();
            case "2" -> handleList();
            case "3" -> handleSearch();
            case "4" -> handleChangeStatus();
            case "5" -> handleEdit();
            case "6" -> handleDelete();
            case "7" -> handleStats();
            case "0" -> running = false;
            default -> System.out.println("Opción no válida.");
        }
    }

    // ----------------- TODO: completar cada handler -----------------

    /** TODO: pedir título, descripción, prioridad y (opcional) fecha; llamar a service.createTask(...). */
    private void handleCreate() {
        System.out.println("[TODO] handleCreate: pedir datos y llamar a service.createTask(...)");
    }

    /** TODO: pedir criterio de orden y mostrar service.listAll(sortBy) en forma de tabla. */
    private void handleList() {
        System.out.println("[TODO] handleList: mostrar service.listAll(...)");
    }

    /** TODO: submenú para filtrar por estado/prioridad/texto/vencidas. */
    private void handleSearch() {
        System.out.println("[TODO] handleSearch: filtros con service.findByStatus / search / findOverdue");
    }

    /** TODO: pedir id + nuevo estado; llamar a service.changeStatus(...). */
    private void handleChangeStatus() {
        System.out.println("[TODO] handleChangeStatus: service.changeStatus(id, nuevoEstado)");
    }

    /** TODO: pedir id + campos a editar; llamar a service.editTask(...). */
    private void handleEdit() {
        System.out.println("[TODO] handleEdit: service.editTask(...)");
    }

    /** TODO: pedir id + confirmación; llamar a service.deleteTask(...). */
    private void handleDelete() {
        System.out.println("[TODO] handleDelete: service.deleteTask(id)");
    }

    /** TODO: mostrar countByStatus / countByPriority / vencidas. */
    private void handleStats() {
        System.out.println("[TODO] handleStats: service.countByStatus(), countByPriority(), etc.");
    }

    // ----------------- Helpers sugeridos (a implementar) -----------------
    // private String readNonEmpty(String prompt) { ... }
    // private Priority readPriority() { ... }       // usa Priority.parse(...)
    // private LocalDateTime readOptionalDate() { ... } // formato "yyyy-MM-dd HH:mm"
}
