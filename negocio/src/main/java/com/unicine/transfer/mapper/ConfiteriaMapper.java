package com.unicine.transfer.mapper;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.transfer.dto.request.ConfiteriaRequest;
import com.unicine.transfer.dto.response.ConfiteriaResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Confiteria} y sus DTOs de transferencia.
 */
@Mapper(uses = ImagenMapper.class)
public interface ConfiteriaMapper {

    ConfiteriaResponse toResponse(Confiteria confiteria);

    List<ConfiteriaResponse> toResponseList(List<Confiteria> confiterias);

    @Mapping(target = "presentaciones", ignore = true)
    Confiteria toEntity(ConfiteriaRequest request);
}
