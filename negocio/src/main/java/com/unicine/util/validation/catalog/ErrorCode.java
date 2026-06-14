package com.unicine.util.validation.catalog;

/**
 * Interfaz comun para todos los catalogos de error del sistema.
 * 
 * Permite que las excepciones de negocio ({@link com.unicine.exception.UnicineException})
 * acepten cualquier catalogo de error sin acoplamiento a un tipo concreto.
 * 
 * @author UniCine
 * @version 1.1
 */
public interface ErrorCode {

    /**
     * Codigo unico del error (ej: ENT001, REG005).
     */
    String getCode();

    /**
     * Mensaje base humano-legible del error.
     */
    String getMessage();

    /**
     * Obtiene el mensaje formateado con parametros posicionales {0}, {1}, etc.
     * 
     * @param args Argumentos para reemplazar en el mensaje
     * @return Mensaje formateado
     */
    String format(Object... args);
}
