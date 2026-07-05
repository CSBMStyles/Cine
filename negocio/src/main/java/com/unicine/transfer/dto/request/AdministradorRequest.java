package com.unicine.transfer.dto.request;

import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.Email;
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

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.user.Administrador}.
 *
 * Incluido:
 * - Datos personales: cedula, nombre, apellido, correo, password.
 *
 * Excluido:
 * - Imagen: se gestiona por endpoint especifico.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorRequest {

    @NotNull(message = ValidationMessages.CEDULA_NOT_NULL)
    @Positive(message = ValidationMessages.CEDULA_POSITIVE)
    private Integer cedula;

    @NotBlank(message = ValidationMessages.NAME_NOT_BLANK)
    @Size(max = 50, message = ValidationMessages.NAME_SIZE_MAX_FIFTY)
    private String nombre;

    @NotBlank(message = ValidationMessages.NAME_NOT_BLANK)
    @Size(max = 50, message = ValidationMessages.NAME_SIZE_MAX_FIFTY)
    private String apellido;

    @NotBlank(message = ValidationMessages.EMAIL_NOT_NULL)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    @Size(max = 150, message = ValidationMessages.EMAIL_SIZE_MAX_ONE_HUNDRED_FIFTY)
    private String correo;

    @NotBlank(message = ValidationMessages.PASSWORD_NOT_BLANK)
    @Size(min = 8, max = 200, message = ValidationMessages.PASSWORD_SIZE_MIN_EIGHT)
    @Pattern(regexp = "^(?=.*[A-Z]).+$", message = ValidationMessages.PASSWORD_UPPERCASE)
    @Pattern(regexp = "^(?=.*[a-z]).+$", message = ValidationMessages.PASSWORD_LOWERCASE)
    @Pattern(regexp = "^(?=.*\\d).+$", message = ValidationMessages.PASSWORD_DIGIT)
    @Pattern(regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$", message = ValidationMessages.PASSWORD_SPECIAL)
    private String password;
}
