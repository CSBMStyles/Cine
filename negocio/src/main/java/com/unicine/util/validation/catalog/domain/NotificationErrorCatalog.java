package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Notificaciones (envio de correos, SMS, push).
 *
 * Contiene codigos para:
 * - Errores de servicios externos de notificacion (DOMAIN_NOTIFICATION_EXTERNAL_*)
 *
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 *
 * @author UniCine
 * @version 1.0
 */
public enum NotificationErrorCatalog implements ErrorCode {

    // External - Servicios externos de notificacion (502 bad gateway)
    DOMAIN_NOTIFICATION_EXTERNAL_SEND_ERROR("DOMAIN_NOTIFICATION_EXTERNAL_SEND_ERROR", "Error al enviar la notificacion: {0}"),
    DOMAIN_NOTIFICATION_EXTERNAL_INVALID_RECIPIENT("DOMAIN_NOTIFICATION_EXTERNAL_INVALID_RECIPIENT", "El destinatario '{0}' no es valido"),
    DOMAIN_NOTIFICATION_EXTERNAL_AUTH_FAILED("DOMAIN_NOTIFICATION_EXTERNAL_AUTH_FAILED", "Fallaron las credenciales SMTP: {0}"),
    DOMAIN_NOTIFICATION_EXTERNAL_TEMPLATE_ERROR("DOMAIN_NOTIFICATION_EXTERNAL_TEMPLATE_ERROR", "Error en la plantilla del mensaje: {0}");

    private final String code;
    private final String message;

    NotificationErrorCatalog(String code, String message) {
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
