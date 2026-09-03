package com.unicine.transfer.dto.auth;

import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de entrada para login.
 * Un solo formulario para los 3 perfiles — el tipo lo resuelve AuthenticationService.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = ValidationMessages.EMAIL_NOT_NULL)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    private String correo;

    @NotBlank(message = ValidationMessages.PASSWORD_NOT_BLANK)
    @Size(min = 8, max = 200, message = ValidationMessages.PASSWORD_SIZE_MIN_EIGHT)
    private String password;
}
