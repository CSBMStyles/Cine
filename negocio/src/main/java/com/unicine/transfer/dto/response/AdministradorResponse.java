package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.user.Administrador}.
 *
 * Incluido:
 * - Datos personales sin password.
 * - Imagen asociada.
 *
 * Excluido:
 * - {@code password} por seguridad.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorResponse {

    private Integer cedula;

    private String nombre;

    private String apellido;

    private String correo;

    private ImagenResponse imagen;
}
