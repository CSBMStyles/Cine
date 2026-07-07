package com.unicine.transfer.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.unicine.entity.showing.Funcion;
import com.unicine.transfer.dto.response.FuncionInterseccionResponse;

@Component
public class FuncionInterseccionMapper {

    private final ImagenMapper imagenMapper = Mappers.getMapper(ImagenMapper.class);

    public FuncionInterseccionResponse convertirDTO(Funcion funcion) {
        return new FuncionInterseccionResponse(
            funcion.getSala().getNombre(),

            funcion.getPelicula().getNombre(), 

            funcion.getFormato(), 

            imagenMapper.toResponseList(funcion.getPelicula().getImagenes()), 

            funcion.getPelicula().getGeneros(), 

            funcion.getHorario().getFechaInicio(), 

            funcion.getHorario().getFechaFin()
        );
    }
}
