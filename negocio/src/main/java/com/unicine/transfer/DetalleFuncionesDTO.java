package com.unicine.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.unicine.entity.Horario;
import com.unicine.enumeration.EstadoPelicula;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DetalleFuncionesDTO {

    private String nombrePelicula;

    private EstadoPelicula estadoPelicula;

    // IMPORTANT: En la entidad construimos el atributo imagenes como un Map, pero al momento de consultar no me trae el dato como un Map, sino como un String trayendo el valor y no la key, por lo que se debe cambiar el tipo de dato en el DTO si queremos que funcione la consulta
    private String imagenesPelicula;

    private Integer codigoSala;

    private String direccionTeatro;

    private String nombreCiudad;

    private Horario horario;

}
