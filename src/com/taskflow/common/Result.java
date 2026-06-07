package com.taskflow.common;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * EJEMPLO DE REFERENCIA (ya resuelto) — tipo Result al estilo "Either".
 *
 * Te dejamos ESTE archivo implementado a propósito, como modelo de cómo usar
 * <b>sealed interfaces</b>, <b>records</b> y <b>pattern matching</b> de switch.
 * Estúdialo y reutilízalo en TaskService para reportar errores de negocio sin
 * lanzar excepciones.
 *
 * @param <T> tipo del valor en caso de éxito
 */
public sealed interface Result<T> permits Result.Ok, Result.Err {

    record Ok<T>(T value) implements Result<T> {}

    record Err<T>(String message) implements Result<T> {}

    static <T> Result<T> ok(T value) {
        return new Ok<>(value);
    }

    static <T> Result<T> err(String message) {
        return new Err<>(message);
    }

    default boolean isOk() {
        return this instanceof Ok<T>;
    }

    default <R> Result<R> map(Function<? super T, ? extends R> mapper) {
        return switch (this) {
            case Ok<T> ok -> Result.ok(mapper.apply(ok.value()));
            case Err<T> err -> Result.err(err.message());
        };
    }

    default void match(Consumer<? super T> onOk, Consumer<String> onErr) {
        switch (this) {
            case Ok<T> ok -> onOk.accept(ok.value());
            case Err<T> err -> onErr.accept(err.message());
        }
    }

    default T orElse(T other) {
        return switch (this) {
            case Ok<T> ok -> ok.value();
            case Err<T> ignored -> other;
        };
    }
}
