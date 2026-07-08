package com.unicine.transfer.dto.request;

import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.theater.Ciudad}.
 *
 * Incluido:
 * - {@code codigo}: opcional en registro, requerido en actualizacion.
 * - {@code nombre}: obligatorio.
 *
 * Excluido:
 * - Relaciones {@code teatros} y {@code peliculaDisposicion}: se gestionan
 *   por endpoints especificos de esas entidades.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CiudadRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.CITY_NAME_NOT_BLANK)
    @Size(min = 2, max = 100, message = ValidationMessages.CITY_NAME_SIZE_MIN_TWO)
    @Pattern(
        regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
        message = ValidationMessages.CITY_NAME_PATTERN
    )
    private String nombre;
}
