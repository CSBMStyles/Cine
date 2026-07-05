package com.unicine.transfer.dto.request;

import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.image.Imagen}.
 *
 * Incluido:
 * - {@code codigo} y {@code url}.
 *
 * Excluido:
 * - Relaciones con cliente, administrador, pelicula o confiteria:
 *   se asocian desde el endpoint correspondiente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenRequest {

    @NotBlank(message = ValidationMessages.IMAGE_CODE_NOT_BLANK)
    @Size(max = 50, message = ValidationMessages.IMAGE_CODE_NOT_BLANK)
    private String codigo;

    @NotBlank(message = ValidationMessages.IMAGE_URL_NOT_BLANK)
    @Size(max = 200, message = ValidationMessages.IMAGE_URL_NOT_BLANK)
    private String url;
}
