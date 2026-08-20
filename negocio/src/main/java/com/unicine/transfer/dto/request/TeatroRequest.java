package com.unicine.transfer.dto.request;

import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
 * DTO de entrada para la entidad {@link com.unicine.entity.theater.Teatro}.
 *
 * Incluido:
 * - {@code codigo}, {@code direccion}, {@code telefono}.
 * - Identificadores de relaciones: {@code ciudadCodigo}, {@code administradorTeatroCedula}.
 *
 * Excluido:
 * - Lista de salas: se gestiona desde el endpoint de salas.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeatroRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.ADDRESS_NOT_BLANK)
    @Size(min = 4, max = 100, message = ValidationMessages.ADDRESS_SIZE_MIN_FOUR)
    private String direccion;

    @NotBlank(message = ValidationMessages.THEATER_PHONE_NOT_BLANK)
    @Pattern(regexp = "^.{10}$", message = ValidationMessages.PHONE_SIZE_EXACT_TEN)
    private String telefono;

    @NotNull(message = ValidationMessages.THEATER_CITY_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer ciudadCodigo;

    @NotNull(message = ValidationMessages.THEATER_ADMIN_NOT_NULL)
    @Positive(message = ValidationMessages.CEDULA_POSITIVE)
    private Integer administradorTeatroCedula;
}
