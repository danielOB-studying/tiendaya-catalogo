package com.tiendaya.catalogo.exception;

/**
 * Se lanza cuando se solicita un recurso (por ejemplo, un producto) que
 * no existe en la base de datos. El GlobalExceptionHandler la traduce
 * a una respuesta HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
