package com.unicine.transfer.mapper;

import com.unicine.entity.user.Cliente;
import com.unicine.transfer.dto.request.ClienteRequest;
import com.unicine.transfer.dto.response.ClienteResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper entre la entidad {@link Cliente} y sus DTOs de transferencia.
 */
@Mapper(uses = ImagenMapper.class)
public interface ClienteMapper {

    ClienteResponse toResponse(Cliente cliente);

    List<ClienteResponse> toResponseList(List<Cliente> clientes);

    @Mapping(target = "compras", ignore = true)
    @Mapping(target = "cuponClientes", ignore = true)
    @Mapping(target = "colecciones", ignore = true)
    Cliente toEntity(ClienteRequest request);
}
