package com.unicine.util.validation.catalog;

/**
 * Catalogo centralizado de mensajes de validacion para anotaciones Bean Validation.
 * 
 * Estas constantes se utilizan directamente en las anotaciones de entidades
 * (ej: @NotBlank(message = ValidationMessages.FIELD_REQUIRED)).
 * 
 * NOTA: Para parametrizacion completa y i18n, migrar a messages.properties en el futuro.
 * 
 * @author UniCine
 * @version 1.0
 */
public final class ValidationMessages {

    private ValidationMessages() {
        throw new AssertionError("No se puede instanciar una clase de utilidad");
    }

    // ============================================================
    // GENERALES - Campos obligatorios
    // ============================================================
    
    public static final String FIELD_REQUIRED = "Este campo no puede estar vacio";
    public static final String FIELD_BLANK = "Este campo no puede estar en blanco";
    public static final String FIELD_NULL = "Este campo no puede ser nulo";
    
    // ============================================================
    // IDENTIFICACION
    // ============================================================
    
    public static final String CEDULA_POSITIVE = "La cedula debe ser un numero positivo";
    public static final String CEDULA_NOT_NULL = "La cedula no puede estar vacia";
    
    // ============================================================
    // NOMBRES Y TEXTOS
    // ============================================================
    
    public static final String NAME_NOT_BLANK = "El nombre no puede estar en blanco";
    public static final String NAME_SIZE_MAX_50 = "El nombre no puede tener mas de cincuenta caracteres";
    public static final String NAME_SIZE_MAX_100 = "El nombre no puede tener mas de cien caracteres";
    public static final String NAME_SIZE_MAX_150 = "El nombre no puede tener mas de ciento cincuenta caracteres";
    public static final String NAME_SIZE_MIN_2 = "El nombre debe tener al menos dos caracteres";
    
    public static final String DESCRIPTION_NOT_BLANK = "La descripcion no puede estar en blanco";
    public static final String CRITERION_NOT_BLANK = "El criterio no puede estar en blanco";
    public static final String CRITERION_SIZE_MAX_100 = "El criterio no puede tener mas de cien caracteres";
    
    // ============================================================
    // CORREO ELECTRONICO
    // ============================================================
    
    public static final String EMAIL_NOT_NULL = "El correo no puede estar vacio";
    public static final String EMAIL_INVALID = "El correo no tiene un formato valido";
    public static final String EMAIL_SIZE_MAX_150 = "El correo no puede tener mas de ciento cincuenta caracteres";
    
    // ============================================================
    // CONTRASENA
    // ============================================================
    
    public static final String PASSWORD_NOT_BLANK = "La contrasena no puede estar en blanco";
    public static final String PASSWORD_SIZE_MIN_8 = "La contrasena debe tener al menos ocho caracteres";
    public static final String PASSWORD_SIZE_MAX_200 = "La contrasena no puede tener mas de doscientos caracteres";
    public static final String PASSWORD_UPPERCASE = "La contrasena debe contener al menos una letra mayuscula";
    public static final String PASSWORD_LOWERCASE = "La contrasena debe contener al menos una letra minuscula";
    public static final String PASSWORD_DIGIT = "La contrasena debe contener al menos un digito";
    public static final String PASSWORD_SPECIAL = "La contrasena debe contener al menos un caracter especial";
    
    // ============================================================
    // TELEFONO
    // ============================================================
    
    public static final String PHONE_NOT_NULL = "El telefono no puede estar vacio";
    public static final String PHONE_ONLY_NUMBERS = "El telefono solo puede contener numeros";
    public static final String PHONE_SIZE_EXACT_10 = "El telefono debe tener exactamente diez caracteres";
    public static final String PHONE_LIST_MAX_5 = "No se pueden registrar mas de cinco telefonos";
    
    // ============================================================
    // FECHAS
    // ============================================================
    
