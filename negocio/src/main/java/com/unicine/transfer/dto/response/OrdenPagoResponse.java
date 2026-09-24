package com.unicine.transfer.dto.response;

import com.unicine.enums.payment.EstadoPago;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para la orden de pago de {@link com.unicine.entity.payment.Pago}.
 *
 * Incluido:
 * - {@code compraCodigo}, {@code mercadoPagoId}, {@code checkoutUrl}, {@code estado}.
 *
 * Excluido:
 * - {@code idempotencyKey}: secreto de reintento, nunca sale al cliente.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenPagoResponse {

    private Integer compraCodigo;

    private String mercadoPagoId;

    private String checkoutUrl;

    private EstadoPago estado;
}
