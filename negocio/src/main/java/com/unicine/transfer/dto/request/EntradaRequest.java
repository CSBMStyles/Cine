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
 * DTO de entrada para la entidad {@link com.unicine.entity.purchase.Entrada}.
 *
 * Incluido:
 * - {@code codigo}, {@code precio}, {@code fila}, {@code columna}.
 * - Identificadores de relaciones: {@code compraCodigo}, {@code funcionCodigo}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntradaRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotNull(message = ValidationMessages.TICKET_PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.TICKET_PRICE_POSITIVE)
    private Double precio;

    @NotNull(message = ValidationMessages.TICKET_ROW_NOT_NULL)
    @Positive(message = ValidationMessages.TICKET_ROW_POSITIVE)
    private Integer fila;

    @NotNull(message = ValidationMessages.TICKET_COLUMN_NOT_NULL)
    @Positive(message = ValidationMessages.TICKET_COLUMN_POSITIVE)
    private Integer columna;

    @NotNull(message = ValidationMessages.TICKET_PURCHASE_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer compraCodigo;

    @NotNull(message = ValidationMessages.TICKET_SHOWING_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer funcionCodigo;
}
