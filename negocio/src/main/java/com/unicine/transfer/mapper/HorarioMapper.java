package com.unicine.transfer.mapper;

import com.unicine.entity.showing.Horario;
import com.unicine.transfer.dto.request.HorarioRequest;
import com.unicine.transfer.dto.response.HorarioResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Horario} y sus DTOs de transferencia.
 */
@Mapper
public interface HorarioMapper {

    HorarioResponse toResponse(Horario horario);

    List<HorarioResponse> toResponseList(List<Horario> horarios);

    @Mapping(target = "funcion", ignore = true)
    Horario toEntity(HorarioRequest request);
}
