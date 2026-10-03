package com.unicine.transfer.dto.auth;

import com.unicine.enums.user.TipoUsuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para login con access token JWT (5.1.1).
 * El refresh token llega en 5.1.3.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Integer cedula;

    private String nombre;

    private String correo;

    private TipoUsuario tipo;

    private java.util.List<Integer> teatroIds;

    private String accessToken;

    private String refreshToken;

    private String mensaje;
}
