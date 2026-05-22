package com.unicine.exception;

import com.unicine.util.validation.catalog.ErrorCatalog;

/**
 * Excepcion lanzada cuando falla la autenticacion de un usuario.
 * 
 * Casos de uso:
 * - Credenciales invalidas
 * - Correo no registrado
 * - Contrasena incorrecta
 * - Cuenta no activada
 * - Token expirado o invalido
 * 
 * HTTP Status asociado: 401 Unauthorized
 * 
 * @author UniCine
 * @version 1.0
 * @see ErrorCatalog#AUTH001 through AUTH006
 */
public class AuthenticationException extends UnicineException {

    public AuthenticationException(ErrorCatalog errorCatalog) {
        super(errorCatalog);
    }

    public AuthenticationException(ErrorCatalog errorCatalog, Object... args) {
        super(errorCatalog, args);
    }

    public AuthenticationException(ErrorCatalog errorCatalog, Throwable cause) {
        super(errorCatalog, cause);
    }

    public AuthenticationException(ErrorCatalog errorCatalog, Throwable cause, Object... args) {
        super(errorCatalog, cause, args);
    }
}
