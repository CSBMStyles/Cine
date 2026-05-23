package com.unicine.transfer.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO para representar un item de confiteria dentro de una compra.
 * Util en reportes internos y calculos de subtotal por item.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CompraConfiteriaDTO {

    private String nombreConfiteria;

    private Double precio;

    private Integer unidades;

    private Double subtotal;
}
