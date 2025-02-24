package com.unicine.transfer.data;

import java.time.LocalDateTime;
import java.util.List;

import com.unicine.entity.Imagen;
import com.unicine.enumeration.FormatoPelicula;
import com.unicine.enumeration.GeneroPelicula;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FuncionInterseccionDTO {

    private String nombreSala;

    private String nombrePelicula;

    private FormatoPelicula formato;

    private List<Imagen> imagenes;

    private List<GeneroPelicula> generos;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

}
