package com.unicine.transfer.dto.request;

import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.purchase.CompraConfiteria}.
 *
 * Incluido:
 * - {@code codigo}, {@code precio}, {@code unidades}.
 * - Identificadores de relaciones: {@code compraCodigo}, {@code presentacionCodigo}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraConfiteriaRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotNull(message = ValidationMessages.PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.PRICE_POSITIVE_OR_ZERO)
    private Double precio;

    @NotNull(message = ValidationMessages.UNITS_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.UNITS_POSITIVE_OR_ZERO)
    private Integer unidades;

    @NotNull(message = ValidationMessages.PURCHASE_CONFECTIONERY_PURCHASE_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer compraCodigo;

    @NotNull(message = ValidationMessages.PURCHASE_CONFECTIONERY_PRESENTATION_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer presentacionCodigo;
}
