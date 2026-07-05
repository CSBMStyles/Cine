package com.unicine.transfer.mapper;

import com.unicine.entity.movie.PeliculaDisposicion;
import com.unicine.transfer.dto.request.PeliculaDisposicionRequest;
import com.unicine.transfer.dto.response.PeliculaDisposicionResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link PeliculaDisposicion} y sus DTOs de transferencia.
 */
@Mapper(uses = {PeliculaMapper.class, CiudadMapper.class})
public interface PeliculaDisposicionMapper {

    PeliculaDisposicionResponse toResponse(PeliculaDisposicion peliculaDisposicion);

    List<PeliculaDisposicionResponse> toResponseList(List<PeliculaDisposicion> peliculaDisposiciones);

    @Mapping(target = "pelicula.codigo", source = "peliculaCodigo")
    @Mapping(target = "ciudad.codigo", source = "ciudadCodigo")
    PeliculaDisposicion toEntity(PeliculaDisposicionRequest request);
}
