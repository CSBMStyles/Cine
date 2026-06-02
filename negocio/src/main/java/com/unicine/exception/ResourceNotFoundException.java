package com.unicine.exception;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Excepcion lanzada cuando un recurso solicitado no existe en la base de datos.
 * 
 * Ejemplos de uso:
 * - Pelicula no encontrada por ID
 * - Cliente inexistente
 * - Teatro no registrado
 * 
 * HTTP Status asociado: 404 Not Found
 * 
 * @author UniCine
 * @version 1.0
 * @see ErrorCode#ENT001 through ENT022
 */
public class ResourceNotFoundException extends UnicineException {

    public ResourceNotFoundException(ErrorCode errorCatalog) {
        super(errorCatalog);
    }

    public ResourceNotFoundException(ErrorCode errorCatalog, Object... args) {
        super(errorCatalog, args);
    }

    public ResourceNotFoundException(ErrorCode errorCatalog, Throwable cause) {
        super(errorCatalog, cause);
    }

    public ResourceNotFoundException(ErrorCode errorCatalog, Throwable cause, Object... args) {
        super(errorCatalog, cause, args);
    }
}
