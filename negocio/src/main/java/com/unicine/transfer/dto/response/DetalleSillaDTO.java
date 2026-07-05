package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DetalleSillaDTO {

    private Integer codigoEntrada;

    private Integer filaEntrada;

    private Integer columnaEntrada;
}
