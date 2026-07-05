package com.unicine.transfer.dto.request;

import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.purchase.CuponCliente}.
 *
 * Incluido:
 * - {@code codigo}, {@code estado}.
 * - Identificadores de relaciones: {@code cuponCodigo}, {@code clienteCedula}.
 *
 * Excluido:
 * - Compra asociada: se gestiona desde el endpoint de compras.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuponClienteRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotNull(message = ValidationMessages.CLIENT_COUPON_STATUS_NOT_NULL)
    private Boolean estado;

    @NotNull(message = ValidationMessages.CLIENT_COUPON_COUPON_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer cuponCodigo;

    @NotNull(message = ValidationMessages.CLIENT_COUPON_CLIENT_NOT_NULL)
    @Positive(message = ValidationMessages.CEDULA_POSITIVE)
    private Integer clienteCedula;
}
