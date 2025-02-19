package com.unicine.transfer.projetion;

import java.util.List;

import com.unicine.entity.Horario;
import com.unicine.entity.Imagen;
import com.unicine.enumeration.EstadoPelicula;

public interface DetalleFuncionesProjection {

    String getNombrePelicula();

    EstadoPelicula getEstadoPelicula();
    
    List<Imagen> getImagenes();

    Integer getCodigoSala();

    String getDireccionTeatro();

    String getNombreCiudad();
    
    Horario getHorario();
}
