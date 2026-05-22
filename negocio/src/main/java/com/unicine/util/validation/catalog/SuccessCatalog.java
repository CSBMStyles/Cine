package com.unicine.util.validation.catalog;

/**
 * Catalogo centralizado de mensajes de exito para operaciones de negocio.
 * 
 * Cada mensaje de exito tiene:
 * - Codigo unico por dominio (formato: SUC###)
 * - Mensaje humano-legible
 * - Categoria funcional
 * 
 * NOTA: El HTTP status para exitos generalmente es 200 OK o 201 Created,
 * asignado en el controlador REST, no en este catalogo.
 * 
 * @author UniCine
 * @version 1.0
 * @see ErrorCatalog
 */
public enum SuccessCatalog {

    // ============================================================
    // CREACION / REGISTRO (201 Created)
    // ============================================================
    
    SUC001("SUC001", "Registro creado correctamente"),
    SUC002("SUC002", "Administrador registrado correctamente"),
    SUC003("SUC003", "Cliente registrado correctamente"),
    SUC004("SUC004", "Administrador de teatro registrado correctamente"),
    SUC005("SUC005", "Pelicula registrada correctamente"),
    SUC006("SUC006", "Teatro registrado correctamente"),
    SUC007("SUC007", "Sala registrada correctamente"),
    SUC008("SUC008", "Ciudad registrada correctamente"),
    SUC009("SUC009", "Funcion registrada correctamente"),
    SUC010("SUC010", "Horario registrado correctamente"),
    SUC011("SUC011", "Esquema de funcion registrado correctamente"),
    SUC012("SUC012", "Cupon registrado correctamente"),
    SUC013("SUC013", "Confiteria registrada correctamente"),
    SUC014("SUC014", "Distribucion de sillas registrada correctamente"),
    SUC015("SUC015", "Imagen subida correctamente"),
    
    // ============================================================
    // ACTUALIZACION (200 OK)
    // ============================================================
    
    SUC101("SUC101", "Registro actualizado correctamente"),
    SUC102("SUC102", "Administrador actualizado correctamente"),
    SUC103("SUC103", "Cliente actualizado correctamente"),
    SUC104("SUC104", "Contrasena actualizada correctamente"),
    SUC105("SUC105", "Pelicula actualizada correctamente"),
    SUC106("SUC106", "Teatro actualizado correctamente"),
    SUC107("SUC107", "Sala actualizada correctamente"),
    SUC108("SUC108", "Imagen actualizada correctamente"),
    SUC109("SUC109", "Estado de la pelicula actualizado correctamente"),
    
    // ============================================================
    // ELIMINACION (200 OK)
    // ============================================================
    
    SUC201("SUC201", "Registro eliminado correctamente"),
    SUC202("SUC202", "Administrador eliminado correctamente"),
    SUC203("SUC203", "Cliente eliminado correctamente"),
    SUC204("SUC204", "Pelicula eliminada correctamente"),
    SUC205("SUC205", "Teatro eliminado correctamente"),
    SUC206("SUC206", "Sala eliminada correctamente"),
    SUC207("SUC207", "Imagen eliminada correctamente"),
    
    // ============================================================
    // AUTENTICACION / AUTORIZACION (200 OK)
    // ============================================================
    
    SUC301("SUC301", "Inicio de sesion exitoso"),
    SUC302("SUC302", "Cierre de sesion exitoso"),
    SUC303("SUC303", "Token refrescado correctamente"),
    SUC304("SUC304", "Cuenta activada correctamente"),
    SUC305("SUC305", "Correo de recuperacion enviado correctamente"),
    SUC306("SUC306", "Contrasena restablecida correctamente"),
    
    // ============================================================
    // COMPRAS / TRANSACCIONES (200 OK)
    // ============================================================
    
    SUC401("SUC401", "Compra realizada con exito"),
    SUC402("SUC402", "Pago procesado correctamente"),
    SUC403("SUC403", "Entradas generadas correctamente"),
    SUC404("SUC404", "Cupon aplicado correctamente"),
    SUC405("SUC405", "Descuento aplicado correctamente"),
    
    // ============================================================
    // NOTIFICACIONES / EMAIL (200 OK)
    // ============================================================
    
    SUC501("SUC501", "Correo enviado correctamente"),
    SUC502("SUC502", "Notificacion enviada correctamente"),
    SUC503("SUC503", "Recordatorio programado correctamente"),
    
    // ============================================================
    // OPERACIONES ESPECIFICAS (200 OK)
    // ============================================================
    
    SUC601("SUC601", "Pelicula agregada a la coleccion correctamente"),
    SUC602("SUC602", "Pelicula removida de la coleccion correctamente"),
    SUC603("SUC603", "Funcion asignada a la sala correctamente"),
    SUC604("SUC604", "Disposicion de pelicula configurada correctamente"),
    SUC605("SUC605", "Imagen restaurada a version anterior correctamente"),
    SUC606("SUC606", "Archivo renombrado correctamente"),
    
    // ============================================================
    // GENERALES (200 OK)
    // ============================================================
    
    SUC901("SUC901", "Operacion completada con exito"),
    SUC902("SUC902", "Solicitud procesada correctamente"),
    SUC903("SUC903", "Datos recuperados correctamente");
    
    // ============================================================
    // ATRIBUTOS
    // ============================================================
    
    private final String code;
    private final String message;
    
    SuccessCatalog(String code, String message) {
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
     * Ejemplo: SuccessCatalog.SUC001.format("Pelicula")
     * Resultado: "Registro creado correctamente: Pelicula"
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
