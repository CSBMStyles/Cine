package com.unicine.exception;

import com.unicine.util.validation.catalog.ErrorCatalog;

/**
 * Excepcion base abstracta para el dominio UniCine.
 * 
 * Todas las excepciones de negocio deben extender de esta clase.
 * Proporciona:
 * - Codigo de error del catalogo centralizado
 * - Mensaje formateado con parametros
 * - Stack trace opcional (configurable)
 * 
 * @author UniCine
 * @version 1.0
 */
public abstract class UnicineException extends RuntimeException {

    private final ErrorCatalog errorCatalog;
    private final String formattedMessage;
    private final String errorCode;

    /**
     * Constructor con catalogo de error sin parametros adicionales.
     * 
     * @param errorCatalog Catalogo de error con codigo y mensaje base
     */
    protected UnicineException(ErrorCatalog errorCatalog) {
        super(errorCatalog.getMessage());
        this.errorCatalog = errorCatalog;
        this.formattedMessage = errorCatalog.getMessage();
        this.errorCode = errorCatalog.getCode();
    }

    /**
     * Constructor con catalogo de error y parametros para formateo.
     * 
     * @param errorCatalog Catalogo de error con codigo y mensaje base
     * @param args Argumentos para reemplazar en el mensaje ({0}, {1}, etc.)
     */
    protected UnicineException(ErrorCatalog errorCatalog, Object... args) {
        super(errorCatalog.format(args));
        this.errorCatalog = errorCatalog;
        this.formattedMessage = errorCatalog.format(args);
        this.errorCode = errorCatalog.getCode();
    }

    /**
     * Constructor con catalogo de error y causa original.
     * 
     * @param errorCatalog Catalogo de error con codigo y mensaje base
     * @param cause Excepcion original que causó el error
     */
    protected UnicineException(ErrorCatalog errorCatalog, Throwable cause) {
        super(errorCatalog.getMessage(), cause);
        this.errorCatalog = errorCatalog;
        this.formattedMessage = errorCatalog.getMessage();
        this.errorCode = errorCatalog.getCode();
    }

    /**
     * Constructor con catalogo de error, parametros de formateo y causa original.
     * 
     * @param errorCatalog Catalogo de error con codigo y mensaje base
     * @param cause Excepcion original que causó el error
     * @param args Argumentos para reemplazar en el mensaje ({0}, {1}, etc.)
     */
    protected UnicineException(ErrorCatalog errorCatalog, Throwable cause, Object... args) {
        super(errorCatalog.format(args), cause);
        this.errorCatalog = errorCatalog;
        this.formattedMessage = errorCatalog.format(args);
        this.errorCode = errorCatalog.getCode();
    }

    public ErrorCatalog getErrorCatalog() {
        return errorCatalog;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return String.format("%s [code=%s, message=%s]", 
            getClass().getSimpleName(), 
            errorCode, 
            formattedMessage);
    }
}
