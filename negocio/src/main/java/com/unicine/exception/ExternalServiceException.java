package com.unicine.exception;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Excepcion lanzada cuando falla la comunicacion con un servicio externo.
 * 
 * Casos de uso:
 * - Error al subir/actualizar/eliminar imagen en ImageKit
 * - Fallo en servicio de email (SMTP)
 * - Timeout en servicio de pagos
 * - Error en API externa
 * 
 * HTTP Status asociado: 502 Bad Gateway
 * 
 * @author UniCine
 * @version 1.0
 * @see ErrorCode#EXT001 through EXT009
 */
public class ExternalServiceException extends UnicineException {

    public ExternalServiceException(ErrorCode errorCatalog) {
        super(errorCatalog);
    }

    public ExternalServiceException(ErrorCode errorCatalog, Object... args) {
        super(errorCatalog, args);
    }

    public ExternalServiceException(ErrorCode errorCatalog, Throwable cause) {
        super(errorCatalog, cause);
    }

    public ExternalServiceException(ErrorCode errorCatalog, Throwable cause, Object... args) {
        super(errorCatalog, cause, args);
    }
}
