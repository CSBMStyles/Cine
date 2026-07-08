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
 * DTO de salida para la entidad {@link com.unicine.entity.movie.PeliculaDisposicion}.
 *
 * Incluido:
 * - {@code estadoPelicula}, {@code fechaFuncionInicial}.
 * - Pelicula y ciudad anidados.
 *
 * Excluido:
 * - Historial de estados para evitar ciclos.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeliculaDisposicionResponse {

    private EstadoPelicula estadoPelicula;

    private LocalDateTime fechaFuncionInicial;

    private PeliculaResponse pelicula;

    private CiudadResponse ciudad;
}
