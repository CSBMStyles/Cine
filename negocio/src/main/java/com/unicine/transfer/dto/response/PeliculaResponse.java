package com.unicine.transfer.dto.response;

import com.unicine.enums.movie.GeneroPelicula;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.movie.Pelicula}.
 *
 * Incluido:
 * - {@code codigo}, {@code generos}, {@code nombre}, {@code repartos}, {@code sinopsis}.
 * - {@code urlTrailer}, {@code puntuacion}, {@code restriccionEdad}, {@code imagenes}.
 *
 * Excluido:
 * - Funciones, colecciones y disposiciones para evitar ciclos.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeliculaResponse {

    private Integer codigo;

    private List<GeneroPelicula> generos;

    private String nombre;

    private Map<String, String> repartos;

    private String sinopsis;

    private String urlTrailer;

    private Double puntuacion;

    private Integer restriccionEdad;

    private List<ImagenResponse> imagenes;
}
