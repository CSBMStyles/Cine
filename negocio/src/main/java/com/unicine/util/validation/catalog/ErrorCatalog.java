package com.unicine.util.validation.catalog;

/**
 * Catalogo centralizado de errores de negocio.
 *
 * <p><strong>DEPRECATED:</strong> Este catalogo monolitico se mantiene temporalmente
 * por compatibilidad. Los nuevos servicios deben usar los catalogos de dominio en
 * {@link com.unicine.util.validation.catalog.domain}:
 * <ul>
 *   <li>{@link com.unicine.util.validation.catalog.domain.UserErrorCatalog}</li>
 *   <li>{@link com.unicine.util.validation.catalog.domain.MovieErrorCatalog}</li>
 *   <li>{@link com.unicine.util.validation.catalog.domain.TheaterErrorCatalog}</li>
 *   <li>{@link com.unicine.util.validation.catalog.domain.ShowingErrorCatalog}</li>
 *   <li>{@link com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog}</li>
 *   <li>{@link com.unicine.util.validation.catalog.domain.ImageErrorCatalog}</li>
 *   <li>{@link com.unicine.util.validation.catalog.domain.SystemErrorCatalog}</li>
 * </ul>
 *
 * @deprecated Usar catalogos de dominio en {@code com.unicine.util.validation.catalog.domain}.
 *     Este archivo sera eliminado en una version futura cuando todos los servicios
 *     esten migrados.
 * @author UniCine
 * @version 1.1
 */
@Deprecated(since = "1.1", forRemoval = true)
public enum ErrorCatalog implements ErrorCode {

    // ============================================================
    // VAL - VALIDACION DE PARAMETROS (400 Bad Request)
    // ============================================================
    
    VAL001("VAL001", "El parametro no puede estar vacio"),
    VAL002("VAL002", "El parametro debe ser un numero positivo"),
    VAL003("VAL003", "El parametro no puede estar en blanco"),
    VAL004("VAL004", "El codigo no puede estar vacio"),
    VAL005("VAL005", "El codigo debe ser un numero positivo"),
    VAL006("VAL006", "El nombre no puede estar en blanco"),
    VAL007("VAL007", "El codigo de teatro no puede estar vacio"),
    VAL008("VAL008", "El codigo de teatro debe ser un numero positivo"),
    
    // ============================================================
    // ENT - ENTIDAD NO ENCONTRADA (404 Not Found)
    // ============================================================
    
    ENT001("ENT001", "El administrador no existe"),
    ENT002("ENT002", "El administrador de teatro no existe"),
    ENT003("ENT003", "El cliente no existe"),
    ENT004("ENT004", "La ciudad no existe"),
    ENT005("ENT005", "No existe ciudad con ese nombre"),
    ENT006("ENT006", "El teatro no existe"),
    ENT007("ENT007", "La sala no existe"),
    ENT008("ENT008", "No existe salas con ese nombre"),
    ENT009("ENT009", "La pelicula no existe"),
    ENT010("ENT010", "No existe peliculas con ese nombre"),
    ENT011("ENT011", "La disposicion de pelicula no existe"),
    ENT012("ENT012", "La distribucion de sillas no existe"),
    ENT013("ENT013", "La funcion no existe"),
    ENT014("ENT014", "El esquema de la funcion no existe"),
    ENT015("ENT015", "El horario no existe"),
    ENT016("ENT016", "El cupon no existe"),
    ENT017("ENT017", "La entrada no existe"),
    ENT018("ENT018", "La compra no existe"),
    ENT019("ENT019", "La confiteria no existe"),
    ENT020("ENT020", "La imagen no existe"),
    ENT021("ENT021", "El genero no existe"),
    ENT022("ENT022", "La coleccion no existe"),
    
    // ============================================================
    // DUP - CONFLICTO / DUPLICADO (409 Conflict)
    // ============================================================
    
    DUP001("DUP001", "La cedula ya esta registrada"),
    DUP002("DUP002", "Este correo ya esta registrado"),
    DUP003("DUP003", "La pelicula ya existe"),
    DUP004("DUP004", "El nombre que esta ingresando ya existe"),
    DUP005("DUP005", "El nombre de la sala ya existe en el teatro"),
    DUP006("DUP006", "La direccion del teatro ya existe en la ciudad"),
    DUP007("DUP007", "La persona ya tiene una imagen, deberia utilizar el metodo actualizar"),
    
    // ============================================================
    // DEL - ELIMINACION NO CONFIRMADA (409 Conflict)
    // ============================================================
    
    DEL001("DEL001", "La eliminacion no fue confirmada"),
    
    // ============================================================
    // AUTH - AUTENTICACION (401 Unauthorized)
    // ============================================================
    
    AUTH001("AUTH001", "Credenciales invalidas"),
    AUTH002("AUTH002", "El correo no existe"),
    AUTH003("AUTH003", "Los datos de autenticacion son incorrectos"),
    AUTH004("AUTH004", "La contrasena actual es incorrecta"),
    AUTH005("AUTH005", "La nueva contrasena no puede ser igual a la actual"),
    AUTH006("AUTH006", "El cliente no esta activo, debe activarla con el enlace que fue enviado a su correo"),
    AUTH007("AUTH007", "El usuario no tiene permisos para realizar esta accion"),
    
    // ============================================================
    // REG - REGLAS DE NEGOCIO (400 Bad Request)
    // ============================================================
    
    REG001("REG001", "El cliente debe ser mayor de edad para registrarse"),
    REG002("REG002", "El tamano de la imagen excede el limite permitido de {0} MB. Tamano actual: {1} MB"),
    REG003("REG003", "El horario se solapa con uno existente"),
    REG004("REG004", "La funcion no tiene horarios disponibles"),
    REG005("REG005", "La sala no tiene suficientes sillas disponibles"),
    REG006("REG006", "El cupon ya fue utilizado"),
    REG007("REG007", "El cupon ha expirado"),
    REG008("REG008", "El descuento no puede ser mayor al total de la compra"),
    REG009("REG009", "La compra no puede modificarse porque ya fue procesada"),
    
    // ============================================================
    // EXT - SISTEMA EXTERNO (502 Bad Gateway)
    // ============================================================
    
    EXT001("EXT001", "Error al subir la imagen: {0}"),
    EXT002("EXT002", "Error al actualizar la imagen: {0}"),
    EXT003("EXT003", "Error al restaurar la version de la imagen: {0}"),
    EXT004("EXT004", "Error al renombrar el archivo: {0}"),
    EXT005("EXT005", "Error al eliminar la imagen: {0}"),
    EXT006("EXT006", "Error al eliminar las imagenes: {0}"),
    EXT007("EXT007", "Error al obtener los datos de la imagen: {0}"),
    EXT008("EXT008", "Error al listar las imagenes: {0}"),
    EXT009("EXT009", "Error al listar las versiones de la imagen: {0}"),
    
    // ============================================================
    // GEN - GENERALES / INTERNOS (500 Internal Server Error)
    // ============================================================
    
    GEN001("GEN001", "Error inesperado del servidor: {0}"),
    GEN002("GEN002", "Operacion no soportada"),
    GEN003("GEN003", "Error al procesar la solicitud"),
    GEN004("GEN004", "Servicio no disponible temporalmente");
    
    // ============================================================
    // ATRIBUTOS
    // ============================================================
    
    private final String code;
    private final String message;
    
    ErrorCatalog(String code, String message) {
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
     * Ejemplo: ErrorCatalog.REG002.format(5, 10.5)
     * Resultado: "El tamano de la imagen excede el limite permitido de 5 MB. Tamano actual: 10.5 MB"
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
