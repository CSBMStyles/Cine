package com.unicine.transfer.projetion;

import java.time.LocalDateTime;
import java.util.List;

import com.unicine.entity.Imagen;
import com.unicine.enumeration.FormatoPelicula;
import com.unicine.enumeration.GeneroPelicula;

public interface FuncionInterseccionProjection {

    String getNombreSala();

    String getNombrePelicula();

    FormatoPelicula getFormato();

    List<Imagen> getImagen();

    List<GeneroPelicula> getGeneros();

    LocalDateTime getFechaInicio();

    LocalDateTime getFechaFin();
}