    public static final String DATE_NOT_NULL = "La fecha no puede estar vacia";
    public static final String DATE_PAST = "La fecha debe estar en el pasado";
    public static final String DATE_FUTURE = "La fecha debe estar en el futuro";
    public static final String DATE_FUTURE_OR_PRESENT = "La fecha debe estar en el presente o en el futuro";
    public static final String BIRTH_DATE_NOT_NULL = "La fecha de nacimiento no puede estar vacia";
    
    // ============================================================
    // PRECIOS Y VALORES NUMERICOS
    // ============================================================
    
    public static final String PRICE_NOT_NULL = "El precio no puede estar vacio";
    public static final String PRICE_POSITIVE = "El precio debe ser un numero positivo";
    public static final String PRICE_POSITIVE_OR_ZERO = "El precio debe ser un numero positivo o cero";
    
    public static final String VALUE_NOT_NULL = "El valor no puede estar vacio";
    public static final String VALUE_POSITIVE = "El valor debe ser un numero positivo";
    public static final String VALUE_POSITIVE_OR_ZERO = "El valor debe ser un numero positivo o cero";
    public static final String VALUE_MAX = "El valor excede el maximo permitido";
    
    public static final String UNITS_NOT_NULL = "Las unidades no pueden estar vacias";
    public static final String UNITS_POSITIVE_OR_ZERO = "Las unidades deben ser un numero positivo o cero";
    
    public static final String DISCOUNT_NOT_NULL = "El descuento no puede estar vacio";
    public static final String DISCOUNT_POSITIVE_OR_ZERO = "El descuento debe ser un numero positivo o cero";
    public static final String DISCOUNT_MAX_TOTAL = "El descuento no puede ser mayor al total";
    
    // ============================================================
    // DIRECCION Y UBICACION
    // ============================================================
    
    public static final String ADDRESS_NOT_BLANK = "La direccion no puede estar vacia";
    public static final String ADDRESS_SIZE_MIN_4 = "La direccion debe tener al menos cuatro caracteres";
    public static final String ADDRESS_SIZE_MAX_100 = "La direccion no puede tener mas de cien caracteres";
    
    public static final String CITY_NAME_NOT_BLANK = "El nombre de la ciudad no puede estar en blanco";
    public static final String CITY_NAME_SIZE_MIN_2 = "El nombre de la ciudad debe tener al menos dos caracteres";
    public static final String CITY_NAME_SIZE_MAX_100 = "El nombre de la ciudad no puede tener mas de cien caracteres";
    public static final String CITY_NAME_PATTERN = "El nombre de la ciudad solo puede contener letras y espacios";
    
    // ============================================================
    // PELICULA
    // ============================================================
    
    public static final String MOVIE_NAME_NOT_BLANK = "El nombre de la pelicula no puede estar en blanco";
    public static final String MOVIE_NAME_SIZE_MAX_100 = "El nombre de la pelicula no puede tener mas de cien caracteres";
    public static final String MOVIE_SYNOPSIS_NOT_BLANK = "La sinopsis no puede estar vacia";
    public static final String MOVIE_TRAILER_URL_SIZE_MAX_200 = "La url del trailer no puede tener mas de doscientos caracteres";
    public static final String MOVIE_RATING_MAX_5 = "La puntuacion no puede ser mayor a cinco";
    public static final String MOVIE_RATING_POSITIVE = "La puntuacion debe ser un numero positivo";
    public static final String MOVIE_AGE_RESTRICTION_MAX_30 = "La restriccion de edad no puede ser mayor a treinta";
    public static final String MOVIE_AGE_RESTRICTION_POSITIVE = "La restriccion de edad debe ser un numero positivo";
    public static final String MOVIE_ACTOR_ROLE_SIZE_MAX_150 = "El rol del actor no puede tener mas de ciento cincuenta caracteres";
    public static final String MOVIE_ACTOR_NAME_SIZE_MAX_150 = "El nombre del actor no puede tener mas de ciento cincuenta caracteres";
    
    // ============================================================
    // TEATRO Y SALA
    // ============================================================
    
