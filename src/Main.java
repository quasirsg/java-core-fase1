import com.taskflow.cli.ConsoleApp;
// import com.taskflow.concurrent.BackgroundScheduler; // descomenta al usar el scheduler
import com.taskflow.repository.FileTaskRepository;
import com.taskflow.service.TaskService;

import java.nio.file.Path;

/**
 * Punto de entrada de TaskFlow CLI.
 *
 * Aquí se hace el "wiring" (cableado) de las piezas: repositorio -> servicio ->
 * scheduler en background -> interfaz de consola.
 *
 * Está pensado para que, una vez implementes los esqueletos, la app arranque.
 * Mientras tanto, el scheduler está comentado para que puedas avanzar por partes.
 *
 * Compilar (desde la raíz del proyecto): ver instrucciones en doc/proyecto_fase1_cli_tareas.md.
 * Ejecutar:  java -cp out Main
 */
public class Main {

    public static void main(String[] args) {
        // 1) Persistencia: archivo donde se guardarán las tareas.
        Path dataFile = Path.of("data", "tasks.dat");
        FileTaskRepository repository = new FileTaskRepository(dataFile);

        // 2) Lógica de negocio.
        TaskService service = new TaskService(repository);

        // 3) Proceso en background (autosave + aviso de vencidas).
        //    DESCOMENTA esto cuando hayas implementado BackgroundScheduler:
        //
        // try (BackgroundScheduler scheduler = new BackgroundScheduler(
        //         repository, service,
        //         overdue -> System.out.println("\n🔔 Tienes " + overdue.size() + " tarea(s) vencida(s)."))) {
        //     scheduler.start(15); // cada 15 segundos
        //     new ConsoleApp(service).run();
        // }

        // 4) Por ahora arrancamos solo la CLI (sin background) para ir probando.
        new ConsoleApp(service).run();
    }
}
