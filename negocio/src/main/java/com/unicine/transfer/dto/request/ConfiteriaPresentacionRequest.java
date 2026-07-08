package com.unicine.transfer.dto.request;

import com.unicine.enums.confiteria.UnidadMedida;
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
 * DTO de entrada para la entidad {@link com.unicine.entity.confiteria.ConfiteriaPresentacion}.
 *
 * Incluido:
 * - {@code codigo}, {@code porcion}, {@code unidadMedida}, {@code precio}, {@code precioBase}, {@code fechaExpiracionTemporal}.
 * - Identificador de relacion: {@code confiteriaCodigo}.
 *
 * Excluido:
 * - Historial de precios: se gestiona desde el endpoint correspondiente.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiteriaPresentacionRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRESENTATION_PORTION_NOT_NULL)
    @Positive(message = ValidationMessages.CONFECTIONERY_PRESENTATION_PORTION_POSITIVE)
    private Double porcion;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRESENTATION_UNIT_NOT_NULL)
    private UnidadMedida unidadMedida;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRESENTATION_PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.CONFECTIONERY_PRESENTATION_PRICE_POSITIVE_OR_ZERO)
    private Double precio;

    @PositiveOrZero(message = ValidationMessages.CONFECTIONERY_PRESENTATION_BASE_PRICE_POSITIVE_OR_ZERO)
    private Double precioBase;

    private LocalDateTime fechaExpiracionTemporal;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRESENTATION_CONFECTIONERY_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer confiteriaCodigo;
}
