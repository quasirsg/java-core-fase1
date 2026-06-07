package com.taskflow.repository;

import com.taskflow.model.Task;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ESQUELETO — Repositorio de tareas en memoria + persistencia en archivo.
 *
 * 🎯 Objetivos: implementar una interfaz genérica, usar colecciones
 * (idealmente thread-safe porque el guardado correrá en un hilo aparte),
 * NIO de archivos (java.nio.file.Files) y manejo de errores de I/O.
 *
 * Este esqueleto YA COMPILA. Reemplaza los `throw` por la lógica real.
 *
 * PASOS SUGERIDOS:
 *  1. save: store.put(entity.id(), entity); return entity;
 *  2. findById: Optional.ofNullable(store.get(id)).
 *  3. findAll: devolver una COPIA (new ArrayList<>(store.values())).
 *  4. deleteById: store.remove(id) != null.
 *  5. count: store.size().
 *  6. load(): si el archivo existe, leer líneas (Files.readAllLines) y
 *     reconstruir cada Task. Lanza PersistenceException si falla la lectura.
 *  7. flush(): serializar cada Task a una línea y escribir (Files.write).
 *     Crea el directorio padre si no existe (Files.createDirectories).
 *
 * IDEA DE FORMATO (simple): separa los 7 campos de Task por un carácter raro
 * como "\u001F" (Unit Separator) para no chocar con comas del usuario.
 * Para fechas usa DateTimeFormatter.ISO_LOCAL_DATE_TIME; representa dueDate
 * nula con un token especial.
 */
public final class FileTaskRepository implements Repository<Task, String> {

    private final Path file;
    private final Map<String, Task> store = new ConcurrentHashMap<>();

    public FileTaskRepository(Path file) {
        this.file = file;
        // load(); // <-- descomenta cuando implementes load()
    }

    @Override
    public Task save(Task entity) {
        throw new UnsupportedOperationException("TODO: implementar save");
    }

    @Override
    public Optional<Task> findById(String id) {
        throw new UnsupportedOperationException("TODO: implementar findById");
    }

    @Override
    public List<Task> findAll() {
        throw new UnsupportedOperationException("TODO: implementar findAll");
    }

    @Override
    public boolean deleteById(String id) {
        throw new UnsupportedOperationException("TODO: implementar deleteById");
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("TODO: implementar count");
    }

    /** TODO 6: cargar las tareas desde el archivo (si existe). */
    public void load() {
        throw new UnsupportedOperationException("TODO: implementar load");
    }

    /** TODO 7: persistir todas las tareas en el archivo. */
    public synchronized void flush() {
        throw new UnsupportedOperationException("TODO: implementar flush");
    }

    // Sugerencia: métodos privados serialize(Task) y deserialize(String).
}
