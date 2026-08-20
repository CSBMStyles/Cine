package com.unicine.transfer.dto.response;

import com.unicine.enums.movie.FormatoPelicula;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.showing.Funcion}.
 *
 * Incluido:
 * - {@code codigo}, {@code precio}, {@code formato}.
 * - Sala, horario, pelicula y esquema de funcion anidados.
 *
 * Excluido:
 * - Compras para evitar ciclos.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuncionResponse {

    private Integer codigo;

    private Double precio;

    private FormatoPelicula formato;

    private SalaResponse sala;

    private HorarioResponse horario;

    private PeliculaResponse pelicula;

    private FuncionEsquemaResponse funcionEsquema;
}
