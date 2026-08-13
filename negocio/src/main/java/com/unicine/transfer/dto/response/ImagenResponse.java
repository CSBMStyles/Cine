package com.unicine.transfer.dto.response;

import com.unicine.enums.image.TipoImagen;

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
 * - {@code codigo}, {@code url}, {@code nombre}, {@code filePath}, {@code thumbnailUrl}.
 * - {@code fileType}, {@code altura}, {@code anchura}, {@code tamanio}.
 * - {@code versionId}, {@code versionName}.
 * - Identificacion del propietario sin anidar DTOs para evitar ciclos.
 *
 * Excluido:
 * - Entidades relacionadas completas para evitar ciclos de serializacion.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenResponse {

    private String codigo;

    private String nombre;

    private String url;

    private String filePath;

    private String thumbnailUrl;

    private String fileType;

    private Integer altura;

    private Integer anchura;

    private Long tamanio;

    private String versionId;

    private String versionName;

    private String tipoPropietario;

    private Integer codigoPropietario;

    private TipoImagen tipoImagen;

    private Integer orden;

    private Boolean principal;
}
