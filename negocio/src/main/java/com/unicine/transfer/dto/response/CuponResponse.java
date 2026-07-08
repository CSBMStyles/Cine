package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.purchase.Cupon}.
 *
 * Incluido:
 * - {@code codigo}, {@code descripcion}, {@code descuento}, {@code criterio}, {@code fechaVencimiento}.
 *
 * Excluido:
 * - Cupones de clientes para evitar ciclos.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuponResponse {

    private Integer codigo;

    private String descripcion;

    private Double descuento;

    private String criterio;

    private LocalDateTime fechaVencimiento;
}
