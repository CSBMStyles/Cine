package com.unicine.transfer.dto.response;

import com.unicine.enums.movie.EstadoPelicula;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.movie.HistorialEstadoPelicula}.
 *
 * Incluido:
 * - {@code codigo}, {@code estadoAnterior}, {@code estadoNuevo}, {@code fechaCambio}.
 * - Disposicion de pelicula anidada.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEstadoPeliculaResponse {

    private Integer codigo;

    private EstadoPelicula estadoAnterior;

    private EstadoPelicula estadoNuevo;

    private LocalDateTime fechaCambio;

    private PeliculaDisposicionResponse peliculaDisposicion;
}
