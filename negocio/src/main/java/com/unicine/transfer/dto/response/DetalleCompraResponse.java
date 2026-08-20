package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DetalleCompraResponse {

    private Double valorTotal;

    private LocalDateTime fechaCompra;

    private Integer codigoFuncion;

    private Double preciosEntrada;

    private Double preciosConfiteria;

}
