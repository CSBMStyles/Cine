package com.unicine.transfer.mapper;

import com.unicine.entity.user.Administrador;
import com.unicine.transfer.dto.request.AdministradorRequest;
import com.unicine.transfer.dto.response.AdministradorResponse;

import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper entre la entidad {@link Administrador} y sus DTOs de transferencia.
 */
@Mapper(uses = ImagenMapper.class)
public interface AdministradorMapper {

    AdministradorResponse toResponse(Administrador administrador);

    List<AdministradorResponse> toResponseList(List<Administrador> administradores);

    Administrador toEntity(AdministradorRequest request);
}
