package com.unicine.transfer.mapper;

import com.unicine.entity.movie.Coleccion;
import com.unicine.transfer.dto.request.ColeccionRequest;
import com.unicine.transfer.dto.response.ColeccionResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Coleccion} y sus DTOs de transferencia.
 */
@Mapper(uses = {ClienteMapper.class, PeliculaMapper.class})
public interface ColeccionMapper {

    ColeccionResponse toResponse(Coleccion coleccion);

    List<ColeccionResponse> toResponseList(List<Coleccion> colecciones);

    @Mapping(target = "cliente.cedula", source = "clienteCedula")
    @Mapping(target = "pelicula.codigo", source = "peliculaCodigo")
    Coleccion toEntity(ColeccionRequest request);
}
