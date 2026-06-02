package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Usuarios (clientes, administradores, autenticacion).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (ENT001-ENT003)
 * - Duplicados (DUP001-DUP002)
 * - Autenticacion (AUTH001-AUTH007)
 * - Reglas de negocio de usuario (REG001)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum UserErrorCatalog implements ErrorCode {

    // ENT - ENTIDAD NO ENCONTRADA (404 Not Found)
    ENT001("ENT001", "El administrador no existe"),
    ENT002("ENT002", "El administrador de teatro no existe"),
    ENT003("ENT003", "El cliente no existe"),

    // DUP - CONFLICTO / DUPLICADO (409 Conflict)
    DUP001("DUP001", "La cedula ya esta registrada"),
    DUP002("DUP002", "Este correo ya esta registrado"),

    // AUTH - AUTENTICACION (401 Unauthorized)
    AUTH001("AUTH001", "Credenciales invalidas"),
    AUTH002("AUTH002", "El correo no existe"),
    AUTH003("AUTH003", "Los datos de autenticacion son incorrectos"),
    AUTH004("AUTH004", "La contrasena actual es incorrecta"),
    AUTH005("AUTH005", "La nueva contrasena no puede ser igual a la actual"),
    AUTH006("AUTH006", "El cliente no esta activo, debe activarla con el enlace que fue enviado a su correo"),
    AUTH007("AUTH007", "El usuario no tiene permisos para realizar esta accion"),

    // REG - REGLAS DE NEGOCIO (400 Bad Request)
    REG001("REG001", "El cliente debe ser mayor de edad para registrarse");

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
