package com.unicine.transfer.dto.request;

import com.unicine.util.validation.catalog.ValidationMessages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO de entrada para crear la orden de pago en Mercado Pago.
 *
 * Incluido:
 * - {@code compraCodigo}: compra ya registrada por checkout 4.4.
 *
 * Excluido:
 * - Monto, pagador e idempotencia: los resuelve el servidor (4.7.2).
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenPagoRequest {

    @NotNull(message = ValidationMessages.PAYMENT_PURCHASE_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer compraCodigo;
}
