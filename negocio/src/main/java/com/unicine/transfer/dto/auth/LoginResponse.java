package com.unicine.transfer.dto.auth;

import com.unicine.enums.user.TipoUsuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para login sin JWT aun.
 * En 5.1 se añadirá token; hoy devuelve identidad verificada.
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

    private String mensaje;
}
