package com.unicine.transfer.mapper;

import com.unicine.entity.theater.Sala;
import com.unicine.transfer.dto.request.SalaRequest;
import com.unicine.transfer.dto.response.SalaResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Sala} y sus DTOs de transferencia.
 */
@Mapper(uses = {TeatroMapper.class, DistribucionSillaMapper.class})
public interface SalaMapper {

    SalaResponse toResponse(Sala sala);

    List<SalaResponse> toResponseList(List<Sala> salas);

    @Mapping(target = "teatro.codigo", source = "teatroCodigo")
    @Mapping(target = "distribucionSilla.codigo", source = "distribucionSillaCodigo")
    @Mapping(target = "funciones", ignore = true)
    Sala toEntity(SalaRequest request);
}
