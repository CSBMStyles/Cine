package com.unicine.transfer.mapper;

import com.unicine.entity.movie.Pelicula;
import com.unicine.transfer.dto.request.PeliculaRequest;
import com.unicine.transfer.dto.response.PeliculaResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Pelicula} y sus DTOs de transferencia.
 */
@Mapper(uses = ImagenMapper.class)
public interface PeliculaMapper {

    PeliculaResponse toResponse(Pelicula pelicula);

    List<PeliculaResponse> toResponseList(List<Pelicula> peliculas);

    @Mapping(target = "funciones", ignore = true)
    @Mapping(target = "colecccion", ignore = true)
    @Mapping(target = "peliculaDisposicion", ignore = true)
    Pelicula toEntity(PeliculaRequest request);
}
