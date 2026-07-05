package com.unicine.transfer.dto.request;

import com.unicine.enums.movie.EstadoPelicula;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.movie.PeliculaDisposicion}.
 *
 * Incluido:
 * - {@code estadoPelicula}, {@code fechaFuncionInicial}.
 * - Identificadores de relaciones: {@code peliculaCodigo}, {@code ciudadCodigo}.
 *
 * Excluido:
 * - Historial de estados: se gestiona desde el endpoint correspondiente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeliculaDisposicionRequest {

    @NotNull(message = ValidationMessages.FIELD_REQUIRED)
    private EstadoPelicula estadoPelicula;

    private LocalDateTime fechaFuncionInicial;

    @NotNull(message = ValidationMessages.SHOWING_MOVIE_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer peliculaCodigo;

    @NotNull(message = ValidationMessages.THEATER_CITY_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer ciudadCodigo;
}
