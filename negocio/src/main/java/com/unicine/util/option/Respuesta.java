package com.unicine.util.option;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Respuesta<T> {

    private String mensaje;

    private T data;

    private boolean exito;
}
