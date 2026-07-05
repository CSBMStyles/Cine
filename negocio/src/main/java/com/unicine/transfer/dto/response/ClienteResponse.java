package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.user.Cliente}.
 *
 * Incluido:
 * - Datos personales sin password.
 * - Datos especificos: estado, fechaNacimiento, telefonos, imagen.
 *
 * Excluido:
 * - {@code password} por seguridad.
 * - Compras, cupones y colecciones para evitar ciclos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponse {

    private Integer cedula;

    private String nombre;

    private String apellido;

    private String correo;

    private Boolean estado;

    private LocalDate fechaNacimiento;

    private List<String> telefonos;

    private ImagenResponse imagen;
}
