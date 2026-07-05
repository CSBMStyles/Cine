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
 * DTO de entrada para la entidad {@link com.unicine.entity.showing.FuncionEsquema}.
 *
 * Incluido:
 * - {@code codigo}, {@code esquemaTemporal}, {@code ocupadas}, {@code disponibles}, {@code mantenimiento}.
 * - Identificador de relacion: {@code funcionCodigo}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuncionEsquemaRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    private String esquemaTemporal;

    @NotNull(message = ValidationMessages.SHOWING_SCHEMA_OCCUPIED_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SHOWING_SCHEMA_OCCUPIED_POSITIVE)
    private Integer ocupadas;

    @NotNull(message = ValidationMessages.SHOWING_SCHEMA_AVAILABLE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SHOWING_SCHEMA_AVAILABLE_POSITIVE)
    private Integer disponibles;

    @NotNull(message = ValidationMessages.SHOWING_SCHEMA_MAINTENANCE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SHOWING_SCHEMA_MAINTENANCE_POSITIVE)
    private Integer mantenimiento;

    @NotNull(message = ValidationMessages.SHOWING_SCHEMA_SHOWING_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer funcionCodigo;
}
