package com.unicine.exception;

import com.unicine.util.validation.catalog.ErrorCatalog;

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
 * @see ErrorCatalog#EXT001 through EXT009
 */
public class ExternalServiceException extends UnicineException {

    public ExternalServiceException(ErrorCatalog errorCatalog) {
        super(errorCatalog);
    }

    public ExternalServiceException(ErrorCatalog errorCatalog, Object... args) {
        super(errorCatalog, args);
    }

    public ExternalServiceException(ErrorCatalog errorCatalog, Throwable cause) {
        super(errorCatalog, cause);
    }

    public ExternalServiceException(ErrorCatalog errorCatalog, Throwable cause, Object... args) {
        super(errorCatalog, cause, args);
    }
}
