package com.unicine.transfer.mapper;

import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.transfer.dto.request.AdministradorTeatroRequest;
import com.unicine.transfer.dto.response.AdministradorTeatroResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link AdministradorTeatro} y sus DTOs de transferencia.
 */
@Mapper(uses = ImagenMapper.class)
public interface AdministradorTeatroMapper {

    AdministradorTeatroResponse toResponse(AdministradorTeatro administradorTeatro);

    List<AdministradorTeatroResponse> toResponseList(List<AdministradorTeatro> administradoresTeatro);

    @Mapping(target = "teatros", ignore = true)
    AdministradorTeatro toEntity(AdministradorTeatroRequest request);
}
