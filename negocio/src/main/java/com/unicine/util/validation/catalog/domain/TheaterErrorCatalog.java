package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Teatros (teatros, salas, ciudades, distribucion de sillas).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (ENT004-ENT008, ENT012)
 * - Duplicados (DUP005-DUP006)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum TheaterErrorCatalog implements ErrorCode {

    // ENT - ENTIDAD NO ENCONTRADA (404 Not Found)
    ENT004("ENT004", "La ciudad no existe"),
    ENT005("ENT005", "No existe ciudad con ese nombre"),
    ENT006("ENT006", "El teatro no existe"),
    ENT007("ENT007", "La sala no existe"),
    ENT008("ENT008", "No existe salas con ese nombre"),
    ENT012("ENT012", "La distribucion de sillas no existe"),
    ENT017("ENT017", "El esquema de sillas no existe"),
    ENT018("ENT018", "El esquema de sillas es invalido"),

    // REG - REGLAS DE NEGOCIO (400 Bad Request)
    REG007("REG007", "La silla no existe en la distribucion de la sala"),

    // DUP - CONFLICTO / DUPLICADO (409 Conflict)
    DUP005("DUP005", "El nombre de la sala ya existe en el teatro"),
    DUP006("DUP006", "La direccion del teatro ya existe en la ciudad");

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
