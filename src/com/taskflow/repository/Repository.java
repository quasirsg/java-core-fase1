package com.taskflow.repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio genérico (patrón Repository).
 * Demuestra el uso de <b>generics</b> y <b>Optional</b> en una API limpia.
 *
 * @param <T>  tipo de la entidad
 * @param <ID> tipo del identificador
 */
public interface Repository<T, ID> {

    /** Guarda (crea o actualiza) la entidad y la devuelve. */
    T save(T entity);

    /** Busca por id. Nunca devuelve null: usa Optional. */
    Optional<T> findById(ID id);

    /** Devuelve todas las entidades (copia defensiva). */
    List<T> findAll();

    /** Elimina por id. Devuelve true si existía y fue eliminada. */
    boolean deleteById(ID id);

    /** Cantidad de entidades almacenadas. */
    long count();
}
