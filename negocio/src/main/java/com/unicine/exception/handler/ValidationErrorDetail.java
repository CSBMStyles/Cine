package com.unicine.exception.handler;

/**
 * Detalle publico de un error de validacion.
 *
 * No incluye el valor rechazado para evitar exponer credenciales u otros
 * datos enviados por el cliente.
 */
public record ValidationErrorDetail(String field, String message) {
}
