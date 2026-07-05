package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.purchase.CuponCliente}.
 *
 * Incluido:
 * - {@code codigo}, {@code estado}.
 * - Cupon y cliente anidados.
 *
 * Excluido:
 * - Compra asociada para evitar ciclos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuponClienteResponse {

    private Integer codigo;

    private Boolean estado;

    private CuponResponse cupon;

    private ClienteResponse cliente;
}