    public static final String THEATER_NAME_NOT_BLANK = "El nombre del teatro no puede estar en blanco";
    public static final String THEATER_PHONE_NOT_BLANK = "El telefono del teatro no puede estar vacio";
    public static final String THEATER_CITY_NOT_NULL = "La ciudad no puede estar vacia";
    public static final String THEATER_ADMIN_NOT_NULL = "El administrador no puede estar vacio";
    
    public static final String ROOM_NAME_NOT_BLANK = "El nombre de la sala no puede estar vacio";
    public static final String ROOM_TYPE_NOT_NULL = "El tipo de sala no puede estar vacio";
    public static final String ROOM_DISTRIBUTION_NOT_NULL = "La distribucion de sillas no puede estar vacia";
    public static final String ROOM_THEATER_NOT_NULL = "El teatro no puede estar vacio";
    
    // ============================================================
    // DISTRIBUCION DE SILLAS
    // ============================================================
    
    public static final String SEAT_SCHEMA_NOT_BLANK = "El esquema no puede estar vacio";
    public static final String SEAT_TOTAL_NOT_NULL = "El total de sillas no puede estar vacio";
    public static final String SEAT_TOTAL_POSITIVE = "El total de sillas debe ser un numero positivo";
    public static final String SEAT_ROWS_NOT_NULL = "El numero de filas no puede estar vacio";
    public static final String SEAT_ROWS_POSITIVE = "El numero de filas debe ser un numero positivo";
    public static final String SEAT_COLUMNS_NOT_NULL = "El numero de columnas no puede estar vacio";
    public static final String SEAT_COLUMNS_POSITIVE = "El numero de columnas debe ser un numero positivo";
    
    // ============================================================
    // FUNCION Y HORARIO
    // ============================================================
    
    public static final String SCHEDULE_START_NOT_NULL = "La fecha de inicio no puede estar vacia";
    public static final String SCHEDULE_START_FUTURE = "La fecha de inicio debe ser en el futuro";
    public static final String SCHEDULE_END_NOT_NULL = "La fecha de fin no puede estar vacia";
    public static final String SCHEDULE_END_FUTURE = "La fecha de fin debe ser en el futuro";
    
    public static final String SHOWING_FORMAT_NOT_NULL = "El formato no puede estar vacio";
    public static final String SHOWING_SCHEDULE_NOT_NULL = "El horario no puede estar vacio";
    public static final String SHOWING_ROOM_NOT_NULL = "La sala no puede estar vacia";
    public static final String SHOWING_MOVIE_NOT_NULL = "La pelicula no puede estar vacia";
    public static final String SHOWING_PRICE_NOT_NULL = "El precio no puede estar vacio";
    public static final String SHOWING_PRICE_POSITIVE_OR_ZERO = "El precio debe ser un numero positivo o cero";
    
    public static final String SHOWING_SCHEMA_NOT_BLANK = "El esquema no puede estar vacio";
    public static final String SHOWING_SCHEMA_SHOWING_NOT_NULL = "La funcion no puede estar vacia";
    public static final String SHOWING_SCHEMA_OCCUPIED_NOT_NULL = "El numero de sillas ocupadas no puede estar vacio";
    public static final String SHOWING_SCHEMA_OCCUPIED_POSITIVE = "El numero de sillas ocupadas debe ser un numero positivo o cero";
    public static final String SHOWING_SCHEMA_AVAILABLE_NOT_NULL = "El numero de sillas disponibles no puede estar vacio";
    public static final String SHOWING_SCHEMA_AVAILABLE_POSITIVE = "El numero de sillas disponibles debe ser un numero positivo o cero";
    public static final String SHOWING_SCHEMA_MAINTENANCE_NOT_NULL = "El numero de sillas en mantenimiento no puede estar vacio";
    public static final String SHOWING_SCHEMA_MAINTENANCE_POSITIVE = "El numero de sillas en mantenimiento debe ser un numero positivo o cero";
    
