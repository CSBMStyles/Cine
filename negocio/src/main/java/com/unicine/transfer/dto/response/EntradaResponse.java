package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.purchase.Entrada}.
 *
 * Incluido:
 * - {@code codigo}, {@code precio}, {@code fila}, {@code columna}.
 * - Funcion anidada.
 *
 * Excluido:
 * - Compra asociada para evitar ciclos.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntradaResponse {

    private Integer codigo;

    private Double precio;

    private Integer fila;

    private Integer columna;

    private FuncionResponse funcion;
}
