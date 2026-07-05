package com.unicine.transfer.dto.request;

import com.unicine.enums.movie.EstadoPelicula;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.movie.HistorialEstadoPelicula}.
 *
 * Incluido:
 * - {@code codigo}, {@code estadoAnterior}, {@code estadoNuevo}, {@code fechaCambio}.
 * - Identificadores de la disposicion: {@code peliculaCodigo}, {@code ciudadCodigo}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEstadoPeliculaRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotNull(message = ValidationMessages.FIELD_REQUIRED)
    private EstadoPelicula estadoAnterior;

    @NotNull(message = ValidationMessages.FIELD_REQUIRED)
    private EstadoPelicula estadoNuevo;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_HISTORY_DATE_NOT_NULL)
    private LocalDateTime fechaCambio;

    @NotNull(message = ValidationMessages.SHOWING_MOVIE_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer peliculaCodigo;

    @NotNull(message = ValidationMessages.THEATER_CITY_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer ciudadCodigo;
}
