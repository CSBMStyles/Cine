package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Imagenes (gestion de imagenes, servicios externos).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (ENT020)
 * - Duplicados (DUP007)
 * - Reglas de negocio de imagen (REG002)
 * - Errores de servicios externos (EXT001-EXT009)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum ImageErrorCatalog implements ErrorCode {

    // ENT - ENTIDAD NO ENCONTRADA (404 Not Found)
    ENT020("ENT020", "La imagen no existe"),

    // DUP - CONFLICTO / DUPLICADO (409 Conflict)
    DUP007("DUP007", "La persona ya tiene una imagen, deberia utilizar el metodo actualizar"),

    // REG - REGLAS DE NEGOCIO (400 Bad Request)
    REG002("REG002", "El tamano de la imagen excede el limite permitido de {0} MB. Tamano actual: {1} MB"),

    // EXT - SISTEMA EXTERNO (502 Bad Gateway)
    EXT001("EXT001", "Error al subir la imagen: {0}"),
    EXT002("EXT002", "Error al actualizar la imagen: {0}"),
    EXT003("EXT003", "Error al restaurar la version de la imagen: {0}"),
    EXT004("EXT004", "Error al renombrar el archivo: {0}"),
    EXT005("EXT005", "Error al eliminar la imagen: {0}"),
    EXT006("EXT006", "Error al eliminar las imagenes: {0}"),
    EXT007("EXT007", "Error al obtener los datos de la imagen: {0}"),
    EXT008("EXT008", "Error al listar las imagenes: {0}"),
    EXT009("EXT009", "Error al listar las versiones de la imagen: {0}");

    private final String code;
    private final String message;

    ImageErrorCatalog(String code, String message) {
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
