package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.purchase.CompraConfiteria}.
 *
 * Incluido:
 * - {@code codigo}, {@code precio}, {@code unidades}.
 * - Presentacion de confiteria anidada.
 *
 * Excluido:
 * - Compra asociada para evitar ciclos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraConfiteriaResponse {

    private Integer codigo;

    private Double precio;

    private Integer unidades;

    private ConfiteriaPresentacionResponse presentacion;
}
