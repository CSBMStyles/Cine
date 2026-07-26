package com.unicine.transfer.dto.request;

import com.unicine.enums.image.TipoImagenPelicula;
import com.unicine.enums.image.TipoPropietarioImagen;
import com.unicine.util.validation.catalog.ValidationMessages;

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

/**
 * DTO de entrada para operaciones sobre {@link com.unicine.entity.image.Imagen}.
 *
 * Incluido:
 * - {@code codigo}, {@code nombre}.
 * - Identificacion del propietario: {@code tipoPropietario}, {@code codigoPropietario}.
 * - {@code tipoImagenPelicula} (opcional, solo para peliculas).
 *
 * Excluido:
 * - URL y demas metadatos: son devueltos por ImageKit y no deben entrar por API.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenRequest {

    @NotBlank(message = ValidationMessages.IMAGE_CODE_NOT_BLANK)
    @Size(max = 50, message = ValidationMessages.IMAGE_CODE_NOT_BLANK)
    private String codigo;

    @Size(max = 100, message = ValidationMessages.IMAGE_NAME_SIZE_MAX_HUNDRED)
    private String nombre;

    @NotNull(message = ValidationMessages.IMAGE_OWNER_TYPE_NOT_NULL)
    private TipoPropietarioImagen tipoPropietario;

    @NotNull(message = ValidationMessages.IMAGE_OWNER_ID_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigoPropietario;

    private TipoImagenPelicula tipoImagenPelicula;
}
