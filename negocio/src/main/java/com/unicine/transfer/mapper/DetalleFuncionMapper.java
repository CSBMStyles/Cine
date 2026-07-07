package com.unicine.transfer.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.unicine.transfer.dto.response.DetalleFuncionesResponse;
import com.unicine.transfer.projetion.DetalleFuncionesProjection;

@Component
public class DetalleFuncionMapper {

    private final ImagenMapper imagenMapper = Mappers.getMapper(ImagenMapper.class);

    private final HorarioMapper horarioMapper = Mappers.getMapper(HorarioMapper.class);

    public DetalleFuncionesResponse convertirDTO(DetalleFuncionesProjection projection) {
        return new DetalleFuncionesResponse(

            projection.getNombrePelicula(),

            imagenMapper.toResponseList(projection.getImagenes()),

            projection.getCodigoSala(),

            projection.getDireccionTeatro(),

            projection.getNombreCiudad(),
            
            horarioMapper.toResponse(projection.getHorario())
        );
    }
}
