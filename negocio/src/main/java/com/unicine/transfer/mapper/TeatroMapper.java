package com.unicine.transfer.mapper;

import com.unicine.entity.theater.Teatro;
import com.unicine.transfer.dto.request.TeatroRequest;
import com.unicine.transfer.dto.response.TeatroResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Teatro} y sus DTOs de transferencia.
 */
@Mapper(uses = {CiudadMapper.class, AdministradorTeatroMapper.class})
public interface TeatroMapper {

    TeatroResponse toResponse(Teatro teatro);

    List<TeatroResponse> toResponseList(List<Teatro> teatros);

    @Mapping(target = "ciudad.codigo", source = "ciudadCodigo")
    @Mapping(target = "administradorTeatro.cedula", source = "administradorTeatroCedula")
    @Mapping(target = "salas", ignore = true)
    Teatro toEntity(TeatroRequest request);
}
