package com.unicine.transfer.mapper;

import com.unicine.entity.movie.Comentario;
import com.unicine.transfer.dto.request.ComentarioRequest;
import com.unicine.transfer.dto.response.ComentarioResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Comentario} y sus DTOs de transferencia.
 */
@Mapper(uses = {ClienteMapper.class, PeliculaMapper.class})
public interface ComentarioMapper {

    ComentarioResponse toResponse(Comentario comentario);

    List<ComentarioResponse> toResponseList(List<Comentario> comentarios);

    @Mapping(target = "cliente.cedula", source = "clienteCedula")
    @Mapping(target = "pelicula.codigo", source = "peliculaCodigo")
    Comentario toEntity(ComentarioRequest request);
}
