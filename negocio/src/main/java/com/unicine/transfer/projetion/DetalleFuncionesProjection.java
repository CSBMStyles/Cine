package com.unicine.transfer.projetion;

import java.util.List;

import com.unicine.entity.showing.Horario;
import com.unicine.entity.image.Imagen;
import com.unicine.enums.movie.EstadoPelicula;

public interface DetalleFuncionesProjection {

    String getNombrePelicula();

    EstadoPelicula getEstadoPelicula();
    
    List<Imagen> getImagenes();

    Integer getCodigoSala();

    String getDireccionTeatro();

    String getNombreCiudad();
    
    Horario getHorario();
}
