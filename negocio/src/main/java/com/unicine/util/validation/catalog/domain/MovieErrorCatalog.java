package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Peliculas (peliculas, disposiciones, generos, colecciones).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (ENT009-ENT011, ENT021-ENT022)
 * - Duplicados (DUP003-DUP004)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum MovieErrorCatalog implements ErrorCode {

    // ENT - ENTIDAD NO ENCONTRADA (404 Not Found)
    ENT009("ENT009", "La pelicula no existe"),
    ENT010("ENT010", "No existe peliculas con ese nombre"),
    ENT011("ENT011", "La disposicion de pelicula no existe"),
    ENT021("ENT021", "El genero no existe"),
    ENT022("ENT022", "La coleccion no existe"),

    // DUP - CONFLICTO / DUPLICADO (409 Conflict)
    DUP003("DUP003", "La pelicula ya existe"),
    DUP004("DUP004", "El nombre que esta ingresando ya existe");

    private final String code;
    private final String message;

    MovieErrorCatalog(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Obtiene el mensaje formateado con los argumentos proporcionados.
     * Soporta parametros posicionales {0}, {1}, etc.
     * 
     * @param args Argumentos para reemplazar en el mensaje
     * @return Mensaje formateado
     */
    public String format(Object... args) {
        String formatted = message;
        for (int i = 0; i < args.length; i++) {
            formatted = formatted.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return formatted;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", code, message);
    }
}
