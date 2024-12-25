package com.unicine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DetalleCompraDTO {

    private Double valorTotal;

    private LocalDateTime fechaCompra;

    private Integer codigoFuncion;

    private Double preciosEntrada;

    private Double preciosConfiteria;

}
