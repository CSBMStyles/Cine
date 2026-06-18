package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Funciones (funciones, horarios, esquemas).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (DOMAIN_SHOWING_ENTITY_*)
 * - Reglas de negocio de programacion (DOMAIN_SHOWING_BUSINESS_RULE_*)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum ShowingErrorCatalog implements ErrorCode {

    // ENTITY - ENTIDAD NO ENCONTRADA (404 Not Found)
    DOMAIN_SHOWING_ENTITY_FUNCTION_NOT_FOUND("DOMAIN_SHOWING_ENTITY_FUNCTION_NOT_FOUND", "La funcion no existe"),
    DOMAIN_SHOWING_ENTITY_FUNCTION_SCHEMA_NOT_FOUND("DOMAIN_SHOWING_ENTITY_FUNCTION_SCHEMA_NOT_FOUND", "El esquema de la funcion no existe"),
    DOMAIN_SHOWING_ENTITY_SCHEDULE_NOT_FOUND("DOMAIN_SHOWING_ENTITY_SCHEDULE_NOT_FOUND", "El horario no existe"),

    // BUSINESS_RULE - REGLAS DE NEGOCIO (400 Bad Request)
    DOMAIN_SHOWING_BUSINESS_RULE_SCHEDULE_OVERLAP("DOMAIN_SHOWING_BUSINESS_RULE_SCHEDULE_OVERLAP", "El horario se solapa con uno existente"),
    DOMAIN_SHOWING_BUSINESS_RULE_FUNCTION_NO_SCHEDULES_AVAILABLE("DOMAIN_SHOWING_BUSINESS_RULE_FUNCTION_NO_SCHEDULES_AVAILABLE", "La funcion no tiene horarios disponibles");

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
