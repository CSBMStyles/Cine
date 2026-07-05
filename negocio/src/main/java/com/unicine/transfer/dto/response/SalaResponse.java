package com.unicine.transfer.dto.response;

import com.unicine.enums.theater.TipoSala;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.theater.Sala}.
 *
 * Incluido:
 * - {@code codigo}, {@code nombre}, {@code tipoSala}.
 * - Teatro y distribucion de sillas anidados.
 *
 * Excluido:
 * - Lista de funciones para evitar ciclos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaResponse {

    private Integer codigo;

    private String nombre;

    private TipoSala tipoSala;

    private TeatroResponse teatro;

    private DistribucionSillaResponse distribucionSilla;
}
