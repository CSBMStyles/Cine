package com.unicine.transfer.dto.response;

import com.unicine.enums.movie.EstadoPropio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.movie.Coleccion}.
 *
 * Incluido:
 * - {@code puntuacion}, {@code estadoPeliculaPropio}, {@code notificacionActiva}.
 * - Cliente y pelicula anidados.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColeccionResponse {

    private Double puntuacion;

    private EstadoPropio estadoPeliculaPropio;

    private Boolean notificacionActiva;

    private ClienteResponse cliente;

    private PeliculaResponse pelicula;
}
