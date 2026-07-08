package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.theater.Teatro}.
 *
 * Incluido:
 * - {@code codigo}, {@code direccion}, {@code telefono}.
 * - Ciudad y administrador de teatro anidados.
 *
 * Excluido:
 * - Lista de salas para evitar ciclos.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeatroResponse {

    private Integer codigo;

    private String direccion;

    private String telefono;

    private CiudadResponse ciudad;

    private AdministradorTeatroResponse administradorTeatro;
}
