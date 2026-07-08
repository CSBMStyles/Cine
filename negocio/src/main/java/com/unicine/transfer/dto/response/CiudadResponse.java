package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.theater.Ciudad}.
 *
 * Incluido:
 * - {@code codigo} y {@code nombre}.
 *
 * Excluido:
 * - Listas de teatros y disposiciones de peliculas para evitar
 *   ciclos de serializacion y cargas pesadas.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CiudadResponse {

    private Integer codigo;

    private String nombre;
}
