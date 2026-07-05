package com.unicine.transfer.mapper;

import com.unicine.entity.showing.Funcion;
import com.unicine.transfer.dto.request.FuncionRequest;
import com.unicine.transfer.dto.response.FuncionResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Funcion} y sus DTOs de transferencia.
 */
@Mapper(uses = {SalaMapper.class, HorarioMapper.class, PeliculaMapper.class, FuncionEsquemaMapper.class})
public interface FuncionMapper {

    FuncionResponse toResponse(Funcion funcion);

    List<FuncionResponse> toResponseList(List<Funcion> funciones);

    @Mapping(target = "sala.codigo", source = "salaCodigo")
    @Mapping(target = "horario.codigo", source = "horarioCodigo")
    @Mapping(target = "pelicula.codigo", source = "peliculaCodigo")
    @Mapping(target = "compras", ignore = true)
    Funcion toEntity(FuncionRequest request);
}
