package com.unicine.util.validation.catalog.domain;

import com.unicine.util.validation.catalog.ErrorCode;

/**
 * Catalogo de errores del dominio de Imagenes (gestion de imagenes, servicios externos).
 * 
 * Contiene codigos para:
 * - Entidades no encontradas (DOMAIN_IMAGE_ENTITY_*)
 * - Duplicados (DOMAIN_IMAGE_DUPLICATE_*)
 * - Reglas de negocio de imagen (DOMAIN_IMAGE_BUSINESS_RULE_*)
 * - Errores de servicios externos (DOMAIN_IMAGE_EXTERNAL_*)
 * 
 * NOTA: El HTTP status se define en el {@code @ControllerAdvice},
 * no en este enum, para mantener la capa de negocio desacoplada de HTTP.
 * 
 * @author UniCine
 * @version 1.0
 */
public enum ImageErrorCatalog implements ErrorCode {

    // Entity - Entidad no encontrada (404 not found)
    DOMAIN_IMAGE_ENTITY_IMAGE_NOT_FOUND("DOMAIN_IMAGE_ENTITY_IMAGE_NOT_FOUND", "La imagen no existe"),

    // Duplicate - Conflicto / duplicado (409 conflict)
    DOMAIN_IMAGE_DUPLICATE_PERSON_ALREADY_HAS_IMAGE("DOMAIN_IMAGE_DUPLICATE_PERSON_ALREADY_HAS_IMAGE", "La persona ya tiene una imagen, deberia utilizar el metodo actualizar"),

    // Business rule - Reglas de negocio (400 bad request)
    DOMAIN_IMAGE_BUSINESS_RULE_IMAGE_SIZE_EXCEEDS_LIMIT("DOMAIN_IMAGE_BUSINESS_RULE_IMAGE_SIZE_EXCEEDS_LIMIT", "El tamano de la imagen excede el limite permitido de {0} MB. Tamano actual: {1} MB"),

    // External - Sistema externo (502 bad gateway)
    DOMAIN_IMAGE_EXTERNAL_UPLOAD_ERROR("DOMAIN_IMAGE_EXTERNAL_UPLOAD_ERROR", "Error al subir la imagen: {0}"),
    DOMAIN_IMAGE_EXTERNAL_UPDATE_ERROR("DOMAIN_IMAGE_EXTERNAL_UPDATE_ERROR", "Error al actualizar la imagen: {0}"),
    DOMAIN_IMAGE_EXTERNAL_RESTORE_VERSION_ERROR("DOMAIN_IMAGE_EXTERNAL_RESTORE_VERSION_ERROR", "Error al restaurar la version de la imagen: {0}"),
    DOMAIN_IMAGE_EXTERNAL_RENAME_FILE_ERROR("DOMAIN_IMAGE_EXTERNAL_RENAME_FILE_ERROR", "Error al renombrar el archivo: {0}"),
    DOMAIN_IMAGE_EXTERNAL_DELETE_IMAGE_ERROR("DOMAIN_IMAGE_EXTERNAL_DELETE_IMAGE_ERROR", "Error al eliminar la imagen: {0}"),
    DOMAIN_IMAGE_EXTERNAL_DELETE_IMAGES_ERROR("DOMAIN_IMAGE_EXTERNAL_DELETE_IMAGES_ERROR", "Error al eliminar las imagenes: {0}"),
    DOMAIN_IMAGE_EXTERNAL_GET_IMAGE_DATA_ERROR("DOMAIN_IMAGE_EXTERNAL_GET_IMAGE_DATA_ERROR", "Error al obtener los datos de la imagen: {0}"),
    DOMAIN_IMAGE_EXTERNAL_LIST_IMAGES_ERROR("DOMAIN_IMAGE_EXTERNAL_LIST_IMAGES_ERROR", "Error al listar las imagenes: {0}"),
    DOMAIN_IMAGE_EXTERNAL_LIST_IMAGE_VERSIONS_ERROR("DOMAIN_IMAGE_EXTERNAL_LIST_IMAGE_VERSIONS_ERROR", "Error al listar las versiones de la imagen: {0}");

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
