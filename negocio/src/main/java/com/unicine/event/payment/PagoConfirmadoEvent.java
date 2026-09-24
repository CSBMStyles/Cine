package com.unicine.event.payment;

import java.time.LocalDateTime;

/**
 * Evento emitido cuando un pago llega a PAGADA por webhook o reconciliacion.
 * Escuchado por Factus 3.B y futuros correos de pago aprobado, sin tocar pagos.
 */
public record PagoConfirmadoEvent(
    Integer compraCodigo,
    String mercadoPagoId,
    Double montoConfirmado,
    LocalDateTime fechaConfirmacion
) {}
