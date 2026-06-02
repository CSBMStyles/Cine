package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Funciones (funciones, horarios, esquemas).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (ENT013-ENT015)
 * - Reglas de negocio de programacion (REG003-REG004)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum ShowingErrorCatalog implements ErrorCode {

    // ENT - ENTIDAD NO ENCONTRADA (404 Not Found)
    ENT013("ENT013", "La funcion no existe"),
    ENT014("ENT014", "El esquema de la funcion no existe"),
    ENT015("ENT015", "El horario no existe"),

    // REG - REGLAS DE NEGOCIO (400 Bad Request)
    REG003("REG003", "El horario se solapa con uno existente"),
    REG004("REG004", "La funcion no tiene horarios disponibles");

    private final String code;
    private final String message;

    ShowingErrorCatalog(String code, String message) {
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
