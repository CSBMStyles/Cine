package com.unicine.exception;

import com.unicine.util.validation.catalog.ErrorCatalog;

/**
 * Excepcion lanzada cuando falla la validacion de datos de entrada.
 * 
 * Casos de uso:
 * - Parametros vacios o nulos donde no se permiten
 * - Formato invalido (email, telefono, etc.)
 * - Violaciones de constraints de Bean Validation
 * 
 * HTTP Status asociado: 400 Bad Request
 * 
 * @author UniCine
 * @version 1.0
 * @see ErrorCatalog#VAL001 through VAL008
 */
public class ValidationException extends UnicineException {

    public ValidationException(ErrorCatalog errorCatalog) {
        super(errorCatalog);
    }

    public ValidationException(ErrorCatalog errorCatalog, Object... args) {
        super(errorCatalog, args);
    }

    public ValidationException(ErrorCatalog errorCatalog, Throwable cause) {
        super(errorCatalog, cause);
    }

    public ValidationException(ErrorCatalog errorCatalog, Throwable cause, Object... args) {
        super(errorCatalog, cause, args);
    }
}
