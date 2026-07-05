package com.unicine.transfer.mapper;

import com.unicine.entity.confiteria.ConfiteriaPresentacion;
import com.unicine.transfer.dto.request.ConfiteriaPresentacionRequest;
import com.unicine.transfer.dto.response.ConfiteriaPresentacionResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link ConfiteriaPresentacion} y sus DTOs de transferencia.
 */
@Mapper(uses = ConfiteriaMapper.class)
public interface ConfiteriaPresentacionMapper {

    ConfiteriaPresentacionResponse toResponse(ConfiteriaPresentacion presentacion);

    List<ConfiteriaPresentacionResponse> toResponseList(List<ConfiteriaPresentacion> presentaciones);

    @Mapping(target = "confiteria.codigo", source = "confiteriaCodigo")
    ConfiteriaPresentacion toEntity(ConfiteriaPresentacionRequest request);
}
