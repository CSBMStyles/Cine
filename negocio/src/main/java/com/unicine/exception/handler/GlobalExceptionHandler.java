package com.unicine.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.MethodParameter;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.validation.method.ParameterValidationResult;

import com.unicine.exception.AuthenticationException;
import com.unicine.exception.AuthorizationException;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ExternalServiceException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.exception.UnicineException;
import com.unicine.exception.ValidationException;
import com.unicine.util.validation.catalog.ErrorCode;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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

        HttpStatus status = resolverEstadoDominio(ex.getErrorCatalog(), HttpStatus.BAD_REQUEST);
        ApiError error = ApiError.fromCatalog(
                status.value(),
                status.getReasonPhrase(),
                ex.getErrorCatalog(),
                extractPath(request)
        );

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Maneja violaciones de reglas de negocio (400 Bad Request).
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRule(BusinessRuleException ex, WebRequest request) {
        log.warn("Business rule violated: {} - {}", ex.getErrorCode(), ex.getMessage());

        HttpStatus status = resolverEstadoDominio(ex.getErrorCatalog(), HttpStatus.BAD_REQUEST);
        ApiError error = ApiError.fromCatalog(
                status.value(),
                status.getReasonPhrase(),
                ex.getErrorCatalog(),
                extractPath(request)
        );

        return ResponseEntity.status(status).body(error);
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
        List<ValidationErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::crearDetalleCampo)
                .toList();

        log.warn("Method argument validation failed: {}", details);

        ApiError error = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                null,
                "La solicitud contiene errores de validacion",
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de validacion de parametros en metodos MVC.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleHandlerMethodValidation(
            HandlerMethodValidationException ex,
            WebRequest request) {

        List<ValidationErrorDetail> details = ex.getParameterValidationResults().stream()
                .flatMap(this::crearDetallesParametro)
                .toList();

        log.warn("Handler method validation failed: {}", details);

        ApiError error = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                null,
                "La solicitud contiene errores de validacion",
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Maneja errores de validacion de @Validated en servicios (400 Bad Request).
     * Se activa cuando falla la validacion de parametros de metodo.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<ValidationErrorDetail> details = ex.getConstraintViolations().stream()
                .map(violation -> new ValidationErrorDetail(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .toList();

        log.warn("Constraint validation failed: {}", details);

        ApiError error = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                null,
                "La solicitud contiene errores de validacion",
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // !SECTION
    // SECTION: Excepciones de seguridad Spring

    /**
     * Maneja denegacion de acceso de Spring Security (403 Forbidden).
     * Cubre {@code AccessDeniedException} y su subtipo {@code AuthorizationDeniedException}
     * lanzado por {@code @PreAuthorize} y method security.
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiError> handleSpringAccessDenied(
            org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        log.warn("Spring access denied: {}", ex.getMessage());

        ApiError error = ApiError.of(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "DOMAIN_USER_AUTH_ACTION_NOT_PERMITTED",
                "No tiene permisos para realizar esta accion.",
                extractPath(request),
                List.of());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleAuthorizationDenied(
            org.springframework.security.authorization.AuthorizationDeniedException ex, WebRequest request) {
        log.warn("Authorization denied: {}", ex.getMessage());

        ApiError error = ApiError.of(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "DOMAIN_USER_AUTH_ACTION_NOT_PERMITTED",
                "No tiene permisos para realizar esta accion.",
                extractPath(request),
                List.of());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // !SECTION
    // SECTION: Errores de request incompleta (400)

    /**
     * Parametro de query faltante, p. ej. DELETE sin ?confirmacion=true.
     */
    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(
            org.springframework.web.bind.MissingServletRequestParameterException ex, WebRequest request) {
        log.warn("Missing request param: {}", ex.getParameterName());

        ValidationErrorDetail detail = new ValidationErrorDetail(ex.getParameterName(),
                "Parametro requerido no presente: " + ex.getParameterName());

        ApiError error = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                null,
                "La solicitud contiene errores de validacion",
                extractPath(request),
                List.of(detail));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.warn("Type mismatch: {}", ex.getMessage());

        ValidationErrorDetail detail = new ValidationErrorDetail(ex.getName(),
                ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());

        ApiError error = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                null,
                "La solicitud contiene errores de validacion",
                extractPath(request),
                List.of(detail));

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

    private ValidationErrorDetail crearDetalleCampo(FieldError error) {
        return new ValidationErrorDetail(error.getField(), error.getDefaultMessage());
    }

    private java.util.stream.Stream<ValidationErrorDetail> crearDetallesParametro(ParameterValidationResult result) {
        String field = obtenerNombreParametro(result.getMethodParameter());

        return result.getResolvableErrors().stream()
                .map(error -> new ValidationErrorDetail(field, obtenerMensaje(error)));
    }

    private String obtenerNombreParametro(MethodParameter parameter) {
        String parameterName = parameter.getParameterName();
        return parameterName != null ? parameterName : "parameter[" + parameter.getParameterIndex() + "]";
    }

    private String obtenerMensaje(MessageSourceResolvable error) {
        return error.getDefaultMessage() != null
                ? error.getDefaultMessage()
                : "El valor no cumple las reglas de validacion";
    }

    private HttpStatus resolverEstadoDominio(ErrorCode errorCode, HttpStatus estadoPredeterminado) {
        String code = errorCode.getCode();

        if (code.contains("_DUPLICATE_") || code.contains("_DELETE_")) {
            return HttpStatus.CONFLICT;
        }

        return estadoPredeterminado;
    }

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
