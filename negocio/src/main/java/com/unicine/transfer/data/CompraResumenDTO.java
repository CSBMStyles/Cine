package com.unicine.transfer.data;

import com.unicine.enums.purchase.MedioPago;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * DTO para resumir la informacion basica de una compra
 * en consultas de listado y reportes internos.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CompraResumenDTO {

    private Integer codigo;

    private LocalDateTime fechaCompra;

    private Double valorTotal;

    private MedioPago medioPago;

    private String nombreCliente;

    private String nombrePelicula;

    private Boolean estado;
}
