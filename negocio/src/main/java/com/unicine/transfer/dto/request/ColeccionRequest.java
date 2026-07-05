package com.unicine.transfer.dto.request;

import com.unicine.enums.movie.EstadoPropio;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.movie.Coleccion}.
 *
 * Incluido:
 * - {@code puntuacion}, {@code estadoPeliculaPropio}, {@code notificacionActiva}.
 * - Identificadores de relaciones: {@code clienteCedula}, {@code peliculaCodigo}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColeccionRequest {

    @Max(value = 5, message = ValidationMessages.MOVIE_RATING_MAX_FIVE)
    @Positive(message = ValidationMessages.MOVIE_RATING_POSITIVE)
    private Double puntuacion;

    private EstadoPropio estadoPeliculaPropio;

    @NotNull(message = ValidationMessages.FIELD_NULL)
    private Boolean notificacionActiva;

    @NotNull(message = ValidationMessages.CLIENT_COUPON_CLIENT_NOT_NULL)
    @Positive(message = ValidationMessages.CEDULA_POSITIVE)
    private Integer clienteCedula;

    @NotNull(message = ValidationMessages.SHOWING_MOVIE_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer peliculaCodigo;
}
