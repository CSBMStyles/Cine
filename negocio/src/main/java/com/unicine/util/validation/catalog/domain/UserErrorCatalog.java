package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Usuarios (clientes, administradores, autenticacion).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (DOMAIN_USER_ENTITY_*)
 * - Duplicados (DOMAIN_USER_DUPLICATE_*)
 * - Autenticacion (DOMAIN_USER_AUTH_*)
 * - Reglas de negocio de usuario (DOMAIN_USER_BUSINESS_RULE_*)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum UserErrorCatalog implements ErrorCode {

    // Entity - Entidad no encontrada (404 not found)
    DOMAIN_USER_ENTITY_ADMIN_NOT_FOUND("DOMAIN_USER_ENTITY_ADMIN_NOT_FOUND", "El administrador no existe"),
    DOMAIN_USER_ENTITY_THEATER_ADMIN_NOT_FOUND("DOMAIN_USER_ENTITY_THEATER_ADMIN_NOT_FOUND", "El administrador de teatro no existe"),
    DOMAIN_USER_ENTITY_CLIENT_NOT_FOUND("DOMAIN_USER_ENTITY_CLIENT_NOT_FOUND", "El cliente no existe"),

    // Duplicate - Conflicto / duplicado (409 conflict)
    DOMAIN_USER_DUPLICATE_ID_ALREADY_REGISTERED("DOMAIN_USER_DUPLICATE_ID_ALREADY_REGISTERED", "La cedula ya esta registrada"),
    DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED("DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED", "Este correo ya esta registrado"),

    // Auth - Autenticacion (401 unauthorized)
    DOMAIN_USER_AUTH_INVALID_CREDENTIALS("DOMAIN_USER_AUTH_INVALID_CREDENTIALS", "Credenciales invalidas"),
    DOMAIN_USER_AUTH_EMAIL_NOT_FOUND("DOMAIN_USER_AUTH_EMAIL_NOT_FOUND", "El correo no existe"),
    DOMAIN_USER_AUTH_AUTH_DATA_INCORRECT("DOMAIN_USER_AUTH_AUTH_DATA_INCORRECT", "Los datos de autenticacion son incorrectos"),
    DOMAIN_USER_AUTH_CURRENT_PASSWORD_INCORRECT("DOMAIN_USER_AUTH_CURRENT_PASSWORD_INCORRECT", "La contrasena actual es incorrecta"),
    DOMAIN_USER_AUTH_NEW_PASSWORD_SAME_AS_CURRENT("DOMAIN_USER_AUTH_NEW_PASSWORD_SAME_AS_CURRENT", "La nueva contrasena no puede ser igual a la actual"),
    DOMAIN_USER_AUTH_CLIENT_INACTIVE("DOMAIN_USER_AUTH_CLIENT_INACTIVE", "El cliente no esta activo, debe activarla con el enlace que fue enviado a su correo"),
    DOMAIN_USER_AUTH_ACTION_NOT_PERMITTED("DOMAIN_USER_AUTH_ACTION_NOT_PERMITTED", "El usuario no tiene permisos para realizar esta accion"),

    // Business rule - Reglas de negocio (400 bad request)
    DOMAIN_USER_BUSINESS_RULE_CLIENT_UNDERAGE("DOMAIN_USER_BUSINESS_RULE_CLIENT_UNDERAGE", "El cliente debe ser mayor de edad para registrarse");

    private final String code;
    private final String message;

    UserErrorCatalog(String code, String message) {
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
