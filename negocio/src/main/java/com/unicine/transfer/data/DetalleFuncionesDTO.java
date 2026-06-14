package com.unicine.transfer.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import com.unicine.entity.showing.Horario;
import com.unicine.entity.image.Imagen;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DetalleFuncionesDTO {

    private String nombrePelicula;

    private List<Imagen> imagenes;

    private Integer codigoSala;

    private String direccionTeatro;

    private String nombreCiudad;

    private Horario horario;
}
