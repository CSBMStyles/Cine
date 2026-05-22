package com.unicine.exception;

import com.unicine.util.validation.catalog.ErrorCatalog;

/**
 * Excepcion lanzada cuando se viola una regla de negocio.
 * 
 * Casos de uso:
 * - Cliente menor de edad intentando registrarse
 * - Horario que se solapa con otro existente
 * - Cupon expirado o ya utilizado
 * - Descuento mayor al total de compra
 * - Compra ya procesada intentando modificarse
 * 
 * HTTP Status asociado: 400 Bad Request
 * 
 * @author UniCine
 * @version 1.0
 * @see ErrorCatalog#REG001 through REG009
 */
public class BusinessRuleException extends UnicineException {

    public BusinessRuleException(ErrorCatalog errorCatalog) {
        super(errorCatalog);
    }

    public BusinessRuleException(ErrorCatalog errorCatalog, Object... args) {
        super(errorCatalog, args);
    }

    public BusinessRuleException(ErrorCatalog errorCatalog, Throwable cause) {
        super(errorCatalog, cause);
    }

    public BusinessRuleException(ErrorCatalog errorCatalog, Throwable cause, Object... args) {
        super(errorCatalog, cause, args);
    }
}
