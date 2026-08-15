package com.unicine.exception.handler;

import java.time.LocalDateTime;
import java.util.List;

import com.unicine.util.validation.catalog.ErrorCode;

import lombok.Builder;
import lombok.Getter;

/**
 * Modelo estandarizado para respuestas de error en la API REST.
 * 
 * Este DTO se utiliza para devolver errores de forma consistente
 * en todos los endpoints de la aplicacion.
 * 
 * Ejemplo de respuesta:
 * {
 *   "timestamp": "2026-05-23T10:15:30",
 *   "status": 404,
 *   "error": "Not Found",
 *   "code": "DOMAIN_USER_ENTITY_ADMIN_NOT_FOUND",
 *   "message": "El administrador no existe",
 *   "path": "/api/administradores/123"
 * }
 * 
 * @author UniCine
 * @version 1.0
 */
@Getter
@Builder
public class ApiError {

    /**
     * Fecha y hora en que ocurrio el error (ISO-8601).
     */
    private LocalDateTime timestamp;

    /**
     * Codigo HTTP de la respuesta (ej: 404, 400, 401).
     */
    private int status;

    /**
     * Nombre del error HTTP (ej: "Not Found", "Bad Request").
     */
    private String error;

    /**
     * Codigo de error del catalogo UniCine (ej: "DOMAIN_USER_ENTITY_ADMIN_NOT_FOUND", "DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED").
     * Null si el error no esta catalogado.
     */
    private String code;

    /**
     * Mensaje humano-legible del error.
     */
    private String message;

    /**
     * Ruta del endpoint donde ocurrio el error.
     */
    private String path;

    /**
     * Detalles de validacion sin incluir valores rechazados o datos sensibles.
     */
    private List<ValidationErrorDetail> details;

    /**
     * Crea una instancia de ApiError con timestamp actual.
     * 
     * @param status Codigo HTTP
     * @param error Nombre del error HTTP
     * @param code Codigo del catalogo UniCine (puede ser null)
     * @param message Mensaje descriptivo
     * @param path Ruta del endpoint
     * @return ApiError construido
     */
    public static ApiError of(int status, String error, String code, String message, String path) {
        return of(status, error, code, message, path, List.of());
    }

    /**
     * Crea una instancia de ApiError con detalles estructurados de validacion.
     *
     * @param status Codigo HTTP
     * @param error Nombre del error HTTP
     * @param code Codigo del catalogo UniCine (puede ser null)
     * @param message Mensaje descriptivo
     * @param path Ruta del endpoint
     * @param details Detalles publicos de validacion
     * @return ApiError construido
     */
    public static ApiError of(
            int status,
            String error,
            String code,
            String message,
            String path,
            List<ValidationErrorDetail> details) {

        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .code(code)
                .message(message)
                .path(path)
                .details(details == null ? List.of() : List.copyOf(details))
                .build();
    }

    /**
     * Crea una instancia desde un ErrorCode.
     * 
     * @param status Codigo HTTP
     * @param error Nombre del error HTTP
     * @param errorCatalog Catalogo de error UniCine
     * @param path Ruta del endpoint
     * @return ApiError construido
     */
    public static ApiError fromCatalog(int status, String error, ErrorCode errorCatalog, String path) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .code(errorCatalog.getCode())
                .message(errorCatalog.getMessage())
                .path(path)
                .details(List.of())
                .build();
    }
}
