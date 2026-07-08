package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.image.Imagen}.
 *
 * Incluido:
 * - {@code codigo} y {@code url}.
 *
 * Excluido:
 * - Entidades relacionadas para evitar ciclos de serializacion.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenResponse {

    private String codigo;

    private String url;
}
