package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Compras (compras, entradas, cupones, confiteria).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (ENT016-ENT019)
 * - Reglas de negocio de compra (REG005-REG009)
 * - Eliminacion no confirmada (DEL001)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum PurchaseErrorCatalog implements ErrorCode {

    // ENT - ENTIDAD NO ENCONTRADA (404 Not Found)
    ENT016("ENT016", "El cupon no existe"),
    ENT017("ENT017", "La entrada no existe"),
    ENT018("ENT018", "La compra no existe"),
    ENT019("ENT019", "La confiteria no existe"),

    // REG - REGLAS DE NEGOCIO (400 Bad Request)
    REG005("REG005", "La sala no tiene suficientes sillas disponibles"),
    REG006("REG006", "El cupon ya fue utilizado"),
    REG007("REG007", "El cupon ha expirado"),
    REG008("REG008", "El descuento no puede ser mayor al total de la compra"),
    REG009("REG009", "La compra no puede modificarse porque ya fue procesada"),
    REG010("REG010", "La silla seleccionada ya esta ocupada"),
    REG012("REG012", "La funcion de la entrada no coincide con la funcion de la compra"),

    // DEL - ELIMINACION NO CONFIRMADA (409 Conflict)
    DEL001("DEL001", "La eliminacion no fue confirmada");

    private final String code;
    private final String message;

    PurchaseErrorCatalog(String code, String message) {
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
