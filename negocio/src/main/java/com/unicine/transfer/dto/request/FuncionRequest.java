package com.unicine.transfer.dto.request;

import com.unicine.enums.movie.FormatoPelicula;
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

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.showing.Funcion}.
 *
 * Incluido:
 * - {@code codigo}, {@code precio}, {@code formato}.
 * - Identificadores de relaciones: {@code salaCodigo}, {@code horarioCodigo}, {@code peliculaCodigo}.
 *
 * Excluido:
 * - Esquema de funcion, entradas y compras: se gestionan por endpoints especificos.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuncionRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotNull(message = ValidationMessages.SHOWING_PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SHOWING_PRICE_POSITIVE_OR_ZERO)
    private Double precio;

    @NotNull(message = ValidationMessages.SHOWING_FORMAT_NOT_NULL)
    private FormatoPelicula formato;

    @NotNull(message = ValidationMessages.SHOWING_ROOM_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer salaCodigo;

    @NotNull(message = ValidationMessages.SHOWING_SCHEDULE_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer horarioCodigo;

    @NotNull(message = ValidationMessages.SHOWING_MOVIE_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer peliculaCodigo;
}
