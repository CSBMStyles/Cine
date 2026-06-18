package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del sistema (validacion de parametros, errores generales).
 * 
 * Contiene codigos para:
 * - Validacion de parametros (DOMAIN_SYSTEM_VALIDATION_*)
 * - Errores internos del servidor (DOMAIN_SYSTEM_GENERAL_*)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum SystemErrorCatalog implements ErrorCode {

    // VALIDATION - VALIDACION DE PARAMETROS (400 Bad Request)
    DOMAIN_SYSTEM_VALIDATION_PARAMETER_CANNOT_BE_EMPTY("DOMAIN_SYSTEM_VALIDATION_PARAMETER_CANNOT_BE_EMPTY", "El parametro no puede estar vacio"),
    DOMAIN_SYSTEM_VALIDATION_PARAMETER_MUST_BE_POSITIVE_NUMBER("DOMAIN_SYSTEM_VALIDATION_PARAMETER_MUST_BE_POSITIVE_NUMBER", "El parametro debe ser un numero positivo"),
    DOMAIN_SYSTEM_VALIDATION_PARAMETER_CANNOT_BE_BLANK("DOMAIN_SYSTEM_VALIDATION_PARAMETER_CANNOT_BE_BLANK", "El parametro no puede estar en blanco"),
    DOMAIN_SYSTEM_VALIDATION_CODE_CANNOT_BE_EMPTY("DOMAIN_SYSTEM_VALIDATION_CODE_CANNOT_BE_EMPTY", "El codigo no puede estar vacio"),
    DOMAIN_SYSTEM_VALIDATION_CODE_MUST_BE_POSITIVE_NUMBER("DOMAIN_SYSTEM_VALIDATION_CODE_MUST_BE_POSITIVE_NUMBER", "El codigo debe ser un numero positivo"),
    DOMAIN_SYSTEM_VALIDATION_NAME_CANNOT_BE_BLANK("DOMAIN_SYSTEM_VALIDATION_NAME_CANNOT_BE_BLANK", "El nombre no puede estar en blanco"),
    DOMAIN_SYSTEM_VALIDATION_THEATER_CODE_CANNOT_BE_EMPTY("DOMAIN_SYSTEM_VALIDATION_THEATER_CODE_CANNOT_BE_EMPTY", "El codigo de teatro no puede estar vacio"),
    DOMAIN_SYSTEM_VALIDATION_THEATER_CODE_MUST_BE_POSITIVE_NUMBER("DOMAIN_SYSTEM_VALIDATION_THEATER_CODE_MUST_BE_POSITIVE_NUMBER", "El codigo de teatro debe ser un numero positivo"),

    // GENERAL - GENERALES / INTERNOS (500 Internal Server Error)
    DOMAIN_SYSTEM_GENERAL_UNEXPECTED_SERVER_ERROR("DOMAIN_SYSTEM_GENERAL_UNEXPECTED_SERVER_ERROR", "Error inesperado del servidor: {0}"),
    DOMAIN_SYSTEM_GENERAL_OPERATION_NOT_SUPPORTED("DOMAIN_SYSTEM_GENERAL_OPERATION_NOT_SUPPORTED", "Operacion no soportada"),
    DOMAIN_SYSTEM_GENERAL_REQUEST_PROCESSING_ERROR("DOMAIN_SYSTEM_GENERAL_REQUEST_PROCESSING_ERROR", "Error al procesar la solicitud"),
    DOMAIN_SYSTEM_GENERAL_SERVICE_TEMPORARILY_UNAVAILABLE("DOMAIN_SYSTEM_GENERAL_SERVICE_TEMPORARILY_UNAVAILABLE", "Servicio no disponible temporalmente");

    private final String code;
    private final String message;

    SystemErrorCatalog(String code, String message) {
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
