package com.unicine.transfer.mapper;

import com.unicine.entity.movie.HistorialEstadoPelicula;
import com.unicine.transfer.dto.request.HistorialEstadoPeliculaRequest;
import com.unicine.transfer.dto.response.HistorialEstadoPeliculaResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link HistorialEstadoPelicula} y sus DTOs de transferencia.
 */
@Mapper(uses = PeliculaDisposicionMapper.class)
public interface HistorialEstadoPeliculaMapper {

    HistorialEstadoPeliculaResponse toResponse(HistorialEstadoPelicula historial);

    List<HistorialEstadoPeliculaResponse> toResponseList(List<HistorialEstadoPelicula> historiales);

    @Mapping(target = "peliculaDisposicion.pelicula.codigo", source = "peliculaCodigo")
    @Mapping(target = "peliculaDisposicion.ciudad.codigo", source = "ciudadCodigo")
    HistorialEstadoPelicula toEntity(HistorialEstadoPeliculaRequest request);
}
