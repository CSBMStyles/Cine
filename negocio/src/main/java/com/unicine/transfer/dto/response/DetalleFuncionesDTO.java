package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DetalleFuncionesDTO {

    private String nombrePelicula;

    private List<ImagenResponse> imagenes;

    private Integer codigoSala;

    private String direccionTeatro;

    private String nombreCiudad;

    private HorarioResponse horario;
}
