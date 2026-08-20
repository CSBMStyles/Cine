package com.unicine.transfer.mapper;

import com.unicine.entity.theater.Ciudad;
import com.unicine.transfer.dto.request.CiudadRequest;
import com.unicine.transfer.dto.response.CiudadResponse;

import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper entre la entidad {@link Ciudad} y sus DTOs de transferencia.
 */
@Mapper
public interface CiudadMapper {

    CiudadResponse toResponse(Ciudad ciudad);

    List<CiudadResponse> toResponseList(List<Ciudad> ciudades);

    Ciudad toEntity(CiudadRequest request);
}
