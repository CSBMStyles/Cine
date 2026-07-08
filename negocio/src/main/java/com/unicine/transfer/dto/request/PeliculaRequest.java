package com.unicine.transfer.dto.request;

import com.unicine.enums.movie.GeneroPelicula;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.movie.Pelicula}.
 *
 * Incluido:
 * - {@code codigo}, {@code generos}, {@code nombre}, {@code repartos}, {@code sinopsis}.
 * - {@code urlTrailer}, {@code puntuacion}, {@code restriccionEdad}.
 *
 * Excluido:
 * - Funciones, colecciones, disposiciones e imagenes: se gestionan por endpoints especificos.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeliculaRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotNull(message = ValidationMessages.FIELD_NULL)
    private List<GeneroPelicula> generos;

    @NotBlank(message = ValidationMessages.MOVIE_NAME_NOT_BLANK)
    @Size(max = 100, message = ValidationMessages.MOVIE_NAME_SIZE_MAX_HUNDRED)
    private String nombre;

    private Map<@Size(max = 150, message = ValidationMessages.MOVIE_ACTOR_ROLE_SIZE_MAX_ONE_HUNDRED_FIFTY) String,
                @Size(max = 150, message = ValidationMessages.MOVIE_ACTOR_NAME_SIZE_MAX_ONE_HUNDRED_FIFTY) String> repartos;

    @NotBlank(message = ValidationMessages.MOVIE_SYNOPSIS_NOT_BLANK)
    private String sinopsis;

    @Size(max = 200, message = ValidationMessages.MOVIE_TRAILER_URL_SIZE_MAX_TWO_HUNDRED)
    private String urlTrailer;

    @NotNull(message = ValidationMessages.VALUE_NOT_NULL)
    @Positive(message = ValidationMessages.MOVIE_RATING_POSITIVE)
    @Max(value = 5, message = ValidationMessages.MOVIE_RATING_MAX_FIVE)
    private Double puntuacion;

    @Positive(message = ValidationMessages.MOVIE_AGE_RESTRICTION_POSITIVE)
    @Max(value = 30, message = ValidationMessages.MOVIE_AGE_RESTRICTION_MAX_THIRTY)
    private Integer restriccionEdad;
}
