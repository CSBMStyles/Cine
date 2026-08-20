package com.unicine.transfer.dto.request;

import com.unicine.enums.confiteria.TipoCambioPrecioPresentacion;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.confiteria.HistorialPrecioPresentacion}.
 *
 * Incluido:
 * - {@code codigo}, {@code precioAnterior}, {@code precioNuevo}, {@code tipoCambio}, {@code porcentaje}, {@code fechaCambio}.
 * - Identificador de relacion: {@code presentacionCodigo}.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialPrecioPresentacionRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_PREVIOUS_PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_PREVIOUS_PRICE_POSITIVE_OR_ZERO)
    private Double precioAnterior;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_NEW_PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_NEW_PRICE_POSITIVE_OR_ZERO)
    private Double precioNuevo;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_TYPE_NOT_NULL)
    private TipoCambioPrecioPresentacion tipoCambio;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_PERCENTAGE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_PERCENTAGE_POSITIVE_OR_ZERO)
    private Integer porcentaje;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_DATE_NOT_NULL)
    private LocalDateTime fechaCambio;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_PRESENTATION_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer presentacionCodigo;
}
