package com.unicine.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.unicine.exception.AuthenticationException;
import com.unicine.exception.AuthorizationException;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ExternalServiceException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.exception.UnicineException;
import com.unicine.exception.ValidationException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la API REST de UniCine.
 * 
 * Captura todas las excepciones del dominio y las mapea a respuestas HTTP
 * estandarizadas con el formato {@link ApiError}.
 * 
 * Mapeo de excepciones a HTTP status:
 * <ul>
 *   <li>{@link ResourceNotFoundException} → 404 Not Found</li>
 *   <li>{@link ValidationException} → 400 Bad Request</li>
 *   <li>{@link BusinessRuleException} → 400 Bad Request</li>
 *   <li>{@link AuthenticationException} → 401 Unauthorized</li>
 *   <li>{@link AuthorizationException} → 403 Forbidden</li>
 *   <li>{@link ExternalServiceException} → 502 Bad Gateway</li>
 *   <li>{@link UnicineException} (generica) → 500 Internal Server Error</li>
 *   <li>{@link MethodArgumentNotValidException} (Spring Validation) → 400 Bad Request</li>
 *   <li>{@link ConstraintViolationException} (Jakarta Validation) → 400 Bad Request</li>
 *   <li>{@link Exception} (cualquier otra) → 500 Internal Server Error</li>
 * </ul>
 * 
 * @author UniCine
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // SECTION: Excepciones del dominio Unicine

    /**
     * Maneja errores cuando un recurso no existe (404 Not Found).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        ApiError error = ApiError.fromCatalog(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getErrorCatalog(),
                extractPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja errores de validacion de datos de entrada (400 Bad Request).
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidation(ValidationException ex, WebRequest request) {
        log.warn("Validation error: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        ApiError error = ApiError.fromCatalog(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getErrorCatalog(),
                extractPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja violaciones de reglas de negocio (400 Bad Request).
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRule(BusinessRuleException ex, WebRequest request) {
        log.warn("Business rule violated: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        ApiError error = ApiError.fromCatalog(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getErrorCatalog(),
                extractPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de autenticacion (401 Unauthorized).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex, WebRequest request) {
        log.warn("Authentication failed: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        ApiError error = ApiError.fromCatalog(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getErrorCatalog(),
                extractPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Maneja errores de autorizacion / permisos (403 Forbidden).
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiError> handleAuthorization(AuthorizationException ex, WebRequest request) {
        log.warn("Authorization denied: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        ApiError error = ApiError.fromCatalog(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex.getErrorCatalog(),
                extractPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Maneja errores de servicios externos (502 Bad Gateway).
     */
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiError> handleExternalService(ExternalServiceException ex, WebRequest request) {
        log.error("External service error: {} - {}", ex.getErrorCode(), ex.getMessage(), ex.getCause());
        
        ApiError error = ApiError.fromCatalog(
                HttpStatus.BAD_GATEWAY.value(),
                HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                ex.getErrorCatalog(),
                extractPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    /**
     * Maneja cualquier otra excepcion del dominio UniCine (500 Internal Server Error).
     */
    @ExceptionHandler(UnicineException.class)
    public ResponseEntity<ApiError> handleUnicineException(UnicineException ex, WebRequest request) {
        log.error("UniCine exception: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);
        
        ApiError error = ApiError.fromCatalog(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getErrorCatalog(),
                extractPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // !SECTION
    // SECTION: Excepciones de validacion de Spring / Jakarta

    /**
     * Maneja errores de validacion de @Valid en controllers (400 Bad Request).
     * Se activa cuando falla la validacion de un @RequestBody.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("Method argument validation failed: {}", errors);
        
        ApiError error = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                null,
                errors,
                extractPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de validacion de @Validated en servicios (400 Bad Request).
     * Se activa cuando falla la validacion de parametros de metodo.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        String errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        
        log.warn("Constraint validation failed: {}", errors);
        
        ApiError error = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                null,
                errors,
                extractPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // !SECTION
    // SECTION: Excepciones genericas

    /**
     * Manejador de ultimo recurso para cualquier excepcion no capturada (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ApiError error = ApiError.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                null,
                "Error interno del servidor. Contacte al administrador.",
                extractPath(request)
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // !SECTION
    // SECTION: Utilidades

    /**
     * Extrae la ruta del request desde el WebRequest.
     */
    private String extractPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            return servletRequest.getRequest().getRequestURI();
        }
        return request.getDescription(false).replace("uri=", "");
    }
    // !SECTION
}
