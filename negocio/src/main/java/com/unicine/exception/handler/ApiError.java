package com.unicine.exception.handler;

import java.time.LocalDateTime;
import java.util.List;

import com.unicine.util.validation.catalog.ErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "ApiError", description = "Error uniforme de la API: status HTTP + codigo de catalogo UniCine")
public class ApiError {

    /**
     * Fecha y hora en que ocurrio el error (ISO-8601).
     */
    @Schema(description = "Fecha y hora del error (ISO-8601)", example = "2026-05-23T10:15:30")
    private LocalDateTime timestamp;

    /**
     * Codigo HTTP de la respuesta (ej: 404, 400, 401).
     */
    @Schema(description = "Codigo HTTP", example = "404")
    private int status;

    /**
     * Nombre del error HTTP (ej: "Not Found", "Bad Request").
     */
    @Schema(description = "Nombre del error HTTP", example = "Not Found")
    private String error;

    /**
     * Codigo de error del catalogo UniCine (ej: "DOMAIN_USER_ENTITY_ADMIN_NOT_FOUND", "DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED").
     * Null si el error no esta catalogado.
     */
    @Schema(description = "Codigo de catalogo UniCine, null si no catalogado",
            example = "DOMAIN_USER_ENTITY_ADMIN_NOT_FOUND", nullable = true)
    private String code;

    /**
     * Mensaje humano-legible del error.
     */
    @Schema(description = "Mensaje humano-legible", example = "El administrador no existe")
    private String message;

    /**
     * Ruta del endpoint donde ocurrio el error.
     */
    @Schema(description = "Ruta del endpoint", example = "/api/administradores/123")
    private String path;

    /**
     * Detalles de validacion sin incluir valores rechazados o datos sensibles.
     */
    @Schema(description = "Detalles de validacion, sin valores rechazados ni sensibles")
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
