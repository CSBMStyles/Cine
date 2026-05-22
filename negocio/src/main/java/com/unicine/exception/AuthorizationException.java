package com.unicine.exception;

import com.unicine.util.validation.catalog.ErrorCatalog;

/**
 * Excepcion lanzada cuando un usuario autenticado no tiene permisos
 * para realizar una operacion.
 * 
 * Casos de uso:
 * - Administrador de teatro intentando modificar una pelicula
 * - Cliente intentando acceder a datos de otro cliente
 * - Usuario sin rol suficiente para una accion
 * 
 * HTTP Status asociado: 403 Forbidden
 * 
 * @author UniCine
 * @version 1.0
 * @see ErrorCatalog#AUTH007
 */
public class AuthorizationException extends UnicineException {

    public AuthorizationException(ErrorCatalog errorCatalog) {
        super(errorCatalog);
    }

    public AuthorizationException(ErrorCatalog errorCatalog, Object... args) {
        super(errorCatalog, args);
    }

    public AuthorizationException(ErrorCatalog errorCatalog, Throwable cause) {
        super(errorCatalog, cause);
    }

    public AuthorizationException(ErrorCatalog errorCatalog, Throwable cause, Object... args) {
        super(errorCatalog, cause, args);
    }
}