    // ============================================================
    // COMPRA Y CUPON
    // ============================================================
    
    public static final String PURCHASE_STATUS_NOT_NULL = "El estado de la compra no puede estar vacio";
    public static final String PURCHASE_DATE_NOT_NULL = "La fecha de compra no puede estar vacia";
    public static final String PURCHASE_MOVIE_DATE_NOT_NULL = "La fecha de la pelicula no puede estar vacia";
    public static final String PURCHASE_MOVIE_DATE_FUTURE = "La fecha de la pelicula debe estar en el presente o en el futuro";
    public static final String PURCHASE_TOTAL_NOT_NULL = "El valor total no puede estar vacio";
    public static final String PURCHASE_TOTAL_POSITIVE = "El valor total debe ser un numero positivo";
    public static final String PURCHASE_PAYMENT_NOT_NULL = "El medio de pago no puede estar vacio";
    public static final String PURCHASE_CLIENT_NOT_NULL = "El cliente no puede estar vacio";
    public static final String PURCHASE_SHOWING_NOT_NULL = "La funcion no puede estar vacia";
    
    public static final String COUPON_DESCRIPTION_NOT_BLANK = "La descripcion no puede estar en blanco";
    public static final String COUPON_CRITERION_NOT_BLANK = "El criterio no puede estar en blanco";
    public static final String COUPON_DISCOUNT_NOT_NULL = "El descuento no puede estar vacio";
    public static final String COUPON_EXPIRY_NOT_NULL = "La fecha de vencimiento no puede estar vacia";
    public static final String COUPON_EXPIRY_FUTURE = "La fecha de vencimiento debe estar en el presente o en el futuro";
    
    public static final String CLIENT_COUPON_STATUS_NOT_NULL = "El estado no puede estar vacio";
    public static final String CLIENT_COUPON_COUPON_NOT_NULL = "El cupon no puede estar vacio";
    public static final String CLIENT_COUPON_CLIENT_NOT_NULL = "El cliente no puede estar vacio";
    
    // ============================================================
    // ENTRADA
    // ============================================================
    
    public static final String TICKET_PRICE_NOT_NULL = "El precio no puede estar vacio";
    public static final String TICKET_PRICE_POSITIVE = "El precio debe ser un numero positivo";
    public static final String TICKET_ROW_NOT_NULL = "La fila no puede estar vacia";
    public static final String TICKET_ROW_POSITIVE = "La fila debe ser un numero positivo";
    public static final String TICKET_COLUMN_NOT_NULL = "La columna no puede estar vacia";
    public static final String TICKET_COLUMN_POSITIVE = "La columna debe ser un numero positivo";
    public static final String TICKET_PURCHASE_NOT_NULL = "La compra no puede estar vacia";
    
    // ============================================================
    // CONFITERIA
    // ============================================================
    
    public static final String CONFECTIONERY_NAME_NOT_BLANK = "El nombre no puede estar en blanco";
    public static final String CONFECTIONERY_NAME_SIZE_MAX_100 = "El nombre no puede tener mas de cien caracteres";
    public static final String CONFECTIONERY_PRICE_NOT_NULL = "El precio no puede estar vacio";
    public static final String CONFECTIONERY_PRICE_POSITIVE_OR_ZERO = "El precio debe ser un numero positivo o cero";
    
    // ============================================================
    // IMAGEN
    // ============================================================
    
    public static final String IMAGE_CODE_NOT_BLANK = "El codigo de la imagen no puede estar vacio";
    public static final String IMAGE_URL_NOT_BLANK = "La url no puede estar vacia";
    
    // ============================================================
    // ESTADO CLIENTE
    // ============================================================
    
    public static final String CLIENT_STATUS_NOT_NULL = "El estado no puede estar vacio";
    public static final String CLIENT_PHONE_NOT_NULL = "El telefono no puede estar vacio";
    public static final String CLIENT_BIRTH_DATE_PAST = "La fecha de nacimiento debe estar en el pasado";
    
}
