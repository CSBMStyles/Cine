package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Peliculas (peliculas, disposiciones, generos, colecciones).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (DOMAIN_MOVIE_ENTITY_*)
 * - Duplicados (DOMAIN_MOVIE_DUPLICATE_*)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum MovieErrorCatalog implements ErrorCode {

    // Entity - Entidad no encontrada (404 not found)
    DOMAIN_MOVIE_ENTITY_MOVIE_NOT_FOUND("DOMAIN_MOVIE_ENTITY_MOVIE_NOT_FOUND", "La pelicula no existe"),
    DOMAIN_MOVIE_ENTITY_MOVIES_NOT_FOUND_BY_NAME("DOMAIN_MOVIE_ENTITY_MOVIES_NOT_FOUND_BY_NAME", "No existe peliculas con ese nombre"),
    DOMAIN_MOVIE_ENTITY_MOVIE_DISPOSITION_NOT_FOUND("DOMAIN_MOVIE_ENTITY_MOVIE_DISPOSITION_NOT_FOUND", "La disposicion de pelicula no existe"),
    DOMAIN_MOVIE_ENTITY_GENRE_NOT_FOUND("DOMAIN_MOVIE_ENTITY_GENRE_NOT_FOUND", "El genero no existe"),
    DOMAIN_MOVIE_ENTITY_COLLECTION_NOT_FOUND("DOMAIN_MOVIE_ENTITY_COLLECTION_NOT_FOUND", "La coleccion no existe"),
    DOMAIN_MOVIE_ENTITY_COMMENT_NOT_FOUND("DOMAIN_MOVIE_ENTITY_COMMENT_NOT_FOUND", "El comentario no existe"),

    // Duplicate - Conflicto / duplicado (409 conflict)
    DOMAIN_MOVIE_DUPLICATE_MOVIE_ALREADY_EXISTS("DOMAIN_MOVIE_DUPLICATE_MOVIE_ALREADY_EXISTS", "La pelicula ya existe"),
    DOMAIN_MOVIE_DUPLICATE_MOVIE_NAME_ALREADY_EXISTS("DOMAIN_MOVIE_DUPLICATE_MOVIE_NAME_ALREADY_EXISTS", "El nombre que esta ingresando ya existe");

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
