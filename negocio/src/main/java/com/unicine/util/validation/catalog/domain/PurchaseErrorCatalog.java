package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Compras (compras, entradas, cupones, confiteria).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (DOMAIN_PURCHASE_ENTITY_*)
 * - Reglas de negocio de compra (DOMAIN_PURCHASE_BUSINESS_RULE_*)
 * - Eliminacion no confirmada (DOMAIN_PURCHASE_DELETE_*)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum PurchaseErrorCatalog implements ErrorCode {

    // ENTITY - ENTIDAD NO ENCONTRADA (404 Not Found)
    DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND("DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND", "El cupon no existe"),
    DOMAIN_PURCHASE_ENTITY_TICKET_NOT_FOUND("DOMAIN_PURCHASE_ENTITY_TICKET_NOT_FOUND", "La entrada no existe"),
    DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND("DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND", "La compra no existe"),
    DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND("DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND", "La confiteria no existe"),
    DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRESENTATION_NOT_FOUND("DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRESENTATION_NOT_FOUND", "La presentacion de confiteria no existe"),
    DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRICE_HISTORY_NOT_FOUND("DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRICE_HISTORY_NOT_FOUND", "El historial de precios no existe"),

    // BUSINESS_RULE - REGLAS DE NEGOCIO (400 Bad Request)
    DOMAIN_PURCHASE_BUSINESS_RULE_ROOM_NOT_ENOUGH_AVAILABLE_SEATS("DOMAIN_PURCHASE_BUSINESS_RULE_ROOM_NOT_ENOUGH_AVAILABLE_SEATS", "La sala no tiene suficientes sillas disponibles"),
    DOMAIN_PURCHASE_BUSINESS_RULE_COUPON_ALREADY_USED("DOMAIN_PURCHASE_BUSINESS_RULE_COUPON_ALREADY_USED", "El cupon ya fue utilizado"),
    DOMAIN_PURCHASE_BUSINESS_RULE_COUPON_EXPIRED("DOMAIN_PURCHASE_BUSINESS_RULE_COUPON_EXPIRED", "El cupon ha expirado"),
    DOMAIN_PURCHASE_BUSINESS_RULE_DISCOUNT_GREATER_THAN_TOTAL("DOMAIN_PURCHASE_BUSINESS_RULE_DISCOUNT_GREATER_THAN_TOTAL", "El descuento no puede ser mayor al total de la compra"),
    DOMAIN_PURCHASE_BUSINESS_RULE_PURCHASE_ALREADY_PROCESSED("DOMAIN_PURCHASE_BUSINESS_RULE_PURCHASE_ALREADY_PROCESSED", "La compra no puede modificarse porque ya fue procesada"),
    DOMAIN_PURCHASE_BUSINESS_RULE_SELECTED_SEAT_ALREADY_OCCUPIED("DOMAIN_PURCHASE_BUSINESS_RULE_SELECTED_SEAT_ALREADY_OCCUPIED", "La silla seleccionada ya esta ocupada"),
    DOMAIN_PURCHASE_BUSINESS_RULE_TICKET_FUNCTION_MISMATCH("DOMAIN_PURCHASE_BUSINESS_RULE_TICKET_FUNCTION_MISMATCH", "La funcion de la entrada no coincide con la funcion de la compra"),
    DOMAIN_PURCHASE_BUSINESS_RULE_COMMENT_NOT_ALLOWED_WITHOUT_ATTENDING("DOMAIN_PURCHASE_BUSINESS_RULE_COMMENT_NOT_ALLOWED_WITHOUT_ATTENDING", "El cliente no puede comentar sin haber asistido a una funcion de la pelicula"),

    // DELETE - ELIMINACION NO CONFIRMADA (409 Conflict)
    DOMAIN_PURCHASE_DELETE_DELETE_NOT_CONFIRMED("DOMAIN_PURCHASE_DELETE_DELETE_NOT_CONFIRMED", "La eliminacion no fue confirmada"),

    // BUSINESS_RULE - HISTORIAL DE PRECIOS
    DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRICE_HISTORY_BASE_PRICE_REQUIRED("DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRICE_HISTORY_BASE_PRICE_REQUIRED", "El precio base es requerido para registrar el cambio de precio");

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
