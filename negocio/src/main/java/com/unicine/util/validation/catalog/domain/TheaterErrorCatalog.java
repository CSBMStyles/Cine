package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Teatros (teatros, salas, ciudades, distribucion de sillas).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (DOMAIN_THEATER_ENTITY_*)
 * - Duplicados (DOMAIN_THEATER_DUPLICATE_*)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum TheaterErrorCatalog implements ErrorCode {

    // Entity - Entidad no encontrada (404 not found)
    DOMAIN_THEATER_ENTITY_CITY_NOT_FOUND("DOMAIN_THEATER_ENTITY_CITY_NOT_FOUND", "La ciudad no existe"),
    DOMAIN_THEATER_ENTITY_CITY_NOT_FOUND_BY_NAME("DOMAIN_THEATER_ENTITY_CITY_NOT_FOUND_BY_NAME", "No existe ciudad con ese nombre"),
    DOMAIN_THEATER_ENTITY_THEATER_NOT_FOUND("DOMAIN_THEATER_ENTITY_THEATER_NOT_FOUND", "El teatro no existe"),
    DOMAIN_THEATER_ENTITY_ROOM_NOT_FOUND("DOMAIN_THEATER_ENTITY_ROOM_NOT_FOUND", "La sala no existe"),
    DOMAIN_THEATER_ENTITY_ROOMS_NOT_FOUND_BY_NAME("DOMAIN_THEATER_ENTITY_ROOMS_NOT_FOUND_BY_NAME", "No existe salas con ese nombre"),
    DOMAIN_THEATER_ENTITY_SEAT_DISTRIBUTION_NOT_FOUND("DOMAIN_THEATER_ENTITY_SEAT_DISTRIBUTION_NOT_FOUND", "La distribucion de sillas no existe"),
    DOMAIN_THEATER_ENTITY_SEAT_SCHEMA_NOT_FOUND("DOMAIN_THEATER_ENTITY_SEAT_SCHEMA_NOT_FOUND", "El esquema de sillas no existe"),
    DOMAIN_THEATER_ENTITY_SEAT_SCHEMA_INVALID("DOMAIN_THEATER_ENTITY_SEAT_SCHEMA_INVALID", "El esquema de sillas es invalido"),

    // Business rule - Reglas de negocio (400 bad request)
    DOMAIN_THEATER_BUSINESS_RULE_SEAT_NOT_FOUND_IN_ROOM_DISTRIBUTION("DOMAIN_THEATER_BUSINESS_RULE_SEAT_NOT_FOUND_IN_ROOM_DISTRIBUTION", "La silla no existe en la distribucion de la sala"),
    DOMAIN_THEATER_BUSINESS_RULE_SEAT_NOT_AVAILABLE_FOR_FUNCTION("DOMAIN_THEATER_BUSINESS_RULE_SEAT_NOT_AVAILABLE_FOR_FUNCTION", "La silla no esta disponible para esta funcion"),

    // Duplicate - Conflicto / duplicado (409 conflict)
    DOMAIN_THEATER_DUPLICATE_ROOM_NAME_ALREADY_EXISTS_IN_THEATER("DOMAIN_THEATER_DUPLICATE_ROOM_NAME_ALREADY_EXISTS_IN_THEATER", "El nombre de la sala ya existe en el teatro"),
    DOMAIN_THEATER_DUPLICATE_THEATER_ADDRESS_ALREADY_EXISTS_IN_CITY("DOMAIN_THEATER_DUPLICATE_THEATER_ADDRESS_ALREADY_EXISTS_IN_CITY", "La direccion del teatro ya existe en la ciudad");

    private final String code;
    private final String message;

    TheaterErrorCatalog(String code, String message) {
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
