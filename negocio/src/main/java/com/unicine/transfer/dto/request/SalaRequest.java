package com.unicine.transfer.dto.request;

import com.unicine.enums.theater.TipoSala;
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
 * DTO de entrada para la entidad {@link com.unicine.entity.theater.Sala}.
 *
 * Incluido:
 * - {@code codigo}, {@code nombre}, {@code tipoSala}.
 * - Identificadores de relaciones: {@code teatroCodigo}, {@code distribucionSillaCodigo}.
 *
 * Excluido:
 * - Lista de funciones: se gestiona desde el endpoint de funciones.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.ROOM_NAME_NOT_BLANK)
    @Size(max = 100, message = ValidationMessages.NAME_SIZE_MAX_HUNDRED)
    private String nombre;

    @NotNull(message = ValidationMessages.ROOM_TYPE_NOT_NULL)
    private TipoSala tipoSala;

    @NotNull(message = ValidationMessages.ROOM_THEATER_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer teatroCodigo;

    @NotNull(message = ValidationMessages.ROOM_DISTRIBUTION_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer distribucionSillaCodigo;
}
