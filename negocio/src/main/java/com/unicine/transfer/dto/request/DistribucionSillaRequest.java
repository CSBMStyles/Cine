package com.unicine.transfer.dto.request;

import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.theater.DistribucionSilla}.
 *
 * Incluido:
 * - {@code codigo}: opcional en registro.
 * - {@code esquema}, {@code totalSillas}, {@code filas}, {@code columnas}.
 *
 * Excluido:
 * - {@code salas}: se gestionan desde el endpoint de salas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistribucionSillaRequest {

    @PositiveOrZero(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.SEAT_SCHEMA_NOT_BLANK)
    private String esquema;

    @NotNull(message = ValidationMessages.SEAT_TOTAL_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SEAT_TOTAL_POSITIVE)
    private Integer totalSillas;

    @NotNull(message = ValidationMessages.SEAT_ROWS_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SEAT_ROWS_POSITIVE)
    private Integer filas;

    @NotNull(message = ValidationMessages.SEAT_COLUMNS_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SEAT_COLUMNS_POSITIVE)
    private Integer columnas;
}
