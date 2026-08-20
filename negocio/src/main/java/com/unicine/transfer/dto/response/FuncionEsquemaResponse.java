package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.showing.FuncionEsquema}.
 *
 * Incluido:
 * - {@code codigo}, {@code esquemaTemporal}, {@code ocupadas}, {@code disponibles}, {@code mantenimiento}.
 * - Identificador de la funcion asociada.
 *
 * Excluido:
 * - Funcion completa para evitar ciclos con {@link FuncionResponse}.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuncionEsquemaResponse {

    private Integer codigo;

    private String esquemaTemporal;

    private Integer ocupadas;

    private Integer disponibles;

    private Integer mantenimiento;

    private Integer funcionCodigo;
}
