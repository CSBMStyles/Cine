package com.unicine.exception;

import com.unicine.util.validation.catalog.ErrorCode;

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
 * @see ErrorCode#VAL001 through VAL008
 */
public class ValidationException extends UnicineException {

    public ValidationException(ErrorCode errorCatalog) {
        super(errorCatalog);
    }

    public ValidationException(ErrorCode errorCatalog, Object... args) {
        super(errorCatalog, args);
    }

    public ValidationException(ErrorCode errorCatalog, Throwable cause) {
        super(errorCatalog, cause);
    }

    public ValidationException(ErrorCode errorCatalog, Throwable cause, Object... args) {
        super(errorCatalog, cause, args);
    }
}
