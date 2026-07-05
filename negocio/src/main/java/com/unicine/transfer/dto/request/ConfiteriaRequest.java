package com.unicine.transfer.dto.request;

import com.unicine.enums.confiteria.CategoriaConfiteria;
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

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.confiteria.Confiteria}.
 *
 * Incluido:
 * - {@code codigo}, {@code nombre}, {@code descripcion}, {@code categoria}.
 *
 * Excluido:
 * - Presentaciones e imagenes: se gestionan por endpoints especificos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiteriaRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.CONFECTIONERY_NAME_NOT_BLANK)
    @Size(max = 100, message = ValidationMessages.CONFECTIONERY_NAME_SIZE_MAX_HUNDRED)
    private String nombre;

    @Size(max = 500, message = ValidationMessages.CONFECTIONERY_DESCRIPTION_SIZE_MAX_FIVE_HUNDRED)
    private String descripcion;

    @NotNull(message = ValidationMessages.CONFECTIONERY_CATEGORY_NOT_NULL)
    private CategoriaConfiteria categoria;
}
