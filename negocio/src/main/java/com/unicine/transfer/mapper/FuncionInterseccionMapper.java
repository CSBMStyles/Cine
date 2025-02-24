package com.unicine.transfer.mapper;

import org.springframework.stereotype.Component;

import com.unicine.entity.Funcion;
import com.unicine.transfer.data.FuncionInterseccionDTO;

@Component
public class FuncionInterseccionMapper {

    public FuncionInterseccionDTO convertirDTO(Funcion funcion) {
        return new FuncionInterseccionDTO(
            funcion.getSala().getNombre(),

            funcion.getPelicula().getNombre(), 

            funcion.getFormato(), 

            funcion.getPelicula().getImagenes(), 

            funcion.getPelicula().getGeneros(), 

            funcion.getHorario().getFechaInicio(), 

            funcion.getHorario().getFechaFin()
        );
    }
}
