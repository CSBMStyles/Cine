package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del sistema (validacion de parametros, errores generales).
 * 
 * Contiene codigos para:
 * - Validacion de parametros (VAL001-VAL008)
 * - Errores internos del servidor (GEN001-GEN004)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum SystemErrorCatalog implements ErrorCode {

    // VAL - VALIDACION DE PARAMETROS (400 Bad Request)
    VAL001("VAL001", "El parametro no puede estar vacio"),
    VAL002("VAL002", "El parametro debe ser un numero positivo"),
    VAL003("VAL003", "El parametro no puede estar en blanco"),
    VAL004("VAL004", "El codigo no puede estar vacio"),
    VAL005("VAL005", "El codigo debe ser un numero positivo"),
    VAL006("VAL006", "El nombre no puede estar en blanco"),
    VAL007("VAL007", "El codigo de teatro no puede estar vacio"),
    VAL008("VAL008", "El codigo de teatro debe ser un numero positivo"),

    // GEN - GENERALES / INTERNOS (500 Internal Server Error)
    GEN001("GEN001", "Error inesperado del servidor: {0}"),
    GEN002("GEN002", "Operacion no soportada"),
    GEN003("GEN003", "Error al procesar la solicitud"),
    GEN004("GEN004", "Servicio no disponible temporalmente");

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
